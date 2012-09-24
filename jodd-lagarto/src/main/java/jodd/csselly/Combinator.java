// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly;

/**
 * CSS selector combinators.
 */
public enum Combinator {

	/**
	 * Describes an arbitrary descendant of some ancestor element
	 */
	DESCENDANT(" "),

	/**
	 * Describes a childhood relationship between two elements.
	 */
	CHILD(">"),

	/**
	 * The elements represented by the two sequences share the same parent
	 * in the document tree and the element represented by the first sequence
	 * immediately precedes the element represented by the second one.
	 */
	ADJACENT_SIBLING("+"),

	/**
	 * The elements represented by the two sequences share the same parent
	 * in the document tree and the element represented by the first sequence
	 * precedes (not necessarily immediately) the element represented by the second one.
	 */
	GENERAL_SIBLING("~");


	Combinator(String sign) {
		this.sign = sign;
	}

	private final String sign;

	/**
	 * Returns combinator sign.
	 */
	public String getSign() {
		return sign;
	}

}
