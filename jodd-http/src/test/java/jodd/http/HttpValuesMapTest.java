// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HttpValuesMapTest {

	@Test
	public void testMultipleValuesSameType() {
		HttpValuesMap httpValuesMap = new HttpValuesMap();

		httpValuesMap.add("one", "1");

		assertEquals("1", httpValuesMap.get("one")[0]);


		httpValuesMap.add("one", "2");

		String[] values = httpValuesMap.getStrings("one");

		assertEquals(2, values.length);
		assertEquals("1", values[0]);
		assertEquals("2", values[1]);


		httpValuesMap.add("one", "3");

		values = httpValuesMap.getStrings("one");

		assertEquals(3, values.length);
		assertEquals("1", values[0]);
		assertEquals("2", values[1]);
		assertEquals("3", values[2]);
	}

	@Test
	public void testMultipleValuesDifferentType() {
		HttpValuesMap httpValuesMap = new HttpValuesMap();

		httpValuesMap.add("one", "1");
		httpValuesMap.add("one", "2");
		httpValuesMap.add("one", Integer.valueOf(3));

		assertEquals("3", httpValuesMap.getStrings("one")[2]);
	}

	@Test
	public void testNullValues() {
		HttpValuesMap httpValuesMap = new HttpValuesMap();

		httpValuesMap.put("one", null);

		assertNull(httpValuesMap.get("one"));

		httpValuesMap.put("one", null);

		assertNull(httpValuesMap.get("one"));

		httpValuesMap.add("one", "1");

		assertEquals("1", httpValuesMap.get("one")[0]);
	}

}