// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.nio.CharBuffer;

/**
 * Parses HTML/XML content using {@link TagVisitor}.
 */
public class LagartoParser extends LagartoParserEngine {

	public LagartoParser(char[] charArray) {
		this(CharBuffer.wrap(charArray));
	}

	public LagartoParser(CharSequence charSequence) {
		this(CharBuffer.wrap(charSequence));
	}

	public LagartoParser(CharBuffer input) {
		initialize(input);
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
