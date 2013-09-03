// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HttpParamsMapTest {

	@Test
	public void testMultipleValuesSameType() {
		HttpParamsMap httpParamsMap = new HttpParamsMap();

		httpParamsMap.add("one", "1");

		assertEquals("1", httpParamsMap.get("one")[0]);


		httpParamsMap.add("one", "2");

		String[] values = httpParamsMap.getStrings("one");

		assertEquals(2, values.length);
		assertEquals("1", values[0]);
		assertEquals("2", values[1]);


		httpParamsMap.add("one", "3");

		values = httpParamsMap.getStrings("one");

		assertEquals(3, values.length);
		assertEquals("1", values[0]);
		assertEquals("2", values[1]);
		assertEquals("3", values[2]);
	}

	@Test
	public void testMultipleValuesDifferentType() {
		HttpParamsMap httpParamsMap = new HttpParamsMap();

		httpParamsMap.add("one", "1");
		httpParamsMap.add("one", "2");
		httpParamsMap.add("one", Integer.valueOf(3));

		assertEquals("3", httpParamsMap.getStrings("one")[2]);
	}

	@Test
	public void testNullValues() {
		HttpParamsMap httpParamsMap = new HttpParamsMap();

		httpParamsMap.put("one", null);

		assertNull(httpParamsMap.get("one"));

		httpParamsMap.put("one", null);

		assertNull(httpParamsMap.get("one"));

		httpParamsMap.add("one", "1");

		assertEquals("1", httpParamsMap.get("one")[0]);
	}

}