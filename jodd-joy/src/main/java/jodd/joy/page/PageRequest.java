// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.joy.page;

/**
 * Represents single page request.
 */
public class PageRequest {

	/**
	 * Default page size.
	 */
	public static int defaultPageSize = 10;
	/**
	 * Default sort index.
	 */
	public static int defaultSortIndex = 0;

	protected int page = 1;
	protected int size = defaultPageSize;
	protected String pagerId;
	protected int sort = defaultSortIndex;

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