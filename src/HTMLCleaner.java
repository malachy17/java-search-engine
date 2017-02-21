/**
 * A helper class with several static methods that will help fetch a webpage,
 * strip out all of the HTML, and parse the resulting plain text into words.
 * Meant to be used for the web crawler project.
 *
 * @see HTMLCleaner
 * @see HTMLCleanerTest
 */
public class HTMLCleaner {

	/** Regular expression for removing special characters. */
	public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	/** Regular expression for splitting text into words by whitespace. */
	public static final String SPLIT_REGEX = "(?U)\\p{Space}+";

	/**
	 * Fetches the webpage at the provided URL, cleans up the HTML tags, and
	 * parses the resulting plain text into words.
	 * 
	 * @param url
	 *            URL to clean and parse.
	 * @return list of parsed words
	 */
	public static String[] fetchWords(String url) {
		String html = fetchHTML(url);
		String text = cleanHTML(html);
		return parseWords(text);
	}

	/**
	 * Cleans the HTML tags, and parses the resulting plain text into words.
	 * 
	 * @param html
	 *            html to clean and parse.
	 * @return
	 */
	public static String[] fetchHTMLWords(String html) {
		String text = cleanHTML(html);
		return parseWords(text);
	}

	/**
	 * Parses the provided plain text (already cleaned of HTML tags) into
	 * individual words.
	 *
	 * @param text
	 *            plain text without html tags
	 * @return list of parsed words
	 */
	public static String[] parseWords(String text) {
		text = text.replaceAll(CLEAN_REGEX, "").toLowerCase().trim();
		return text.split(SPLIT_REGEX);
	}

	/**
	 * Removes all style and script tags (and any text in between those tags),
	 * all HTML tags, and all HTML entities.
	 *
	 * @param html
	 *            html code to parse
	 * @return plain text
	 */
	public static String cleanHTML(String html) {
		String text = html;
		text = stripElement("script", text);
		text = stripElement("style", text);
		text = stripTags(text);
		text = stripEntities(text);
		return text;
	}

	/**
	 * Fetches the webpage at the provided URL by opening a socket, sending an
	 * HTTP request, removing the headers, and returning the resulting HTML
	 * code.
	 *
	 * @param link
	 *            webpage to download
	 * @return html code
	 */
	public static String fetchHTML(String link) {
		try {
			return HTTPFetcher.fetchHTML(link);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return "Failed to fetchHTML in HTMLCleaner.";
		}
	}

	/**
	 * Removes everything between the element tags, and the element tags
	 * themselves. For example, consider the html code:
	 *
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 *
	 * If removing the "style" element, all of the above code will be removed,
	 * and replaced with the empty string.
	 *
	 * @param name
	 *            name of the element to strip, like "style" or "script"
	 * @param html
	 *            html code to parse
	 * @return html code without the element specified
	 */
	public static String stripElement(String name, String html) {
		String elem_regex = "(?s)(?i)<" + name + "[^>]*>(.)*?</" + name + "[\\s]*>";
		return html.replaceAll(elem_regex, "");
	}

	/**
	 * Removes all HTML tags, which is essentially anything between the "<" and
	 * ">" symbols. The tag will be replaced by the empty string.
	 *
	 * @param html
	 *            html code to parse
	 * @return text without any html tags
	 */
	public static String stripTags(String html) {
		return html.replaceAll("(?i)<[^>]*>", "");
	}

	/**
	 * Replaces all HTML entities in the text with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html
	 *            the text with html code being checked
	 * @return text with HTML entities replaced by an empty string
	 */
	public static String stripEntities(String html) {
		return html.replaceAll("(?i)&[^;^\\s]*;", "");
	}
}
