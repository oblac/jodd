// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.Util;

import java.io.IOException;

/**
 * XML declaration node.
 */
public class XmlDeclaration extends Node {

	protected String version;
	protected String encoding;
	protected String standalone;

	public XmlDeclaration(Document ownerDocument, CharSequence version, CharSequence encoding, CharSequence standalone) {
		super(ownerDocument, NodeType.XML_DECLARATION, "xml");

		this.version = Util.toString(version);
		this.encoding = Util.toString(encoding);
		this.standalone = Util.toString(standalone);
	}

	public String getVersion() {
		return version;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getStandalone() {
		return standalone;
	}

	@Override
	public XmlDeclaration clone() {
		return cloneTo(new XmlDeclaration(ownerDocument, version, encoding, standalone));
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		ownerDocument.getRenderer().renderXmlDeclaration(this, appendable);
	}

}
