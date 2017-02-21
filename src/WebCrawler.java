import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Crawls links starting from a seed URL in a breadth-first search fashion and
 * sends all words found to an InvertedIndex to be added in.
 */
public class WebCrawler implements WebCrawlerInterface {

	private final InvertedIndex index;
	private final LinkedList<String> queue;
	private final Set<String> urls;

	/**
	 * Constructor for the WebCrawler class. Takes in an InvertedIndex which is
	 * used to send words to. Initializes a queue and set of URLs used in
	 * addSeed.
	 * 
	 * @param index
	 *            The InvertedIndex object that words from sendToIndex() will be
	 *            sent to.
	 */
	public WebCrawler(InvertedIndex index) {
		this.index = index;
		this.queue = new LinkedList<>();
		this.urls = new HashSet<>();
	}

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
	public void addSeed(String seed) throws UnknownHostException, MalformedURLException, IOException {
		urls.add(seed);
		queue.add(seed);

		while (!queue.isEmpty()) {
			String current = queue.remove();
			String html = HTTPFetcher.fetchHTML(current);
			WebCrawlerInterface.sendToIndex(html, current, index);
			ArrayList<String> links = LinkParser.listLinks(html, current);

			for (String link : links) {
				if (urls.size() >= 50) {
					break;
				} else if (!urls.contains(link)) {
					urls.add(link);
					queue.add(link);
				}
			}
		}
	}
}
