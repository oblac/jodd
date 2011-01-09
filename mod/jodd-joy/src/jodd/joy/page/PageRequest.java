// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

/**
 * Represents single page request.
 */
public class PageRequest {

	public static int defaultPageSize = 10;

	protected int page = 1;
	protected int size = defaultPageSize;

	public PageRequest() {
	}

	public PageRequest(int page, int size) {
		this.page = page;
		this.size = size;
	}

	/**
	 * Returns requested page number.
	 * @see #setPage(int)
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Specifies requested page number.
	 * Page numbers are 1-based.
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Returns size of the page. Page size refers to total numbers of items per page.
	 * @see #setSize(int)
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Specifies page size, i.e. number of elements per page.
	 */
	public void setSize(int size) {
		this.size = size;
	}


	/**
	 * Calculates offset.
	 */
	public int calcOffset() {
		return (page - 1) * size;
	}

	@Override
	public String toString() {
		return "PageRequest{" +
				"page=" + page +
				", size=" + size +
				'}';
	}
}
