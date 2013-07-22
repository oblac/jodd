// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;

import java.io.IOException;

/**
 * XML declaration node.
 */
public class XmlDeclaration extends Node {

	protected XmlDeclaration(LagartoDOMBuilder domBuilder, Tag tag) {
		super(domBuilder, NodeType.XML_DECLARATION, tag.getName());

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = tag.getAttributeName(i);
			String value = tag.getAttributeValue(i);
			setAttribute(key, value);
		}
	}

	protected XmlDeclaration(LagartoDOMBuilder domBuilder, String name) {
		super(domBuilder, NodeType.XML_DECLARATION, name);
	}

	@Override
	public XmlDeclaration clone() {
		return cloneTo(new XmlDeclaration(domBuilder, nodeName));
	}


	@Override
	public void toHtml(Appendable appendable) throws IOException {
		appendable.append("<?");
		appendable.append(nodeName);

		int attrCount = getAttributesCount();
		if (attrCount != 0) {
			for (int i = 0; i < attrCount; i++) {
				Attribute attr = getAttribute(i);
				appendable.append(' ');
				attr.toHtml(appendable);
			}
		}
		appendable.append("?>");
	}

}
