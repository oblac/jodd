// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.lagarto.TagUtil;
import jodd.util.HtmlDecoder;
import jodd.util.HtmlEncoder;
import jodd.util.StringUtil;

import java.io.IOException;

public class Text extends Node {

	public Text(Document ownerDocument, String text) {
		super(ownerDocument, NodeType.TEXT, null);
		this.nodeValue = text;
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
	 * Sets HTML text, but decodes it first.
	 */
	public void setTextContent(String text) {
		nodeValue = HtmlDecoder.decode(text);		// todo do we need this?
	}

	/**
	 * Returns encoded HTML text.
	 */
	@Override
	public String getTextContent() {
		Node parentNode = getParentNode();

		String nodeName = parentNode.getNodeName();

		if (nodeName != null && TagUtil.isRawTagName(nodeName)) {
			return nodeValue;
		}

		return HtmlEncoder.text(nodeValue);
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

	/**
	 * Sets HTML text, but encodes it first.
	 */
	public void setTextStrict(String text) {
		nodeValue = HtmlEncoder.strict(text);
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		ownerDocument.getRenderer().renderText(this, appendable);
	}
}
