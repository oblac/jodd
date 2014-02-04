// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.map;

import jodd.util.collection.ArrayEnumeration;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpServletRequestParamMapTest {

	@Test
	public void testParamMap() {
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterValues("a")).thenReturn(new String[] {"1"});
		when(servletRequest.getParameterValues("b")).thenReturn(new String[] {"2", "22"});
		when(servletRequest.getParameterNames()).thenReturn(
				new ArrayEnumeration<String>(new String[]{"a", "b"}));

		HttpServletRequestParamMap map = new HttpServletRequestParamMap(servletRequest);

		assertEquals("1", map.get("a"));
		assertEquals("2", ((String[]) map.get("b"))[0]);
		assertEquals("22", ((String[]) map.get("b"))[1]);

		Set<Map.Entry<String, Object>> set = map.entrySet();
		assertEquals(2, set.size());

		Iterator<Map.Entry<String, Object>> iterator = set.iterator();
		assertTrue(iterator.hasNext());

		Map.Entry<String, Object> entry = iterator.next();
		assertNotNull(entry);
		assertTrue(entry.getKey().equals("a") || entry.getKey().equals("b"));

		assertTrue(iterator.hasNext());
		entry = iterator.next();
		assertNotNull(entry);
		assertTrue(entry.getKey().equals("a") || entry.getKey().equals("b"));

		assertFalse(iterator.hasNext());

		try {
			map.put("a", "foo");
			fail();
		} catch (UnsupportedOperationException uoex) {
		}
	}
}