// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly.selector;

import jodd.lagarto.csselly.Selector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeFilter;

/**
 * Attribute selector.
 */
public class AttributeSelector extends Selector implements NodeFilter {

	// ---------------------------------------------------------------- ctor

	protected final String name;
	protected final String value;
	protected final Match match;
	protected char quoteChar;

	public AttributeSelector(String name, String sign, String value) {
		super(Type.ATTRIBUTE);
		this.name = name.trim();
		this.value = extractValue(value);
		this.match = Match.valueOf(sign);
	}

	public AttributeSelector(String name, Match match, String value) {
		super(Type.ATTRIBUTE);
		this.name = name.trim();
		this.match = match;
		this.value = extractValue(value);
	}

	public AttributeSelector(String attr) {
		super(Type.ATTRIBUTE);
		int index = attr.indexOf('=');
		if (index == -1) {
			this.name = attr.trim();
			this.match = null;
			this.value = null;
			return;
		}
		char first = attr.charAt(index - 1);
		this.match = Match.valueOfFirstChar(first);

		int signLen = this.match.getSign().length();
		index -= (signLen - 1);
		this.name = attr.substring(0, index).trim();
		this.value = extractValue(attr.substring(index + signLen));
	}

	protected String extractValue(String value) {
		quoteChar = value.charAt(0);
		if (quoteChar != '"' && quoteChar != '\'') {
			quoteChar = 0;
		}

		if (quoteChar != 0) {
			value = value.substring(1, value.length() - 1);
		}
		return value.trim();
	}

	// ---------------------------------------------------------------- getters

	/**
	 * Returns attribute name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns attribute value or <code>null</code> if doesn't exist.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns matching type.
	 */
	public Match getMatch() {
		return match;
	}

	/**
	 * Returns the quote char or <code>0</code> if quote is not used.
	 */
	public char getQuoteChar() {
		return quoteChar;
	}

	// ---------------------------------------------------------------- match

	public boolean accept(Node node) {
		if (!node.hasAttribute(name)) {
			return false;
		}

		if (value == null) {		// just detect if attribute exist
			return true;
		}

		String nodeValue = node.getAttribute(name);
		return match.compare(nodeValue, value);
	}
}
