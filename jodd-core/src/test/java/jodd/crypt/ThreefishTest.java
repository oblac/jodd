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

package jodd.crypt;

import jodd.util.MathUtil;
import jodd.util.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreefishTest {

	Threefish threefish;

	@BeforeEach
	void setUp() {
		threefish = new Threefish(Threefish.BLOCK_SIZE_BITS_1024);
		threefish.init("This is a key message and I feel good", 0x1122334455667788L, 0xFF00FF00AABB9933L);

	}

	@Test
	void testSimple() {
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
	void testLoop() {

		long reps = 10000;
		while (reps-- > 0) {
			String s = RandomString.get().randomAscii(MathUtil.randomInt(1, 1024));
			byte[] encrypted = threefish.encryptString(s);
			String s2 = threefish.decryptString(encrypted);
			assertEquals(s, s2);
		}

	}
}
