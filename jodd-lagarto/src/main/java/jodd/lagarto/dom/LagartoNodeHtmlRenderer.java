//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.TagWriterUtil;
import jodd.util.HtmlEncoder;

import java.io.IOException;

/**
 * Pretty HTML code generator from {@link Node}.
 */
public class LagartoNodeHtmlRenderer {

	/**
	 * Renders generic node value.
	 */
	public void renderNodeValue(Node node, Appendable appendable) throws IOException {
		String nodeValue = node.getNodeValue();

		if (nodeValue != null) {
			appendable.append(nodeValue);
		}
	}

	/**
	 * Renders {@link CData cdata}.
	 */
	public void renderCData(CData cdata, Appendable appendable) throws IOException {
		String nodeValue = cdata.getNodeValue();
		TagWriterUtil.writeCData(appendable, nodeValue);
	}

	/**
	 * Renders {@link Comment comments}.
	 */
	public void renderComment(Comment comment, Appendable appendable) throws IOException {
		String nodeValue = comment.getNodeValue();

		if (comment.conditionalDownlevelHidden == null) {
			TagWriterUtil.writeComment(appendable, nodeValue);
		} else {
			TagWriterUtil.writeConditionalComment(
					appendable, nodeValue, comment.isStartingTag,
					comment.conditionalDownlevelHidden.booleanValue(), comment.additionalComment);
		}
	}

	/**
	 * Renders {@link DocumentType}.
	 */
	public void renderDocumentType(DocumentType documentType, Appendable appendable) throws IOException {
		TagWriterUtil.writeDoctype(appendable,
				documentType.nodeValue, documentType.publicId, documentType.baseUri);
	}

	/**
	 * Renders {@link Text text} nodes.
	 */
	public void renderText(Text text, Appendable appendable) throws IOException {
		String nodeValue = text.getNodeValue();

		appendable.append(nodeValue);
	}

	/**
	 * Renders {@link XmlDeclaration} nodes.
	 */
	public void renderXmlDeclaration(XmlDeclaration xmlDeclaration, Appendable appendable) throws IOException {
		String nodeName = xmlDeclaration.getNodeName();

		appendable.append("<?");
		appendable.append(nodeName);

		int attrCount = xmlDeclaration.getAttributesCount();
		if (attrCount != 0) {
			for (int i = 0; i < attrCount; i++) {
				Attribute attr = xmlDeclaration.getAttribute(i);
				appendable.append(' ');
				renderAttribute(xmlDeclaration, attr, appendable);
			}
		}
		appendable.append("?>");
	}


	// ---------------------------------------------------------------- element

	/**
	 * Enumeration of case options for output name.
	 */
	public enum Case {
		/**
		 * Default case, depends on {@link LagartoDOMBuilder#setCaseSensitive(boolean) case sensitivity}
		 * flag of builder. May be either lowercase or raw.
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
	public void setTagCase(Case tagCase) {
		this.tagCase = tagCase;
	}

	public Case getTagCase() {
		return tagCase;
	}

	/**
	 * Sets {@link Case case} of attribute names.
	 */
	public void setAttributeCase(Case attributeCase) {
		this.attributeCase = attributeCase;
	}

	public Case getAttributeCase() {
		return attributeCase;
	}

	protected boolean attributeValuePreserveSingleQuote = true;

	public boolean isAttributeValuePreserveSingleQuote() {
		return attributeValuePreserveSingleQuote;
	}

	/**
	 * Defines if attribute values should encode apostrophe or not.
	 */
	public void setAttributeValuePreserveSingleQuote(boolean attributeValuePreserveSingleQuote) {
		this.attributeValuePreserveSingleQuote = attributeValuePreserveSingleQuote;
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
	protected String resolveNodeName(Node node) {
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
	protected String resolveAttributeName(Node node, Attribute attribute) {
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
	protected void renderAttribute(Node node, Attribute attribute, Appendable appendable) throws IOException {
		String name = resolveAttributeName(node, attribute);
		String value = attribute.getValue();

		appendable.append(name);
		if (value != null) {
			appendable.append('=');
			appendable.append('\"');
			if (attributeValuePreserveSingleQuote) {
				appendable.append(HtmlEncoder.attribute(value));
			} else {
				appendable.append(HtmlEncoder.text(value));
			}
			appendable.append('\"');
		}
	}

	/**
	 * Renders single element.
	 */
	public void renderElement(Element element, Appendable appendable) throws IOException {
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
			renderElementBody(element, appendable);
		}

		appendable.append("</");
		appendable.append(nodeName);
		appendable.append('>');
	}

	/**
	 * Renders element body.
	 */
	protected void renderElementBody(Element element, Appendable appendable) throws IOException {
		element.toInnerHtml(appendable);
	}

}