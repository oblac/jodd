// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.TagUtil;

/**
 * Rules for void tags.
 */
public class HtmlVoidRules {

	/**
	 * Default void tags.
	 * http://dev.w3.org/html5/spec/Overview.html#void-elements
	 */
	private static final String[] HTML5_VOID_TAGS = {
			"area", "base", "br", "col", "embed", "hr", "img", "input",
			"keygen", "link", "menuitem", "meta", "param", "source",
			"track", "wbr"};

	/**
	 * Returns <code>true</code> if tag name is a void tag.
	 */
	public boolean isVoidTag(CharSequence tagName) {
		for (String html5VoidTag : HTML5_VOID_TAGS) {
			if (TagUtil.equalsToLowercase(tagName, html5VoidTag)) {
				return true;
			}
		}
		return false;
	}

}