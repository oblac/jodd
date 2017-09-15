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

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class HttpMultiMapTest {

	@Test
	public void testAdd() {
		HttpMultiMap<String> mm = HttpMultiMap.newCaseInsensitiveMap();

		mm.add("One", "one");
		mm.add("Two", "two");

		assertEquals(2, mm.size());
		assertEquals("one", mm.get("one"));
		assertEquals("two", mm.get("two"));
	}

	@Test
	public void testAddSameName() {
		HttpMultiMap<String> mm = HttpMultiMap.newCaseInsensitiveMap();

		mm.add("One", "one");
		mm.add("one", "two");

		assertEquals(1, mm.size());
		assertEquals("two", mm.get("one"));

		List<String> all = mm.getAll("one");
		assertEquals(2, all.size());
		assertEquals("one", all.get(0));
		assertEquals("two", all.get(1));

		mm.add("one", "three");
		all = mm.getAll("one");
		assertEquals(3, all.size());
		assertEquals("one", all.get(0));
		assertEquals("two", all.get(1));
		assertEquals("three", all.get(2));
	}

	@Test
	public void testMissing() {
		HttpMultiMap<String> mm = HttpMultiMap.newCaseInsensitiveMap();

		assertNull(mm.get("xxx"));
	}

	@Test
	public void testIterator() {
		HttpMultiMap<String> mm = HttpMultiMap.newCaseInsensitiveMap();

		mm.add("One", "one");
		mm.add("one", "two");
		mm.add("two", "2.");
		mm.add("one", "three");

		assertEquals(2, mm.size());

		Iterator<Map.Entry<String, String>> i = mm.iterator();

		assertTrue(i.hasNext());
		assertEquals("one", i.next().getValue());
		assertEquals("two", i.next().getValue());
		assertEquals("2.", i.next().getValue());
		assertEquals("three", i.next().getValue());
		assertFalse(i.hasNext());

		try {
			i.next();
			fail("error");
		} catch (Exception ignore) {}

		mm.clear();
		i = mm.iterator();
		assertFalse(i.hasNext());
	}


	@Test
	public void testNullValues() {
		HttpMultiMap<String> hmm = HttpMultiMap.newCaseInsensitiveMap();

		assertFalse(hmm.contains("one"));

		hmm.add("one", null);

		assertNull(hmm.get("one"));
		assertTrue(hmm.contains("one"));

		hmm.add("one", null);

		assertNull(hmm.get("one"));
		assertTrue(hmm.contains("one"));

		hmm.set("one", "1");

		assertEquals("1", hmm.get("one"));
		assertTrue(hmm.contains("one"));
	}

	@Test
	public void testParametersNumber() {
		HttpMultiMap<String> hmm = HttpMultiMap.newCaseInsensitiveMap();

		for (int i = 0; i < 30; i++) {
			hmm.add(String.valueOf(i), "!" + i);
		}

		assertEquals(30, hmm.size());
	}

	@Test
	public void testLetterCaseInsensitive() {
		HttpMultiMap<String> mm = HttpMultiMap.newCaseInsensitiveMap();

		mm.add("one", "1.1");
		mm.add("one", "1.1.1");
		mm.add("One", "1.2");

		assertEquals(1, mm.size());

		assertEquals("1.2", mm.get("one"));
		assertEquals("1.2", mm.get("ONE"));
		assertEquals("1.2", mm.get("One"));

		List<String> list = mm.getAll("ONE");

		assertEquals(3, list.size());

		assertEquals(1, mm.names().size());
		assertEquals(3, mm.entries().size());
	}

	@Test
	public void testLetterCaseSensitive() {
		HttpMultiMap<String> mm = HttpMultiMap.newCaseSensitiveMap();

		mm.add("one", "1.1");
		mm.add("one", "1.1.1");
		mm.add("One", "1.2");

		assertEquals(2, mm.size());

		assertEquals("1.1.1", mm.get("one"));
		assertNull(mm.get("ONE"));
		assertEquals("1.2", mm.get("One"));

		List<String> list = mm.getAll("ONE");
		assertEquals(0, list.size());

		list = mm.getAll("one");
		assertEquals(2, list.size());

		assertEquals(2, mm.names().size());
		assertEquals(3, mm.entries().size());
	}

}
