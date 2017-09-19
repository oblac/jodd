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

package jodd.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FastByteArrayTest {

	@Test
	public void testFbat() throws IOException {
		FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();

		fbaos.write(173);
		fbaos.write(new byte[]{1, 2, 3});
		fbaos.write(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 4, 3);

		byte[] result = fbaos.toByteArray();
		byte[] expected = new byte[]{(byte) 173, 1, 2, 3, 5, 6, 7};

		assertTrue(Arrays.equals(expected, result));
	}

	@Test
	public void testFbat2() throws IOException {
		FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream(2);

		fbaos.write(173);
		fbaos.write(new byte[]{1, 2, 3});
		fbaos.write(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 4, 3);

		byte[] result = fbaos.toByteArray();
		byte[] expected = new byte[]{(byte) 173, 1, 2, 3, 5, 6, 7};

		assertTrue(Arrays.equals(expected, result));
	}

	@Test
	public void testFbatSingle() throws IOException {
		FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream(2);

		fbaos.write(73);
		fbaos.write(74);
		fbaos.write(75);
		fbaos.write(76);
		fbaos.write(77);

		byte[] result = fbaos.toByteArray();
		byte[] expected = new byte[]{73, 74, 75, 76, 77};

		assertTrue(Arrays.equals(expected, result));
	}
}
