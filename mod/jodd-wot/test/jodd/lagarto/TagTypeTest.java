// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import junit.framework.TestCase;

public class TagTypeTest extends TestCase {

	public void testOpen() {
		assertTrue(TagType.START.isStartingTag());
		assertTrue(TagType.SELF_CLOSING.isStartingTag());
		assertFalse(TagType.END.isStartingTag());
	}

	public void testClose() {
		assertTrue(TagType.END.isEndingTag());
		assertTrue(TagType.SELF_CLOSING.isEndingTag());
		assertFalse(TagType.START.isEndingTag());
	}
}
