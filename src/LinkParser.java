import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses links from HTML. Assumes the HTML is valid, and all attributes are
 * properly quoted and URL encoded.
 *
 * <p>
 * See the following link for details on the HTML Anchor tag:
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a"> https:
 * //developer.mozilla.org/en-US/docs/Web/HTML/Element/a </a>
 * 
 * @see LinkTester
 */
public class LinkParser {

	/**
	 * The regular expression used to parse the HTML for links.
	 */
	public static final String REGEX = "(?i)<a[^>]*?href=\"(.+?)\"";

	/**
	 * The group in the regular expression that captures the raw link.
	 */
	public static final int GROUP = 1; // Change if necessary

	/**
	 * Parses the provided text for HTML links.
	 *
	 * @param text
	 *            the html to match the regex against.
	 * @param url
	 *            the url used for the base.
	 * @return list of URLs found in HTML code
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws UnknownHostException
	 */
	public static ArrayList<String> listLinks(String text, String url)
			throws UnknownHostException, MalformedURLException, IOException {

		URL base = new URL(url);
		ArrayList<String> links = new ArrayList<>();

		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(text);

		while (m.find()) {
			String strLink = m.group(GROUP);

			URL absolute = new URL(base, strLink);
			URL link = new URL(absolute.getProtocol(), absolute.getHost(), absolute.getFile());
			strLink = link.toString();

			links.add(strLink);
		}

		return links;
	}
}