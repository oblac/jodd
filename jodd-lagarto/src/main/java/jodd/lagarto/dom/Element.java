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