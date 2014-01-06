// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;

import java.io.IOException;

/**
 * XML declaration node.
 */
public class XmlDeclaration extends Node {

	public XmlDeclaration(Document ownerDocument, Tag tag) {
		super(ownerDocument, NodeType.XML_DECLARATION, tag.getName());

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = tag.getAttributeName(i);
			String value = tag.getAttributeValue(i);
			setAttribute(key, value);
		}
	}

	public XmlDeclaration(Document ownerDocument, String name) {
		super(ownerDocument, NodeType.XML_DECLARATION, name);
	}

	@Override
	public XmlDeclaration clone() {
		return cloneTo(new XmlDeclaration(ownerDocument, nodeName));
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		ownerDocument.getRenderer().renderXmlDeclaration(this, appendable);
	}

}
