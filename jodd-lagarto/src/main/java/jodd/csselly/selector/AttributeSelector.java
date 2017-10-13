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

package jodd.csselly.selector;

import jodd.csselly.Selector;
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

		if (nodeValue == null) {
			return false;
		}
		
		return match.compare(nodeValue, value);
	}
}
