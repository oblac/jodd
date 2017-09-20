// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.servlet.map;

import jodd.util.collection.ArrayEnumeration;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpServletRequestParamMapTest {

	@Test
	public void testParamMap() {
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterValues("a")).thenReturn(new String[] {"1"});
		when(servletRequest.getParameterValues("b")).thenReturn(new String[] {"2", "22"});
		when(servletRequest.getParameterNames()).thenReturn(
			new ArrayEnumeration<>(new String[] {"a", "b"}));

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
			fail("error");
		} catch (UnsupportedOperationException uoex) {
		}
	}
}
