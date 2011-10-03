// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter;

import jodd.lagarto.TagAdapter;
import jodd.lagarto.TagVisitor;
import jodd.util.CharUtil;

import java.nio.CharBuffer;

/**
 * Cleans all non important stuff from html.
 */
public class CleanHtmlTagAdapter extends TagAdapter {

	public CleanHtmlTagAdapter(TagVisitor target) {
		super(target);
	}

	/**
	 * Skips HTML comments.
	 */
	@Override
	public void comment(CharSequence comment) {
	}

	/**
	 * Cleans unnecessary whitespaces.
	 */
	@Override
	public void text(CharSequence text) {
		char[] dest = new char[text.length()];

		int ndx = 0;
		boolean regularChar = true;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (CharUtil.isWhitespace(c)) {
				if (regularChar) {
					regularChar = false;
					c = ' ';
				} else {
					continue;
				}
			} else {
				regularChar = true;
			}

			dest[ndx] = c;
			ndx++;
		}

		if (regularChar || (ndx != 1)) {
			super.text(CharBuffer.wrap(dest, 0, ndx));
		}
	}
}
