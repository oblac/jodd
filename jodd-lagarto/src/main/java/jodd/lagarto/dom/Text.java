// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.HtmlDecoder;
import jodd.util.HtmlEncoder;
import jodd.util.StringUtil;

import java.io.IOException;

/**
 * Text node. Text value is stored as node value in decoded, readable form.
 * There is also an option to get and set <b>html content</b> in
 * raw, html form.
 */
public class Text extends Node {

	protected String encodedText;

	public Text(Document ownerDocument, String text) {
		super(ownerDocument, NodeType.TEXT, null);
		this.nodeValue = text;
		this.encodedText = null;
	}

	@Override
	public Text clone() {
		return cloneTo(new Text(ownerDocument, nodeValue));
	}
	
	protected Boolean blank;

	/**
	 * Returns <code>true</code> if text content is blank.
	 */
	public boolean isBlank() {
		if (blank == null) {
			blank = Boolean.valueOf(StringUtil.isBlank(nodeValue));
		}
		return blank.booleanValue();
	}

	/**
	 * Sets the plain text as node value.
	 */
	@Override
	public void setNodeValue(String value) {
		encodedText = null;
		super.setNodeValue(value);
	}

	/**
	 * Sets HTML text, but decodes it first.
	 */
	public void setTextContent(String text) {
		encodedText = text;
		nodeValue = HtmlDecoder.decode(text);
	}

	/**
	 * Returns encoded HTML text.
	 */
	@Override
	public String getTextContent() {
		if (encodedText == null) {
			encodedText = HtmlEncoder.text(nodeValue);
		}
		return encodedText;
	}

	/**
	 * Appends the text content to <code>Appendable</code>.
	 */
	@Override
	public void appendTextContent(Appendable appendable) {
		try {
			appendable.append(getTextContent());
		} catch (IOException ioex) {
			throw new LagartoDOMException(ioex);
		}
	}

	@Override
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.text(this);
	}
}
