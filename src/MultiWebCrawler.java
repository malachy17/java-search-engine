import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Crawls links starting from a seed URL in a breadth-first search fashion and
 * sends all words found to an InvertedIndex to be added in.
 */
public class MultiWebCrawler implements WebCrawlerInterface {

	private static final Logger logger = LogManager.getLogger();

	private final MultiInvertedIndex index;
	private final Set<String> urls;

	private final WorkQueue minions;

	/**
	 * Constructor for the WebCrawler class. Takes in an InvertedIndex which is
	 * used to send words to. Initializes a queue and set of URLs used in
	 * addSeed.
	 * 
	 * @param index
	 *            The InvertedIndex object that words from sendToIndex() will be
	 *            sent to.
	 */
	public MultiWebCrawler(MultiInvertedIndex index, WorkQueue minions) {
		this.index = index;
		this.urls = new HashSet<>();

		this.minions = minions;
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
		synchronized (urls) {
			urls.add(seed);
		}
		minions.execute(new Minion(seed));
		minions.finish();
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link Minion} is created to handle that subdirectory.
	 */
	private class Minion implements Runnable {

		private String current;

		public Minion(String current) {
			logger.debug("Minion created for {}", current);
			this.current = current;
		}

		@Override
		public void run() {
			try {
				String html = HTTPFetcher.fetchHTML(current);
				ArrayList<String> links = LinkParser.listLinks(html, current);

				synchronized (urls) {
					for (String link : links) {
						if (urls.size() >= 50) {
							break;
						} else if (!urls.contains(link)) {
							urls.add(link);
							minions.execute(new Minion(link));
						}
					}
				}

				InvertedIndex local = new InvertedIndex();
				WebCrawlerInterface.sendToIndex(html, current, local);
				index.addAll(local);
			} catch (Exception e) {
				logger.catching(Level.DEBUG, e);
			}

			logger.debug("Minion finished {}", current);
		}
	}
}
