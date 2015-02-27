// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PathTest {

	@Test
	public void testPaths() {
		Path path = new Path();

		assertEquals(0, path.length());
		assertEquals("[]", path.toString());

		path.push("one");
		assertEquals(1, path.length());
		assertEquals("[one]", path.toString());

		path.push("two");
		assertEquals(2, path.length());
		assertEquals("[one.two]", path.toString());

		path.push("three");
		assertEquals(3, path.length());
		assertEquals("[one.two.three]", path.toString());

		path.push("four");
		path.push("five");
		path.push("six");
		path.push("seven");
		path.push("eight");
		path.push("nine");
		path.push("ten");

		assertEquals(10, path.length());
		assertEquals("[one.two.three.four.five.six.seven.eight.nine.ten]", path.toString());

		assertEquals("ten", path.pop());
		assertEquals("nine", path.pop());
		assertEquals("eight", path.pop());
		assertEquals("seven", path.pop());
		assertEquals("six", path.pop());
		assertEquals("five", path.pop());

		assertEquals("[one.two.three.four]", path.toString());

		assertTrue(Path.parse("one.two.three.four").equals(path));
		assertEquals(Path.parse("one.two.three.four").hashCode(), path.hashCode());

		path.pop();

		assertFalse(Path.parse("one.two.three.four").equals(path));
		assertNotEquals(Path.parse("one.two.three.four").hashCode(), path.hashCode());

		assertFalse(Path.parse("one.two.thre").equals(path));
		assertNotEquals(Path.parse("one.two.thre").hashCode(), path.hashCode());
	}
}