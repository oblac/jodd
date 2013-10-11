// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.nio.CharBuffer;

/**
 * DOM Builder. Generic interface for DOM builder
 * implementation.
 */
public interface DOMBuilder {

	/**
	 * Parses content and returns root {@link Document document node}.
	 */
	Document parse(CharSequence content);

	/**
	 * Parses content and returns root {@link Document document node}.
	 */
	Document parse(CharBuffer content);

}