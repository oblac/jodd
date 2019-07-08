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

import jodd.lagarto.TagWriterUtil;
import jodd.net.HtmlEncoder;

import java.io.IOException;

/**
 * {@link jodd.lagarto.dom.NodeVisitor} that renders DOM tree to string.
 */
public class LagartoHtmlRendererNodeVisitor implements NodeVisitor {

	protected final Appendable appendable;

	public LagartoHtmlRendererNodeVisitor(final Appendable appendable) {
		this.appendable = appendable;
	}

	@Override
	public void cdata(final CData cdata) {
		String nodeValue = cdata.getNodeValue();
		try {
			TagWriterUtil.writeCData(appendable, nodeValue);
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
	}

	@Override
	public void comment(final Comment comment) {
		String nodeValue = comment.getNodeValue();

		try {
			TagWriterUtil.writeComment(appendable, nodeValue);
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
	}

	@Override
	public void document(final Document document) {
		document.visitChildren(this);
	}

	@Override
	public void documentType(final DocumentType documentType) {
		try {
			TagWriterUtil.writeDoctype(appendable,
					documentType.nodeValue,
					documentType.publicId,
					documentType.systemId);
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
	}

	@Override
	public void text(final Text text) {
		String nodeValue = text.getTextValue();

		try {
			appendable.append(nodeValue);
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
	}

	@Override
	public void xmlDeclaration(final XmlDeclaration xmlDeclaration) {
		try {
			TagWriterUtil.writeXml(appendable,
					xmlDeclaration.getVersion(), xmlDeclaration.getEncoding(), xmlDeclaration.getStandalone());
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
	}

	// ---------------------------------------------------------------- element

	/**
	 * Enumeration of case options for output name.
	 */
	public enum Case {
		/**
		 * Default case, depends on parser case sensitivity.
		 */
		DEFAULT,
		/**
		 * Raw name, no modifications.
		 */
		RAW,
		/**
		 * Lowercase name.
		 */
		LOWERCASE,
		/**
		 * Uppercase name.
		 */
		UPPERCASE,
	}

	/**
	 * Letter case of tag names.
	 */
	protected Case tagCase = Case.DEFAULT;

	/**
	 * Letter case of attributes names.
	 */
	protected Case attributeCase = Case.DEFAULT;

	/**
	 * Sets {@link Case case} of tag names.
	 */
	public void setTagCase(final Case tagCase) {
		this.tagCase = tagCase;
	}

	public Case getTagCase() {
		return tagCase;
	}

	/**
	 * Sets {@link Case case} of attribute names.
	 */
	public void setAttributeCase(final Case attributeCase) {
		this.attributeCase = attributeCase;
	}

	public Case getAttributeCase() {
		return attributeCase;
	}

	/**
	 * Resets all cases to default.
	 */
	public void reset() {
		tagCase = Case.DEFAULT;
		attributeCase = Case.DEFAULT;
	}

	/**
	 * Renders node name.
	 */
	protected String resolveNodeName(final Node node) {
		switch (tagCase) {
			case DEFAULT: return node.getNodeName();
			case RAW: return node.getNodeRawName();
			case LOWERCASE: return node.getNodeRawName().toLowerCase();
			case UPPERCASE: return node.getNodeRawName().toUpperCase();
		}
		return null;
	}

	/**
	 * Renders attribute name.
	 */
	protected String resolveAttributeName(final Node node, final Attribute attribute) {
		switch (attributeCase) {
			case DEFAULT: return attribute.getName();
			case RAW: return attribute.getRawName();
			case LOWERCASE: return attribute.getRawName().toLowerCase();
			case UPPERCASE: return attribute.getRawName().toUpperCase();
		}
		return null;
	}

	/**
	 * Renders attribute.
	 */
	protected void renderAttribute(final Node node, final Attribute attribute, final Appendable appendable) throws IOException {
		String name = resolveAttributeName(node, attribute);
		String value = attribute.getValue();

		appendable.append(name);
		if (value != null) {
			appendable.append('=');
			appendable.append('\"');
			appendable.append(HtmlEncoder.attributeDoubleQuoted(value));
			appendable.append('\"');
		}
	}

	@Override
	public void element(final Element element) {
		try {
			_element(element);
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
	}

	protected void _element(final Element element) throws IOException {
		String nodeName = resolveNodeName(element);

		appendable.append('<');
		appendable.append(nodeName);

		int attrCount = element.getAttributesCount();

		if (attrCount != 0) {
			for (int i = 0; i < attrCount; i++) {
				Attribute attr = element.getAttribute(i);
				appendable.append(' ');
				renderAttribute(element, attr, appendable);
			}
		}

		int childCount = element.getChildNodesCount();

		if (element.selfClosed && childCount == 0) {
			appendable.append("/>");
			return;
		}

		appendable.append('>');

		if (element.voidElement) {
			return;
		}

		if (childCount != 0) {
			elementBody(element);
		}

		appendable.append("</");
		appendable.append(nodeName);
		appendable.append('>');
	}

	protected void elementBody(final Element element) throws IOException {
		int childCount = element.getChildNodesCount();

		if (element.isRawTag()) {
			for (int i = 0; i < childCount; i++) {
				Node childNode = element.getChild(i);

				if (childNode.getNodeType() == Node.NodeType.TEXT) {
					appendable.append(childNode.getNodeValue());
				} else {
					childNode.visit(this);
				}
			}
		} else {
			element.visitChildren(this);
		}
	}

}