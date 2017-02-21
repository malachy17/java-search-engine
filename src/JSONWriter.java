import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Outputs the data of a Map to a JSON file in proper JSON format.
 * 
 * @see <a href="http://json.org/">http://json.org/</a>
 */
public class JSONWriter {

	/** Tab character used for pretty JSON output. */
	private static final char TAB = '\t';

	/** End of line character used for pretty JSON output. */
	private static final char END = '\n';

	private static final Logger logger = LogManager.getLogger();

	/**
	 * Writes data from a Map to JSON format on a JSON file.
	 * 
	 * @param outFile
	 *            the file that the JSON output is saved on
	 * @param map
	 *            the data structure who's data is being used to write JSON
	 *            output
	 * @throws IOException
	 */
	public static void writeNestedObject(Path outFile, Map<String, TreeMap<String, TreeSet<Integer>>> map)
			throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(outFile, Charset.forName("UTF-8"));) {
			writer.write("{" + END);

			int count1 = 1;
			int size1 = map.keySet().size();
			for (String word : map.keySet()) {
				writer.write(JSONWriter.tab(1) + JSONWriter.quote(word) + ": {" + END);

				int count2 = 1;
				int size2 = map.get(word).keySet().size();
				for (String file : map.get(word).keySet()) {
					writer.write(JSONWriter.tab(2) + JSONWriter.quote(file) + ": [" + END);

					int count3 = 1;
					int size3 = map.get(word).get(file).size();
					for (Integer position : map.get(word).get(file)) {
						writer.write(JSONWriter.tab(3) + position + JSONWriter.addComma(count3, size3) + END);
						count3++;
					}

					writer.write(JSONWriter.tab(2) + "]" + JSONWriter.addComma(count2, size2) + END);
					count2++;
				}

				writer.write(JSONWriter.tab(1) + "}" + JSONWriter.addComma(count1, size1) + END);
				count1++;
			}

			writer.write("}" + END);
		} catch (Exception e) {
			System.err.println("Error in writeNestedObject");
			System.err.println(e.getMessage());
			logger.debug("Error", e);
		}
	}

	/**
	 * Outputs the Map of queries and results to a file in JSON format.
	 * 
	 * @param outFile
	 * @param map
	 * @throws IOException
	 */
	public static void writeSearchResults(Path outFile, TreeMap<String, ArrayList<SearchResult>> map)
			throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(outFile, Charset.forName("UTF-8"));) {
			writer.write("{" + END);

			int count1 = 1;
			int size1 = map.keySet().size();
			for (String word : map.keySet()) {
				writer.write(JSONWriter.tab(1) + JSONWriter.quote(word) + ": [" + END);

				int count2 = 1;
				int size2 = map.get(word).size();
				for (SearchResult result : map.get(word)) {
					writer.write(JSONWriter.tab(2) + "{" + END);
					writer.write(JSONWriter.tab(3) + "\"where\": " + JSONWriter.quote(result.getPath()) + "," + END);
					writer.write(JSONWriter.tab(3) + "\"count\": " + result.getCount() + "," + END);
					writer.write(JSONWriter.tab(3) + "\"index\": " + result.getFirstPosition() + END);
					writer.write(JSONWriter.tab(2) + "}" + JSONWriter.addComma(count2, size2) + END);
					count2++;
				}

				writer.write(JSONWriter.tab(1) + "]" + JSONWriter.addComma(count1, size1) + END);
				count1++;
			}

			writer.write("}" + END);
		}
	}

	/**
	 * Returns a comma count is less than the size of the data structure.
	 * 
	 * @param count
	 *            a counter that keeps track of the number of elements
	 * @param size
	 *            the size of the data structure
	 * @return a comma if count is less than size, an empty string otherwise
	 */
	private static String addComma(int count, int size) {
		if (count < size) {
			return ",";
		} else {
			return "";
		}
	}

	/**
	 * Returns a quoted version of the provided text.
	 * 
	 * @param text
	 * @return "text" in quotes
	 */
	private static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	/**
	 * Returns n tab characters.
	 * 
	 * @param n
	 *            number of tab characters
	 * @return n tab characters concatenated
	 */
	private static String tab(int n) {
		char[] tabs = new char[n];
		Arrays.fill(tabs, TAB);
		return String.valueOf(tabs);
	}

}