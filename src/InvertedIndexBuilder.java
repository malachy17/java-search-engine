import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Traverses a given directory and goes through every text file, line by line
 * and sends every word, the file it's found in, and it's position to the given
 * index for adding.
 */
public class InvertedIndexBuilder implements InvertedIndexBuilderInterface {

	private final InvertedIndex index;

	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * Traverses a given directory and goes through every file. If the file ends
	 * with ".txt", then it hands off that file and the index to the parseFile
	 * method.
	 * 
	 * @param path
	 *            the direcotry to start traversing from
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
						InvertedIndexBuilderInterface.parseFile(file, index);
					}
				}
			}
		}
	}
}
