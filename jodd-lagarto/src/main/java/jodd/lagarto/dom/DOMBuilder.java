// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

/**
 * DOM Builder. Generic interface for DOM builder
 * implementation.
 */
public interface DOMBuilder {

	/**
	 * Parses content and returns root {@link Document document node}.
	 */
	Document parse(char[] content);

	/**
	 * Parses content and returns root {@link Document document node}.
	 */
	Document parse(String content);

}