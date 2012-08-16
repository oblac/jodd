// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * Document node is always a root node.
 */
public class Document extends Node {

	public Document() {
		super(NodeType.DOCUMENT, null, true);
	}
	
	@Override
	public Document clone() {
		return cloneTo(new Document());
	}

}
