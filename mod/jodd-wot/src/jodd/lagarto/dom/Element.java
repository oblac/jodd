// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;

import java.io.IOException;

/**
 * Tag node.
 */
public class Element extends Node {

	public Element(Tag tag, boolean caseSensitive) {
		super(NodeType.ELEMENT, tag.getName(), caseSensitive);

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = tag.getAttributeName(i);
			String value = tag.getAttributeValue(i);
			setAttribute(key, value);
		}
	}

	public Element(String name) {
		this(name, false);
	}

	public Element(String name, boolean caseSensitive) {
		super(NodeType.ELEMENT, name, caseSensitive);
	}

	@Override
	public Element clone() {
		return cloneTo(new Element(nodeName, caseSensitive));
	}

	// ---------------------------------------------------------------- html

	/**
	 * When set to <code>true</code> closed tag will be used instead of
	 * shortcut form (&lt;foo/&gt;) when there are no children nodes. Some
	 * tags requires to have closing tag (e.g. <code>script</code>).
	 */
	protected boolean forceCloseTag;

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		appendable.append('<');
		appendable.append(nodeName);

		int attrCount = getAttributesCount();
		if (attrCount != 0) {
			for (int i = 0; i < attrCount; i++) {
				Attribute attr = getAttribute(i);
				appendable.append(' ');
				attr.toHtml(appendable);
			}
		}

		int childCount = getChildNodesCount();
		if ((childCount == 0) && !forceCloseTag) {
			appendable.append("/>");
		} else {
			appendable.append('>');

			if (childCount != 0) {
				toInnerHtml(appendable);
			}

			appendable.append("</");
			appendable.append(nodeName);
			appendable.append('>');
		}
	}
}
