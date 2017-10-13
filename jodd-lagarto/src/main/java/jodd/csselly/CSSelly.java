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

package jodd.csselly;

import jodd.util.StringUtil;
import jodd.util.UnsafeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSS selector parser. Works with one query, i.e. does not support groups
 * of selectors (selectors separated by a comma). To parse selectors group,
 * manually split the group query into single queries and parse each.
 * See: http://www.w3.org/TR/css3-selectors/#w3cselgrammar
 */
public class CSSelly {

	protected final CSSellyLexer lexer;

	public CSSelly(String input) {
		this(UnsafeUtil.getChars(input));
	}

	public CSSelly(char[] input) {
		this.lexer = new CSSellyLexer(input);
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses selector string.
	 * Returns <code>null</code> if no selector can be parsed.
	 */
	public List<CssSelector> parse() {
		try {
			lexer.yylex();
			if (lexer.selectors.isEmpty()) {
				return null;
			}

			// fixes last combinator
			CssSelector last = lexer.selectors.get(lexer.selectors.size() - 1);
			if (last.getCombinator() == Combinator.DESCENDANT) {
				last.setCombinator(null);
			}

			// set previous css selector
			CssSelector prevCssSelector = null;
			for (CssSelector cssSelector : lexer.selectors) {
				if (prevCssSelector != null) {
					cssSelector.setPrevCssSelector(prevCssSelector);
				}
				prevCssSelector = cssSelector;
			}

			return lexer.selectors;
		} catch (IOException ioex) {
			throw new CSSellyException(ioex);
		}
	}

	/**
	 * Parses string of selectors (separated with <b>,</b>). Returns
	 * list of {@link CssSelector} lists in the same order.
	 */
	public static List<List<CssSelector>> parse(String query) {
		String[] singleQueries = StringUtil.splitc(query, ',');
		List<List<CssSelector>> selectors = new ArrayList<>(singleQueries.length);

		for (String singleQuery: singleQueries) {
			selectors.add(new CSSelly(singleQuery).parse());
		}

		return selectors;
	}

	// ---------------------------------------------------------------- toString

	/**
	 * Returns string representation of given list of selectors.
	 */
	public static String toString(List<CssSelector> selectors) {
		StringBuilder out = new StringBuilder();
		for (CssSelector s : selectors) {
			out.append(s.toString());
		}
		return out.toString();
	}

}
