// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Tag type.
 */
public enum TagType {

	/**
	 * Start tags: <code>&lt;foo&gt;</code>.
	 */
	START("<", ">"),

	/**
	 * End tags: <code>&lt;/foo&gt;</code>.
	 */
	END("</", ">"),

	/**
	 * Self closing tag: <code>&lt;foo/&gt;</code>.
	 */
	SELF_CLOSING("<", "/>");

	private final String startString;
	private final String endString;
	private final boolean isStarting;
	private final boolean isEnding;

	TagType(String startString, String endString) {
		this.startString = startString;
		this.endString = endString;
		isStarting = startString.length() == 1;
		isEnding = startString.length() == 2 || endString.length() == 2;
	}

	/**
	 * Returns tags starting string.
	 */
	public String getStartString() {
		return startString;
	}

	/**
	 * Returns tags ending string.
	 */
	public String getEndString() {
		return endString;
	}

	/**
	 * Returns <code>true</code> if tag is {@link #START} or {@link #SELF_CLOSING}.
	 */
	public boolean isStartingTag() {
		return isStarting;
	}

	/**
	 * Returns <code>true</code> if tag is {@link #END} or {@link #SELF_CLOSING}.
	 */
	public boolean isEndingTag() {
		return isEnding;
	}
}
