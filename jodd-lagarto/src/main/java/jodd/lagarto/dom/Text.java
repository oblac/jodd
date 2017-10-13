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

import jodd.util.HtmlDecoder;
import jodd.util.HtmlEncoder;
import jodd.util.StringUtil;

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
	public void setTextValue(String text) {
		encodedText = text;
		nodeValue = HtmlDecoder.decode(text);
	}

	/**
	 * Returns encoded HTML text.
	 */
	public String getTextValue() {
		if (encodedText == null) {
			encodedText = HtmlEncoder.text(nodeValue);
		}
		return encodedText;
	}

	@Override
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.text(this);
	}
}
