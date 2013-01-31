// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TagTypeTest {

	@Test
	public void testOpen() {
		assertTrue(TagType.START.isStartingTag());
		assertTrue(TagType.SELF_CLOSING.isStartingTag());
		assertFalse(TagType.END.isStartingTag());
	}

	@Test
	public void testClose() {
		assertTrue(TagType.END.isEndingTag());
		assertTrue(TagType.SELF_CLOSING.isEndingTag());
		assertFalse(TagType.START.isEndingTag());
	}
}
