// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

/**
 * Represents single page request.
 */
public class PageRequest {

	public static int defaultPageSize = 10;

	protected int page = 1;
	protected int size = defaultPageSize;
	protected String pagerId;
	protected int sort;

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
	 * Returns sort index.
	 * If <code>0</code>, nothing should be sorted.
	 * Positive values represents ascending order,
	 * negative values descending.
	 */
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	/**
	 * Returns pager id.
	 */
	public String getPagerId() {
		return pagerId;
	}

	/**
	 * Returns pager id.
	 */
	public void setPagerId(String pagerId) {
		this.pagerId = pagerId;
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
				", size=" + size +
				", pagerId=" + pagerId +
				'}';
	}
}
