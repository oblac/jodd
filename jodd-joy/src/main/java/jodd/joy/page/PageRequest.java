// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

/**
 * Represents single page request.
 */
public class PageRequest {

	protected int page = 1;
	protected int size = 10;
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

	// ---------------------------------------------------------------- sort

	/**
	 * Returns sort 1-based index of column that should be sorted.
	 * If <code>0</code>, nothing should be sorted.
	 * Positive values represents ascending order,
	 * negative values descending.
	 * <p>
	 * By using the index we also hide the real column names.
	 */
	public int getSort() {
		return sort;
	}

	/**
	 * Returns sort index.
	 * @see #getSort()
	 */
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
				", sort=" + sort +
				", pagerId=" + pagerId +
				'}';
	}
}