// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;

import java.io.IOException;

/**
 * Tag node.
 */
public class Element extends Node {

	protected final boolean voidElement;
	protected final boolean selfClosed;

	public Element(Tag tag, boolean voidElement, boolean selfClosed, boolean caseSensitive) {
		super(NodeType.ELEMENT, tag.getName(), caseSensitive);
		this.voidElement = voidElement;
		this.selfClosed = selfClosed;

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = tag.getAttributeName(i);
			String value = tag.getAttributeValue(i);
			setAttribute(key, value);
		}
	}

	/**
	 * Internal constructor.
	 */
	Element(String name) {
		this(name, false, false, false);
	}

	// ---------------------------------------------------------------- clone

	/**
	 * Internal constructor.
	 */
	private Element(String name, boolean voidElement, boolean selfClosed, boolean caseSensitive) {
		super(NodeType.ELEMENT, name, caseSensitive);
		this.voidElement = voidElement;
		this.selfClosed = selfClosed;
	}

	@Override
	public Element clone() {
		return cloneTo(new Element(nodeName, voidElement, selfClosed, caseSensitive));
	}

	// ---------------------------------------------------------------- html

	/**
	 * Returns <code>true</code> if element is void.
	 */
	public boolean isVoidElement() {
		return voidElement;
	}

	/**
	 * Returns <code>true</code> if element can self-close itself when empty.
	 */
	public boolean isSelfClosed() {
		return selfClosed;
	}

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

		if (selfClosed && childCount == 0) {
			appendable.append("/>");
			return;
		}

		appendable.append('>');

		if (voidElement) {
			return;
		}

		if (childCount != 0) {
			toInnerHtml(appendable);
		}

		appendable.append("</");
		appendable.append(nodeName);
		appendable.append('>');
	}

	@Override
	public String toString() {
		return '<' + nodeName + '>';
	}
}
