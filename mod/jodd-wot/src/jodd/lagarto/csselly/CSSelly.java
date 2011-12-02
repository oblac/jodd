// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.csselly;

import jodd.io.CharBufferReader;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.List;

/**
 * CSS selector parser..
 */
public class CSSelly {

	protected CSSellyLexer lexer;

	public CSSelly(CharSequence charSequence) {
		this(CharBuffer.wrap(charSequence));
	}

	public CSSelly(CharBuffer input) {
		this.lexer = new CSSellyLexer(new CharBufferReader(input));
	}

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
