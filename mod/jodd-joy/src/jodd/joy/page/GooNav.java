// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

/**
 * Default page navigator that works similar to Google's.
 */
public class GooNav {

	protected int from;
	protected int to;

	public GooNav(PageData pageData, int pages) {
		this(pageData.getTotalPages(), pageData.getCurrentPage(), pages);
	}

	public GooNav(int total, int current, int pages) {
		if (total == 0) {
			return;
		}

		from = current - pages;
		if (from < 1) {
			from = 1;
		}

		to = current + pages - 1;
		if (to > total) {
			to = total;
		}
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
}
