// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

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

		this.version = version != null ? version.toString() : null;		// todo to JoddScript
		this.encoding = encoding != null ? encoding.toString() : null;
		this.standalone = standalone != null ?  standalone.toString() : null;
	}

	public XmlDeclaration(Document ownerDocument, String name) {
		super(ownerDocument, NodeType.XML_DECLARATION, name);
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
		return cloneTo(new XmlDeclaration(ownerDocument, nodeName));
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		ownerDocument.getRenderer().renderXmlDeclaration(this, appendable);
	}

}
