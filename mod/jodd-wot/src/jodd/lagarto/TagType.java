// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Tag type.
 */
public enum TagType {

	/**
	 * Open tag: <code>&lt;foo&gt;</code>.
	 */
	OPEN("<", ">"),

	/**
	 * Close tag: <code>&lt;/foo&gt;</code>.
	 */
	CLOSE("</", ">"),

	/**
	 * Empty body tag: <code>&lt;foo/&gt;</code>.
	 */
	EMPTY("<", "/>");

	private final String start;
	private final String end;
	private final boolean isOpening;
	private final boolean isClosing;

	private TagType(String start, String end) {
		this.start = start;
		this.end = end;
		isOpening = start.length() == 1;
		isClosing = start.length() == 2 || end.length() == 2;
	}

	/**
	 * Returns tags starting string.
	 */
	public String getStartString() {
		return start;
	}

	/**
	 * Returns tags ending string.
	 */
	public String getEndString() {
		return end;
	}

	/**
	 * Returns <code>true</code> if tag is {@link #OPEN} or {@link #EMPTY}.
	 */
	public boolean isOpeningTag() {
		return isOpening;
	}

	/**
	 * Returns <code>true</code> if tag is {@link #CLOSE} or {@link #EMPTY}.
	 */
	public boolean isClosingTag() {
		return isClosing;
	}
}
