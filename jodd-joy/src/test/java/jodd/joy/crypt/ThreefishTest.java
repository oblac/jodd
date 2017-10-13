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

package jodd.joy.crypt;

import jodd.util.MathUtil;
import jodd.util.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreefishTest {

	Threefish threefish;

	@BeforeEach
	public void setUp() throws Exception {
		threefish = new Threefish(Threefish.BLOCK_SIZE_BITS_1024);
		threefish.init("This is a key message and I feel good", 0x1122334455667788L, 0xFF00FF00AABB9933L);

	}

	private void assertEqualsArray(byte[] expected, byte[] value) {
		assertEquals(expected.length, value.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], value[i]);
		}
	}

	@Test
	public void testSimple() throws UnsupportedEncodingException {
		String message = "Threefish!";
		byte[] encrypted = threefish.encryptString(message);
		String message2 = threefish.decryptString(encrypted);
		assertEquals(message, message2);

		message = "Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!Jodd was here!";
		encrypted = threefish.encryptString(message);
		message2 = threefish.decryptString(encrypted);

		assertEquals(message, message2);
	}

	@Test
	public void testLoop() throws UnsupportedEncodingException {

		long reps = 10000;
		while (reps-- > 0) {
			String s = RandomString.getInstance().randomAscii(MathUtil.randomInt(1, 1024));
			byte[] encrypted = threefish.encryptString(s);
			String s2 = threefish.decryptString(encrypted);
			assertEquals(s, s2);
		}

	}
}
