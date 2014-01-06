// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;

import java.io.IOException;

/**
 * Tag node.
 */
public class Element extends Node {

	protected final boolean voidElement;
	protected final boolean selfClosed;

	public Element(Document ownerNode, Tag tag, boolean voidElement, boolean selfClosed) {
		super(ownerNode, NodeType.ELEMENT, tag.getName());
		this.voidElement = voidElement;
		this.selfClosed = selfClosed;

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = tag.getAttributeName(i);
			String value = tag.getAttributeValue(i);
			setAttribute(key, value);
		}
	}

	// ---------------------------------------------------------------- clone

	public Element(Document ownerDocument, String name) {
		this(ownerDocument, name, false, false);
	}

	public Element(Document ownerDocument, String name, boolean voidElement, boolean selfClosed) {
		super(ownerDocument, NodeType.ELEMENT, name);
		this.voidElement = voidElement;
		this.selfClosed = selfClosed;
	}

	@Override
	public Element clone() {
		return cloneTo(new Element(ownerDocument, nodeName, voidElement, selfClosed));
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
		ownerDocument.getRenderer().renderElement(this, appendable);
	}

	@Override
	public String toString() {
		return '<' + nodeName + '>';
	}
}
