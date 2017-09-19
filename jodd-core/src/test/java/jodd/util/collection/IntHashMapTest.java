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

package jodd.util.collection;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntHashMapTest {

	@Test
	public void testIntHashMap() {
		IntHashMap ihm = new IntHashMap();

		assertTrue(ihm.isEmpty());

		for (int i = 0; i < 10000; i++) {
			ihm.put(i, new Integer(i));
		}

		assertEquals(10000, ihm.size());
		assertFalse(ihm.isEmpty());

		for (int i = 0; i < 10000; i++) {
			assertEquals(i, ((Integer) ihm.get(i)).intValue());
		}

		assertTrue(ihm.containsKey(1));
		assertTrue(ihm.containsValue(Integer.valueOf(173)));

		IntHashMap ihm2 = ihm.clone();

		assertEquals(10000, ihm2.size());

		ihm.remove(1);

		assertEquals(9999, ihm.size());
		assertEquals(10000, ihm2.size());

		ihm.clear();

		assertTrue(ihm.isEmpty());

		ihm.put(Integer.valueOf("123"), "Xxx");
		assertEquals("Xxx", ihm.get(123));

		Set<Integer> set = ihm.keySet();
		for (Integer i : set) {
			assertEquals(123, i.intValue());
		}

		for (Map.Entry<Integer, Object> entry : ihm.entrySet()) {
			assertEquals(123, entry.getKey().intValue());
			assertEquals("Xxx", entry.getValue());
		}
	}

}
