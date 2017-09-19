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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BitsTest {

	@Test
	public void testBitsPutGet() {
		byte[] bytes = new byte[10];

		Bits.putBoolean(bytes, 0, true);
		assertTrue(Bits.getBoolean(bytes, 0));

		Bits.putChar(bytes, 0, 'A');
		assertEquals('A', Bits.getChar(bytes, 0));

		Bits.putShort(bytes, 0, (short) 73);
		assertEquals(73, Bits.getShort(bytes, 0));

		Bits.putInt(bytes, 0, 3373);
		assertEquals(3373, Bits.getInt(bytes, 0));

		Bits.putLong(bytes, 0, 3453454364564564L);
		assertEquals(3453454364564564L, Bits.getLong(bytes, 0));

		Bits.putFloat(bytes, 0, (float) 34.66);
		assertEquals(34.66, Bits.getFloat(bytes, 0), 0.001);

		Bits.putDouble(bytes, 0, 34.66);
		assertEquals(34.66, Bits.getDouble(bytes, 0), 0.001);
	}
}
