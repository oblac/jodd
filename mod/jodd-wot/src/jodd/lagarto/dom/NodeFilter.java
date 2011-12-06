// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * Node filter.
 */
public interface NodeFilter {

	/**
	 * Test whether a specified node is acceptable in some selection process.
	 */
	boolean accept(Node node);
}
