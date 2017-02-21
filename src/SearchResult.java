/**
 * A SearchResult class that represents a search result from a query entered
 * into a search engine. Contains the file that the SearchResult is found in and
 * its frequency and initial position in said file.
 */
public class SearchResult implements Comparable<SearchResult> {

	private int count;
	private int firstPosition;

	private final String path;

	/**
	 * Constructor for a SearchResult object.
	 * 
	 * @param count
	 *            the frequency of the SearchResult in a given file.
	 * @param firstPosition
	 *            the first position the SearchResult is found in in a given
	 *            file.
	 * @param path
	 *            the file that this SearchResult is found in.
	 */
	public SearchResult(int count, int firstPosition, String path) {
		this.count = count;
		this.firstPosition = firstPosition;
		this.path = path;
	}

	/**
	 * Compares SearchResults first by which one has a greater count, if equal
	 * then by the lesser firstPosition, if equal than by the default comparing
	 * function.
	 */
	public int compareTo(SearchResult other) {

		if (Integer.compare(this.count, other.count) != 0) {
			return -1 * Integer.compare(this.count, other.count);
		}

		if (Integer.compare(this.firstPosition, other.firstPosition) != 0) {
			return Integer.compare(this.firstPosition, other.firstPosition);
		}

		return this.path.compareTo(other.path);
	}

	/**
	 * @return this SearchObject's count.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @return this SearchObject's firstPosition.
	 */
	public int getFirstPosition() {
		return this.firstPosition;
	}

	/**
	 * @return this SearchObject's path.
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Adds the given integer to the SearchObject's count member.
	 * 
	 * @param count
	 *            The integer to add to this SearchObject's count.
	 */
	public void addCount(int count) {
		this.count += count;
	}

	/**
	 * Sets this object's firstPosition to the given integer.
	 * 
	 * @param position
	 *            The new position to replace this object's firstPosition.
	 */
	public void setFirstPosition(int position) {
		if (position < firstPosition) {
			this.firstPosition = position;
		}
	}
}
