// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

/**
 * Callback function for iterating nodes.
 */
public interface JerryFunction {

	/**
	 * Invoked on node. Returns <code>true</code> to continue looping.
	 */
	boolean onNode(Jerry $this, int index);
}
