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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test primitive arrays
 */
public class PrimitiveArrayListTest {

	@Test
	public void testIntArrayList() {
		IntArrayList list = new IntArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add(1);
		list.add(2);
		list.add(3);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertEquals(1, list.get(0));
		assertEquals(3, list.get(2));
		list.add(0, 4);
		assertEquals(1, list.get(1));
		assertEquals(4, list.get(0));

		assertEquals(1, list.lastIndexOf(1));
		assertEquals(1, list.indexOf(1));

		list.addAll(new int[]{9, 8, 7});
		assertEquals(7, list.size());
		assertEquals(9, list.get(4));
		assertEquals(7, list.get(6));

		list.addAll(3, new int[]{-1, -2});
		assertEquals(9, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-1, list.get(3));
		assertEquals(-2, list.get(4));
		assertEquals(3, list.get(5));
		assertEquals(9, list.get(6));
		assertEquals(7, list.get(8));

		list.set(3, -3);
		assertEquals(-3, list.get(3));

		list.remove(3);
		assertEquals(8, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-2, list.get(3));
		assertEquals(3, list.get(4));
		assertEquals(9, list.get(5));
		assertEquals(7, list.get(7));

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertEquals(2, list.get(2));
		assertEquals(9, list.get(3));
		assertEquals(7, list.get(5));

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new IntArrayList(new int[]{1, 2, 3, 4, 5, 6});
		assertTrue(list.contains(4));
		assertFalse(list.contains(111));
	}

}
