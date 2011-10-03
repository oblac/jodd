// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import junit.framework.TestCase;

public class TagTypeTest extends TestCase {

	public void testOpen() {
		assertTrue(TagType.OPEN.isOpeningTag());
		assertTrue(TagType.EMPTY.isOpeningTag());
		assertFalse(TagType.CLOSE.isOpeningTag());
	}

	public void testClose() {
		assertTrue(TagType.CLOSE.isClosingTag());
		assertTrue(TagType.EMPTY.isClosingTag());
		assertFalse(TagType.OPEN.isClosingTag());
	}
}
