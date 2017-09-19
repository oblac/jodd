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

package jodd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoopIteratorTest {

	@Test
	public void testUp() {
		LoopIterator ls = new LoopIterator(1, 5);

		assertTrue(ls.next());    // jump to 1
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertTrue(ls.next());    // 2
		assertTrue(ls.next());    // 3
		assertTrue(ls.next());    // 4
		assertTrue(ls.next());    // jump to 5

		assertFalse(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(5, ls.getValue());
		assertEquals(5, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertFalse(ls.next());

		ls.reset();

		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());
	}

	@Test
	public void testDown() {
		LoopIterator ls = new LoopIterator(5, 1, -1);

		assertTrue(ls.next());    // jump to 5
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(5, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertTrue(ls.next());
		assertTrue(ls.next());
		assertTrue(ls.next());
		assertTrue(ls.next());    // jump to 1

		assertFalse(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(5, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());

		assertFalse(ls.next());

		ls.reset();

		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertFalse(ls.isLast());
		assertEquals(5, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertEquals(0, ls.getIndexModulus());
	}

	@Test
	public void testSingle() {
		LoopIterator ls = new LoopIterator(1, 5, 20, 10);
		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertFalse(ls.next());

		ls = new LoopIterator(1, 1);
		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
		assertEquals(1, ls.getCount());
		assertFalse(ls.isEven());
		assertTrue(ls.isOdd());
		assertEquals(1, ls.getModulus());
		assertFalse(ls.next());

		ls = new LoopIterator(1, 1, -1);
		assertTrue(ls.next());
		assertTrue(ls.isFirst());
		assertTrue(ls.isLast());
		assertEquals(1, ls.getValue());
	}

	@Test
	public void testNone() {
		LoopIterator ls = new LoopIterator(2, 1);
		assertFalse(ls.next());

		ls = new LoopIterator(1, 3, -1);
		assertFalse(ls.next());
	}
}

