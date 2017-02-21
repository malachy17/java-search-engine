import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that contains both a map of queries to SearchResult objects and an
 * index that is used as the database to search the queries in. It also contains
 * a parseQuery method to parse the given query, find all valid locations in the
 * InvertedIndex and fill up the map with queries and their respective list of
 * SearchResult objects.
 */
public class MultiQueryHelper implements QueryHelperInterface {

	private static final Logger logger = LogManager.getLogger();
	private final ReadWriteLock lock;

	private final WorkQueue minions;

	// A map to store query searches to a list of SearchResult objects.
	private final TreeMap<String, ArrayList<SearchResult>> map;

	// The inverted index of all words found in all files.
	// Changed reference from multi to normal to work with driver.
	private final MultiInvertedIndex index;

	public MultiQueryHelper(MultiInvertedIndex index, WorkQueue minions) {
		this.lock = new ReadWriteLock();
		this.minions = minions;

		this.index = index;
		map = new TreeMap<>();
	}

	/**
	 * Goes through each query line, cleans, rearranges the words. Sends those
	 * cleaned words to the exactSearch method. Gets the list from exactSearch,
	 * puts it in a map with the query as the key. Returns the map.
	 * 
	 * @param file
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public void parseQuery(Path file, boolean exact) throws IOException {

		String line = null;

		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));) {
			while ((line = reader.readLine()) != null) {
				minions.execute(new Minion(line, exact));
			}
		}
		minions.finish();
	}

	/**
	 * Sends a path and a map of queries to be printed in JSON by the JSONWriter
	 * class.
	 * 
	 * @param output
	 *            the location that the map in JSON will be printed to.
	 * @throws IOException
	 */
	public void toJSON(Path output) throws IOException {
		lock.lockReadOnly(); // read lock, since only reading shared data.
		JSONWriter.writeSearchResults(output, map);
		lock.unlockReadOnly();
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link Minion} is created to handle that subdirectory.
	 */
	private class Minion implements Runnable {

		private boolean exact;
		private String line;
		private String[] words;

		public Minion(String line, boolean exact) {
			logger.debug("Minion created for {}", line);
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			try {
				// Put these in minion instead of parseQuery, because we want
				// each minion to do the work.
				line = InvertedIndexBuilderInterface.clean(line);
				words = line.split("\\s+");
				Arrays.sort(words);
				line = String.join(" ", words);

				// Efficiency issue fixed where search was inside put()
				// inside lock, also fixed duplicate code with ternary operator.
				ArrayList<SearchResult> current = (exact) ? index.exactSearch(words) : index.partialSearch(words);
				lock.lockReadWrite();
				map.put(line, current);
				lock.unlockReadWrite();

			} catch (Exception e) {
				logger.warn("Unable to parse {}", line);
				logger.catching(Level.DEBUG, e);
			}

			logger.debug("Minion finished {}", line);
		}
	}
}