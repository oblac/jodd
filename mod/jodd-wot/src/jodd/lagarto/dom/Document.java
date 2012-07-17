// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.util.List;

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

	protected List<String> errors;

	/**
	 * Returns list of errors occurred during parsing.
	 * Returns <code>null</code> if parsing was
	 * successful; or if errors are not collected.
	 */
	public List<String> getErrors() {
		return errors;
	}
}
