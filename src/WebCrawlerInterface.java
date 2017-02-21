import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

public interface WebCrawlerInterface {

	/**
	 * Starts at the seed web-page and performs a breadth-first search upon that
	 * web-page's links, and their links and so forth until it fills a the urls
	 * set with 50 links. Uses the set to keep track of the amount of links and
	 * a queue to iterate each scraped link with. Sends each valid link's HTML
	 * to the sendToIndex() method where that link's words will be passed onto
	 * the InvertedIndex for adding.
	 * 
	 * @param seed
	 *            The first web-page crawled, and parent to all other web-pages.
	 * 
	 * @throws UnknownHostException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void addSeed(String seed) throws UnknownHostException, MalformedURLException, IOException;

	/**
	 * Takes in HTML and that html's absolute URL and sends each word's name,
	 * file that it is found in, and position within the file to the
	 * InvertedIndex to be added in.
	 * 
	 * @param html
	 *            The HTML containing the words that this method will grab.
	 * @param link
	 *            The web-page's URL name that the word is found in.
	 */
	public static void sendToIndex(String html, String link, InvertedIndex index) {
		String[] words = HTMLCleaner.fetchHTMLWords(html);
		int position = 1;

		for (String word : words) {
			index.add(word, link, position);
			position++;
		}
	}

}
