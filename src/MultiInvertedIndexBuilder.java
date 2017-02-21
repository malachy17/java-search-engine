import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiInvertedIndexBuilder implements InvertedIndexBuilderInterface {

	private static final Logger logger = LogManager.getLogger();

	private final MultiInvertedIndex index;

	private final WorkQueue minions;

	public MultiInvertedIndexBuilder(MultiInvertedIndex index, WorkQueue minions) {
		this.index = index;
		this.minions = minions;
	}

	/**
	 * Traverses a given directory and goes through every file. If the file ends
	 * with ".txt", then it hands off that file and the index to the parseFile
	 * method.
	 * 
	 * @param path
	 *            the directory to start traversing from
	 * @param index
	 *            the inverted index to hand off to the parseFile method.
	 * @throws IOException
	 */
	public void traverse(Path path) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			for (Path file : listing) {
				if (Files.isDirectory(file)) {
					traverse(file);
				} else {
					if (file.getFileName().toString().toLowerCase().endsWith(".txt")) {
						minions.execute(new Minion(file));
					}
				}
			}
		} catch (IOException e) {
			logger.warn("Unable to traverse {}", path);
			logger.catching(Level.DEBUG, e);
		}

		minions.finish();
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link Minion} is created to handle that subdirectory.
	 */
	private class Minion implements Runnable {

		private Path file;

		public Minion(Path file) {
			logger.debug("Minion created for {}", file);
			this.file = file;
		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilderInterface.parseFile(file, local);
				index.addAll(local);
			} catch (IOException e) {
				logger.warn("Unable to parse {}", file);
				logger.catching(Level.DEBUG, e);
			}

			logger.debug("Minion finished {}", file);
		}
	}
}
