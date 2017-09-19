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

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayEnumerationTest {

	@Test
	public void testEnumeration() {
		Integer[] i = new Integer[]{1, 2, 3, 4, 5};

		ArrayEnumeration<Integer> ae = new ArrayEnumeration<>(i);
		assertTrue(ae.hasMoreElements());
		assertEquals("1", ae.nextElement().toString());
		assertEquals("2", ae.nextElement().toString());
		assertEquals("3", ae.nextElement().toString());
		assertEquals("4", ae.nextElement().toString());
		assertTrue(ae.hasMoreElements());
		assertEquals("5", ae.nextElement().toString());
		assertFalse(ae.hasMoreElements());

		try {
			ae.nextElement();
			fail("error");
		} catch (NoSuchElementException nseex) {
			// ignore
		}

	}

	@Test
	public void testEnumerationFrom() {
		Integer[] i = new Integer[]{1, 2, 3, 4, 5};

		ArrayEnumeration<Integer> ae = new ArrayEnumeration<>(i, 2, 2);
		assertTrue(ae.hasMoreElements());
		assertEquals("3", ae.nextElement().toString());
		assertEquals("4", ae.nextElement().toString());
		assertFalse(ae.hasMoreElements());

		try {
			ae.nextElement();
			fail("error");
		} catch (NoSuchElementException nseex) {
			// ignore
		}

	}
}
