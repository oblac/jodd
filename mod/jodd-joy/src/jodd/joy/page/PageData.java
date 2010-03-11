package jodd.joy.page;

import java.util.List;

/**
 * Holds information about single page of results.
 * Item indexes are zero-based and page numbers are one-based.
 */
public class PageData<T> {


	protected final int pageSize;

	/**
	 * Returns page size, i.e. number of items per page.
	 */
	public int getPageSize() {
		return pageSize;
	}


	protected final int firstIndex;

	/**
	 * Returns index of the first item on this page.
	 */
	public int getFirstIndex() {
		return firstIndex;
	}


	protected final int lastIndex;

	/**
	 * Returns index of last item of page.
	 * On every page but last, last index equals to first index plus page size.
	 */
	public int getLastIndex() {
		return lastIndex;
	}


	protected final List<T> items;

	/**
	 * Returns the list of items on this page.
	 */
	public List<T> getItems() {
		return items;
	}


	protected final int totalItems;

	/**
	 * Returns total number of items.
	 */
	public int getTotalItems() {
		return this.totalItems;
	}


	protected final int totalPages;

	/**
	 * Returns total number of pages.
	 */
	public int getTotalPages() {
		return totalPages;
	}


	protected int currentPage;

	/**
	 * Returns current page number.
	 */
	public int getCurrentPage() {
		return currentPage;
	}


	protected final int pageItemsCount;

	/**
	 * Returns the number of shown items per page.
	 * Note that this value is calculated.
	 */
	public int getPageItemsCount() {
		return pageItemsCount;
	}

	// ---------------------------------------------------------------- ctors

	public PageData() {
		this(1, 0, PageRequest.defaultPageSize, null);
	}

	public PageData(int page, int size) {
		this(page, size, PageRequest.defaultPageSize, null);
	}

	public PageData(int page, int size, int pageSize) {
		this(page, size, pageSize, null);
	}

	public PageData(PageRequest req, int size, List<T> items) {
		this(req.getPage(), size, req.getSize(), items);
	}

	/**
	 * Main constructor.
	 * @param page	current page
	 * @param size	total number of items
	 * @param pageSize number of items per page
	 * @param items list of fetched items
	 */
	public PageData(int page, int size, int pageSize, List<T> items) {
		if (pageSize <= 0) {
			pageSize = PageRequest.defaultPageSize;
		}
		this.pageSize = pageSize;
		this.totalItems = size;
		this.items = items;
		this.totalPages = (totalItems % pageSize == 0) ? totalItems / pageSize : totalItems / pageSize + 1;
		if (page < 1) {
			page = 1;
		}
		if (page > totalPages) {
			page = totalPages;
		}
		this.currentPage = page;
		this.firstIndex = calcFirstItemIndexOfPage(page, pageSize, size);

		int last = isLastPage() ? totalItems - 1: firstIndex + pageSize - 1;
		int itemsPerPage = last - firstIndex + 1;
		if (last < 0) {
			last = 0;
			itemsPerPage = 0;
		}

		this.lastIndex = last;
		this.pageItemsCount = itemsPerPage;
	}

	// ---------------------------------------------------------------- checks


	/**
	 * Returns <code>true</code> if there is a next page, i.e. we are not at the last page.
	 */
	public boolean hasNextPage() {
		return currentPage < totalPages - 1;
	}

	/**
	 * Returns <code>true</code> if we are on the last page.
	 */
	public boolean isLastPage() {
		return currentPage == totalPages;
	}

	/**
	 * Returns <code>true</code> if there is a previous page, i.e. we are not at the first page.
	 */
	public boolean hasPreviousPage() {
		return currentPage > 1;
	}

	/**
	 * Returns <code>true</code> id we are on the first page.
	 */
	public boolean isFirstPage() {
		return currentPage == 1 || currentPage == 0;
	}

	/**
	 * Convenient report method that can be used as JSON array. 
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('[');
		result.append(currentPage).append(',');
		result.append(totalPages).append(',');
		result.append(totalItems).append(',');
		result.append(firstIndex).append(',');
		result.append(lastIndex).append(',');
		result.append(pageSize).append(']');
		return result.toString();
	}

	// ---------------------------------------------------------------- utilities

	/**
	 * Calculates page number that contains some item.
	 */
	public static int calcPageOfItem(int itemIndex, int pageSize) {
		return itemIndex / pageSize + 1;
	}

	/**
	 * Calculates the first item index of requested page.
	 */
	public static int calcFirstItemIndexOfPage(int page, int pageSize, int total) {
		if (total == 0) {
			return 0;
		}
		if (page < 1) {
			page = 1;
		}
		int first = (page - 1) * pageSize;
		if (first >= total) {
			first = ((total - 1) / pageSize) * pageSize;	// first item on the last page
		}
		return first;
	}

	/**
	 * Calculates first item index of the page.
	 */
	public static int calcFirstItemIndexOfPage(PageRequest pageRequest, int total) {
		return calcFirstItemIndexOfPage(pageRequest.getPage(), pageRequest.getSize(), total);
	}
}
