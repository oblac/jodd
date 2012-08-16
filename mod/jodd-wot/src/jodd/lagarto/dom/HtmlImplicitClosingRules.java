// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.StringUtil;

/**
 * HTML rules for implicitly closing tags.
 */
public class HtmlImplicitClosingRules {

	/**
	 * List of tags that can be implicitly closed on provided children.
	 * The first array contains parent tag name (i.e. parent node name),
	 * that can be closed.
	 * The second array contains list of all children that will
	 * implicitly close the parent (i.e. current node name).
	 */
	public static final String[][] IMPLIED_ON_START = new String[][] {
			new String[] {
					"p"
			},
			new String[] {
					"address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl",
					"fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol",
					"p", "section", "summary", "ul", "h1", "h2", "h3", "h4", "h5", "h6", "pre", "listing",
					"form", "li", "dd", "dt", "plaintext", "table", "hr", "xmp",
			},

			new String[] {
					"dd", "dt"
			},
			new String[] {
					"dd", "dt"
			},
	};
	/**
	 * List of tags that can be implicitly closed on tags end.
	 * The first array contains current node name (i.e. ending tag).
	 * The second array contains list of all of parent tags that can be closed.
	 */
	public static final String[][] IMPLIED_ON_END = new String[][] {
			new String[] {
					"dl"
			},
			new String[] {
					"dd", "dt"
			},
	};


	/**
	 * Returns <code>true</code> if parent node tag can be closed implicitly.
	 */
	public boolean implicitlyCloseParentTagOnNewTag(String parentNodeName, String nodeName) {

		parentNodeName = parentNodeName.toLowerCase();
		nodeName = nodeName.toLowerCase();

		boolean result = false;

		for (int i = 0; i < IMPLIED_ON_START.length; i+=2) {
			if (StringUtil.equalsOne(parentNodeName, IMPLIED_ON_START[i]) != -1) {
				result = (StringUtil.equalsOne(nodeName, IMPLIED_ON_START[i + 1]) != -1);
			}
		}

		return result;
	}

	/**
	 * Returns <code>true</code> if current end tag (node name) closes the parent tag.
	 */
	public boolean implicitlyCloseParentTagOnTagEnd(String parentNodeName, String nodeName) {

		parentNodeName = parentNodeName.toLowerCase();
		nodeName = nodeName.toLowerCase();

		// body and html tag closes all.
		if (nodeName.equals("body") || nodeName.equals("html")) {
			return true;
		}

		boolean result = false;

		for (int i = 0; i < IMPLIED_ON_END.length; i+=2) {
			if (StringUtil.equalsOne(nodeName, IMPLIED_ON_END[i]) != -1) {
				result = (StringUtil.equalsOne(parentNodeName, IMPLIED_ON_END[i + 1]) != -1);
			}
		}

		return result;
	}
}
