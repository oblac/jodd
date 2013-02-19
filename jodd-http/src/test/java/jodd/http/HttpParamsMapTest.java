// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class HttpParamsMapTest {

	@Test
	public void testMultipleValuesSameType() {
		HttpParamsMap httpParamsMap = new HttpParamsMap();

		httpParamsMap.put("one", "1");

		assertEquals("1", httpParamsMap.get("one"));


		httpParamsMap.put("one", "2");

		String[] values = (String[]) httpParamsMap.get("one");

		assertEquals(2, values.length);
		assertEquals("1", values[0]);
		assertEquals("2", values[1]);


		httpParamsMap.put("one", "3");

		values = (String[]) httpParamsMap.get("one");

		assertEquals(3, values.length);
		assertEquals("1", values[0]);
		assertEquals("2", values[1]);
		assertEquals("3", values[2]);
	}

	@Test
	public void testDoubleValuesDifferentType() {
		HttpParamsMap httpParamsMap = new HttpParamsMap();

		httpParamsMap.put("one", "1");

		assertEquals("1", httpParamsMap.get("one"));

		try {
			httpParamsMap.put("one", Integer.valueOf(2));
			fail();
		}
		catch (HttpException hex) {

		}
	}

	@Test
	public void testMultipleValuesDifferentType() {
		HttpParamsMap httpParamsMap = new HttpParamsMap();

		httpParamsMap.put("one", "1");
		httpParamsMap.put("one", "2");

		try {
			httpParamsMap.put("one", Integer.valueOf(3));
			fail();
		}
		catch (HttpException hex) {

		}
	}

	@Test
	public void testNullValues() {
		HttpParamsMap httpParamsMap = new HttpParamsMap();

		httpParamsMap.put("one", null);

		assertNull(httpParamsMap.get("one"));

		httpParamsMap.put("one", null);

		assertNull(httpParamsMap.get("one"));

		httpParamsMap.put("one", "1");

		assertEquals("1", httpParamsMap.get("one"));
	}

}