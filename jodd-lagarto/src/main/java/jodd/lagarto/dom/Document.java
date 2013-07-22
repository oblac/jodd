// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * Document node is always a root node.
 */
public class Document extends Node {

	protected Document(LagartoDOMBuilder domBuilder) {
		super(domBuilder, NodeType.DOCUMENT, null);
	}
	
	@Override
	public Document clone() {
		return cloneTo(new Document(domBuilder));
	}

}
