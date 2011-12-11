// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.servlet.HtmlDecoder;
import jodd.servlet.HtmlEncoder;

import java.io.IOException;

public class Text extends Node {

	public Text() {
		this(null);
	}

	public Text(String text) {
		super(NodeType.TEXT, null, true);
		this.nodeValue = text;
	}

	/**
	 * Returns decoded HTML text.
	 */
	public String getText() {
		return HtmlDecoder.decode(nodeValue);
	}

	/**
	 * Sets HTML text, but encodes it first.
	 */
	public void setText(String text) {
		nodeValue = HtmlEncoder.text(text);
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
