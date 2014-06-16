// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;
import jodd.util.Util;

/**
 * Tag node.
 */
public class Element extends Node {

	protected final boolean voidElement;
	protected final boolean selfClosed;
	protected final boolean rawTag;

	public Element(Document ownerNode, Tag tag, boolean voidElement, boolean selfClosed) {
		super(ownerNode, NodeType.ELEMENT, Util.toString(tag.getName()));
		this.voidElement = voidElement;
		this.selfClosed = selfClosed;
		this.rawTag = tag.isRawTag();

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = Util.toString(tag.getAttributeName(i));
			String value = Util.toString(tag.getAttributeValue(i));
			setAttribute(key, value);
		}
	}

	// ---------------------------------------------------------------- clone

	public Element(Document ownerDocument, String name) {
		this(ownerDocument, name, false, false, false);
	}

	public Element(Document ownerDocument, String name, boolean voidElement, boolean selfClosed, boolean rawTag) {
		super(ownerDocument, NodeType.ELEMENT, name);
		this.voidElement = voidElement;
		this.selfClosed = selfClosed;
		this.rawTag = rawTag;
	}

	@Override
	public Element clone() {
		return cloneTo(new Element(ownerDocument, nodeName, voidElement, selfClosed, rawTag));
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

	/**
	 * Returns <code>true</code> if tags content is RAW text.
	 */
	public boolean isRawTag() {
		return rawTag;
	}

	@Override
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.element(this);
	}

	@Override
	public String toString() {
		return '<' + nodeName + '>';
	}

}