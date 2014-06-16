// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

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
	protected void visitNode(NodeVisitor nodeVisitor) {
		nodeVisitor.cdata(this);
	}
}