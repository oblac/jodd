// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.page;

/**
 * Moderate page navigator.
 * Like altavista.
 */
public class PageNav {

	public static final int DEFAULT_SHOWN = 10;

	protected int from;
	protected int to;


	public PageNav(PageData pageData) {
		this(pageData.getTotalPages(), pageData.getCurrentPage(), DEFAULT_SHOWN);
	}

	public PageNav(PageData pageData, int shown) {
		this(pageData.getTotalPages(), pageData.getCurrentPage(), shown);
	}

	public PageNav(int total, int current, int shown) {
		if (total == 0) {
			return;
		}
		if (total <= shown) {
			from = 1;
			to = total;
			return;
		}
		int leftMin = shown / 2;
		int rightMin = leftMin - 1;

		from = current - leftMin;
		if (from < 1) {
			from = 1;
			to = shown;
			return;
		}

		to = current + rightMin;
		if (to > total) {
			to = total;
			from = to - shown + 1;
		}
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

}
