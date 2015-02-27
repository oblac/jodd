// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathQueryTest {

	@Test
	public void testPathMatching() {
		assertTrue(new PathQuery("hello", true).matches(Path.parse("hello")));
		assertFalse(new PathQuery("hello", true).matches(Path.parse("boom")));
		assertTrue(new PathQuery("hello.world", true).matches(Path.parse("hello.world")));
		assertFalse(new PathQuery("hello", true).matches(new Path()));
		assertTrue(new PathQuery("hello.*.world", true).matches(Path.parse("hello.jupiter.world")));
		assertTrue(new PathQuery("hello.*.world", true).matches(Path.parse("hello.earth.moon.world")));
		assertTrue(new PathQuery("*.class", true).matches(Path.parse("foo.class")));
		assertTrue(new PathQuery("*.class", true).matches(Path.parse("foo.bar.tzar.class")));
		assertFalse(new PathQuery("*.class", true).matches(Path.parse("foo.bar.tzar")));
		assertTrue(new PathQuery("*", true).matches(Path.parse("a.b.c.d")));
		assertTrue(new PathQuery("*.class.*", true).matches(Path.parse("a.b.class.d")));
		assertTrue(new PathQuery("*", true).matches(Path.parse("123.asd.234s.fsdre")));
		assertTrue(new PathQuery("*.*", true).matches(Path.parse("billy.bong.class.yeker")));
	}

	@Test
	public void testPathMatchingDifferences() {
		assertTrue(new PathQuery("one.two", true).matches(Path.parse("one")));
		assertTrue(new PathQuery("one.two.three", true).matches(Path.parse("one")));
		assertTrue(new PathQuery("one.two", true).matches(Path.parse("one.two")));
		assertFalse(new PathQuery("one.two", true).matches(Path.parse("one.two.three")));

		assertFalse(new PathQuery("one.two", false).matches(Path.parse("one")));
		assertTrue(new PathQuery("one.two", false).matches(Path.parse("one.two")));
		assertFalse(new PathQuery("one.two", false).matches(Path.parse("one.two.three")));
	}

}