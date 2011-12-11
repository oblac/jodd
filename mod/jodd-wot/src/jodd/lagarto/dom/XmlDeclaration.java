// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.Tag;

import java.io.IOException;

/**
 * XML declaration node.
 */
public class XmlDeclaration extends Node {

	public XmlDeclaration(Tag tag, boolean caseSensitive) {
		super(NodeType.XML_DECLARATION, tag.getName(), caseSensitive);

		int attrCount = tag.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String key = tag.getAttributeName(i);
			String value = tag.getAttributeValue(i);
			setAttribute(key, value);
		}
	}

	public XmlDeclaration(String name) {
		this(name, false);
	}

	public XmlDeclaration(String name, boolean caseSensitive) {
		super(NodeType.XML_DECLARATION, name, caseSensitive);
	}

	@Override
	public XmlDeclaration clone() {
		return cloneTo(new XmlDeclaration(nodeName, caseSensitive));
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
