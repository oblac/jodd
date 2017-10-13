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

package jodd.datetime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BeforeAfterTest {

	@Test
	public void testBefore() {
		JDateTime now = new JDateTime();
		JDateTime future = now.clone();
		future.addSecond(1);
		JDateTime future2 = now.clone();
		future2.addDay(1);

		assertFalse(now.isBefore(now));
		assertEquals(now, now);
		assertTrue(now.equalsDate(now));
		assertTrue(now.equalsTime(now));

		assertTrue(now.isBefore(future));
		assertFalse(future.isBefore(now));
		assertFalse(now.equals(future));
		assertTrue(now.equalsDate(future));
		assertFalse(now.equalsTime(future));
		assertFalse(now.isBeforeDate(future));
		assertFalse(future.isBeforeDate(now));

		assertTrue(now.isBefore(future2));
		assertFalse(future2.isBefore(now));
		assertTrue(now.isBeforeDate(future2));
		assertFalse(future2.isBeforeDate(now));
		assertFalse(now.equals(future2));
		assertFalse(now.equalsDate(future2));
		assertTrue(now.equalsTime(future2));
	}

	@Test
	public void testAfter() {
		JDateTime now = new JDateTime();
		JDateTime past = now.clone();
		past.subSecond(1);
		JDateTime past2 = now.clone();
		past2.subDay(1);

		assertFalse(now.isAfter(now));
		assertEquals(now, now);
		assertTrue(now.equalsDate(now));
		assertTrue(now.equalsTime(now));

		assertTrue(now.isAfter(past));
		assertFalse(past.isAfter(now));
		assertFalse(now.equals(past));
		assertTrue(now.equalsDate(past));
		assertFalse(now.equalsTime(past));
		assertFalse(now.isAfterDate(past));
		assertFalse(past.isAfterDate(now));

		assertTrue(now.isAfter(past2));
		assertFalse(past2.isAfter(now));
		assertTrue(now.isAfterDate(past2));
		assertFalse(past2.isAfterDate(now));
		assertFalse(now.equals(past2));
		assertFalse(now.equalsDate(past2));
		assertTrue(now.equalsTime(past2));
	}
}
