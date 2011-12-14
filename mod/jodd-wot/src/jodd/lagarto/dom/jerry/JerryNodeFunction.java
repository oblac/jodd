// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

import jodd.lagarto.dom.Node;

/**
 * Callback function for iterating nodes.
 */
public interface JerryNodeFunction {

	/**
	 * Invoked on node. Returns <code>true</code> to continue looping.
	 */
	boolean onNode(Node node, int index);
}
