// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.UnsafeUtil;

/**
 * Parses HTML/XML content using {@link TagVisitor}.
 */
public class LagartoParser extends LagartoParserEngine {

	public LagartoParser(char[] charArray) {
		initialize(charArray);
	}

	public LagartoParser(String string) {
		initialize(UnsafeUtil.getChars(string));
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses content using provided {@link TagVisitor}.
	 */
	@Override
	public void parse(TagVisitor visitor) {
		super.parse(visitor);
	}

}
