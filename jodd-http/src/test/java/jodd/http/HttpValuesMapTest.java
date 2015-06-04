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

package jodd.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HttpValuesMapTest {

	@Test
	public void testMultipleValuesSameType() {
		HttpValuesMap<String> httpValuesMap = HttpValuesMap.ofStrings();

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
		HttpValuesMap<Object> httpValuesMap = HttpValuesMap.ofObjects();

		httpValuesMap.add("one", "1");
		httpValuesMap.add("one", "2");
		httpValuesMap.add("one", Integer.valueOf(3));

		assertEquals("3", httpValuesMap.getStrings("one")[2]);
	}

	@Test
	public void testNullValues() {
		HttpValuesMap<String> httpValuesMap = HttpValuesMap.ofStrings();

		httpValuesMap.put("one", null);

		assertNull(httpValuesMap.get("one"));

		httpValuesMap.put("one", null);

		assertNull(httpValuesMap.get("one"));

		httpValuesMap.add("one", "1");

		assertEquals("1", httpValuesMap.get("one")[0]);
	}

}