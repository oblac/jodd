// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.io.IOException;

/**
 * CDATA node.
 */
public class CData extends Node {

	public CData(Document ownerDocument, String cdata) {
		super(ownerDocument, NodeType.CDATA, null);
		this.nodeValue = cdata;
	}

	@Override
	public CData clone() {
		return cloneTo(new CData(ownerDocument, nodeValue));
	}

	@Override
	public void toHtml(Appendable appendable) throws IOException {
		ownerDocument.getRenderer().renderCData(this, appendable);
	}

}