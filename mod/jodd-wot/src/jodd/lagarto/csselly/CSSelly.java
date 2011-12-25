// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly;

import jodd.io.CharBufferReader;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.List;

/**
 * CSS selector parser. Works with one query, i.e. does not support groups
 * of selectors (selectors separated by a comma). To parse selectors group,
 * manually split the group query into single queries and parse each.
 * See: http://www.w3.org/TR/css3-selectors/#w3cselgrammar
 */
public class CSSelly {

	protected final CSSellyLexer lexer;

	public CSSelly(CharSequence charSequence) {
		this(CharBuffer.wrap(charSequence));
	}

	public CSSelly(CharBuffer input) {
		this.lexer = new CSSellyLexer(new CharBufferReader(input));
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
			CssSelector last = lexer.selectors.getLast();
			if (last.getCombinator() == Combinator.DESCENDANT) {
				lexer.selectors.getLast().setCombinator(null);
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
