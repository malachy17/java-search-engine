import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Goes through a given directory and documents all words in every text file
 * within that directory. Documents by storing each word, its file, and its
 * position in the file into an InvertedIndex class.
 */
public class Driver {

	/**
	 * Takes in a "-dir" flag and a following directory, and starts traversing
	 * within said directory. Takes in an optional "-index" flag and a following
	 * path representing the location as to where the JSONWriter will output
	 * it's data. If and "-index" flag without a following path is provided, the
	 * JSON data will be printed at the default path: "index.json". If no
	 * "-index" flag is provided, the InvertedIndex's data will not be printed
	 * in JSON at all.
	 * 
	 * Can also take in the following optional flags and following arguments.
	 * 
	 * "-exact" : Searches the index for the exact query or queries entered by
	 * the user.
	 * 
	 * "-query" : Searches the index for words with the query or queries entered
	 * by the user as prefixes.
	 * 
	 * "-results" : Prints out a map of which files words in each query can be
	 * found in, in order of relevance.
	 * 
	 * @param args
	 *            the flags and corresponding data used in creating and
	 *            utilizing an InvertedIndex
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		WorkQueue queue = null;
		InvertedIndex index = null;

		QueryHelperInterface query = null;
		InvertedIndexBuilderInterface builder = null;
		WebCrawlerInterface crawler = null;

		if (parser.hasFlag("-multi")) {

			int threads = 5;
			threads = parser.getValue("-multi", threads);
			threads = threads < 1 ? 1 : threads;
			queue = new WorkQueue(threads);

			MultiInvertedIndex multi = new MultiInvertedIndex();
			index = multi;

			query = new MultiQueryHelper(multi, queue);
			builder = new MultiInvertedIndexBuilder(multi, queue);
			crawler = new MultiWebCrawler(multi, queue);

		} else {
			index = new InvertedIndex();

			query = new QueryHelper(index);
			builder = new InvertedIndexBuilder(index);
			crawler = new WebCrawler(index);
		}

		if (parser.hasFlag("-dir")) {
			try {
				Path path = Paths.get(parser.getValue("-dir"));
				builder.traverse(path);
			} catch (IOException e) {
				System.err.println("-dir: Unable to traverse path.");
			} catch (NullPointerException e) {
				System.err.println("Enter a directory after the \"dir\" flag.");
			}
		}

		if (parser.hasFlag("-url")) {
			try {
				String url = parser.getValue("-url");
				crawler.addSeed(url);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		if (parser.hasFlag("-index")) {
			try {
				Path outFile = Paths.get(parser.getValue("-index", "index.json"));
				index.toJSON(outFile);
			} catch (Exception e) {
				System.err.println("-index: Unable to write to path + outputFile.");
			}
		}

		if (parser.hasFlag("-exact")) {
			try {
				Path file = Paths.get(parser.getValue("-exact"));
				query.parseQuery(file, true);
			} catch (IOException e) {
				System.err.println("-exact: Unable to use path.");
			} catch (NullPointerException e) {
				System.err.println("-exact: Some data was unretrievable.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		if (parser.hasFlag("-query")) {
			try {
				Path file = Paths.get(parser.getValue("-query"));
				query.parseQuery(file, false);
			} catch (IOException e) {
				System.err.println("query: Unable to use path.");
			} catch (NullPointerException e) {
				System.err.println("query: Some data was unretrievable.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		if (parser.hasFlag("-results")) {
			try {
				Path outFile = Paths.get(parser.getValue("-results", "results.json"));
				query.toJSON(outFile);
			} catch (IOException e) {
				System.err.println("-results: Unable to use path.");
			} catch (NullPointerException e) {
				System.err.println("-results: Some data was unretrievable.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		if (parser.hasFlag("-port")) {
			try {
				int port = parser.getValue("-port", 8080);
				SearchEngineServer server = new SearchEngineServer(port, index);
				server.startUp();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		if (queue != null) {
			queue.shutdown();
		}
	}
}