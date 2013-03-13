// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.HtmlDecoder;
import jodd.util.HtmlEncoder;
import jodd.util.StringUtil;

import java.io.IOException;

public class Text extends Node {

	public Text() {
		this(null);
	}

	public Text(String text) {
		super(NodeType.TEXT, null, true);
		this.nodeValue = text;
	}

	@Override
	public Text clone() {
		return cloneTo(new Text(nodeValue));
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
	 * Sets HTML text, but encodes it first.
	 */
	public void setTextContent(String text) {
		nodeValue = HtmlEncoder.text(text);
	}

	/**
	 * Returns decoded HTML text.
	 */
	@Override
	public String getTextContent() {
		return HtmlDecoder.decode(nodeValue);
	}

	/**
	 * Appends the text content to a StringBuilder.
	 */
	@Override
	public void appendTextContent(StringBuilder sb) {
		sb.append(getTextContent());
	}

	/**
	 * Sets HTML text, but encodes it first.
	 */
	public void setTextStrict(String text) {
		nodeValue = HtmlEncoder.strict(text);
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		appendable.append(nodeValue);
	}
}
