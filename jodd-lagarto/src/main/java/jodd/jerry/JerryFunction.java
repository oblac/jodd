// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jerry;

/**
 * Callback function for iterating nodes.
 */
public interface JerryFunction {

	/**
	 * Invoked on node. Returns <code>true</code> to continue looping.
	 */
	boolean onNode(Jerry $this, int index);
}
