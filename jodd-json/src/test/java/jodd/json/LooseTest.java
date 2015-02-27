// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LooseTest {

	@Test
	public void testInvalidEscape() {
		try {
			assertEquals("ABC\\D", new JsonParser().parse("\"ABC\\D\""));
			fail();
		} catch (JsonException ignore) {
		}
		assertEquals("ABC\\D", new JsonParser().looseMode(true).parse("\"ABC\\D\""));

//		Map<String, Object> map = new JsonParser().looseMode(true).parse("{\"foo\": \"bar\\\"}");
//		assertEquals(1, map.size());
//		assertEquals("bar\\", map.get("foo"));
	}

	@Test
	public void testQuotes() {
		try {
			assertEquals("ABC", new JsonParser().parse("'ABC'"));
			fail();
		} catch (JsonException ignore) {
		}

		assertEquals("ABC", new JsonParser().looseMode(true).parse("'ABC'"));
		assertEquals("AB'C", new JsonParser().looseMode(true).parse("'AB\\'C'"));

		Map<String, Object> map = new JsonParser().looseMode(true).parse("{'foo':'BAR'}");

		assertEquals(1, map.size());
		assertEquals("BAR", map.get("foo"));
	}

	@Test
	public void testUnquotes() {
		Map<String, Object> map = new JsonParser().looseMode(true).parse("{foo: BAR , who : me}");

		assertEquals(2, map.size());
		assertEquals("BAR", map.get("foo"));
		assertEquals("me", map.get("who"));

		try {
			new JsonParser().looseMode(true).parse("{foo: BAR , who : m\te}");
			fail();
		} catch (JsonException ignore) {
		}
	}

}