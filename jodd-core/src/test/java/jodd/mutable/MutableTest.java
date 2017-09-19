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

package jodd.mutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MutableTest {

	@Test
	public void testMutableInteger() {
		MutableInteger m = new MutableInteger();

		assertTrue(m instanceof Number);
		m.set(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertTrue(m.equals(Integer.valueOf(27)));
		assertEquals(0, m.compareTo((MutableInteger) m2));
	}

	@Test
	public void testMutableLong() {
		MutableLong m = new MutableLong();

		assertTrue(m instanceof Number);
		m.set(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertTrue(m.equals(Long.valueOf(27)));
		assertEquals(0, m.compareTo((MutableLong) m2));
	}

	@Test
	public void testMutableShort() {
		MutableShort m = new MutableShort();

		assertTrue(m instanceof Number);
		m.set(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertTrue(m.equals(Short.valueOf((short) 27)));
		assertEquals(0, m.compareTo((MutableShort) m2));
	}

	@Test
	public void testMutableByte() {
		MutableByte m = new MutableByte();

		assertTrue(m instanceof Number);
		m.set(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertTrue(m.equals(Byte.valueOf((byte) 27)));
		assertEquals(0, m.compareTo((MutableByte) m2));
	}

	@Test
	public void testMutableFloat() {
		MutableFloat m = new MutableFloat();

		assertTrue(m instanceof Number);
		m.set(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableFloat) m2));
	}

	@Test
	public void testMutableDouble() {
		MutableDouble m = new MutableDouble();

		assertTrue(m instanceof Number);
		m.set(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableDouble) m2));
	}

	@Test
	public void testMutableBoolean() {
		MutableBoolean m = new MutableBoolean();

		m.set(true);
		assertEquals(true, m.get());

		Object m2 = m.clone();

		assertEquals(m2, m);
		assertTrue(m.equals(Boolean.TRUE));
		assertEquals(0, m.compareTo((MutableBoolean) m2));
	}

}
