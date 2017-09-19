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

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharUtilTest {

	@Test
	public void testToSimpleByteArray() {
		char[] src = new char[]{0, 10, 'A', 127, 128, 255, 256};
		byte[] dest = CharUtil.toSimpleByteArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals(65, dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(-128, dest[4]);
		assertEquals(-1, dest[5]);
		assertEquals(0, dest[6]);
	}

	@Test
	public void testToSimpleCharArray() {
		byte[] src = new byte[]{0, 10, 65, 127, -128, -1};
		char[] dest = CharUtil.toSimpleCharArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals('A', dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(128, dest[4]);
		assertEquals(255, dest[5]);
	}

	@Test
	public void testToAsciiByteArray() {
		char[] src = new char[]{0, 10, 'A', 127, 128, 255, 256};
		byte[] dest = CharUtil.toAsciiByteArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals(65, dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(-128, dest[4]);
		assertEquals(-1, dest[5]);
		assertEquals(0x3F, dest[6]);
	}

	@Test
	public void testToRawByteArray() {
		char[] src = new char[]{0, 'A', 255, 256, 0xFF7F};
		byte[] dest = CharUtil.toRawByteArray(src);

		assertEquals(src.length * 2, dest.length);

		assertEquals(0, dest[0]);
		assertEquals(0, dest[1]);

		assertEquals(0, dest[2]);
		assertEquals(65, dest[3]);

		assertEquals(0, dest[4]);
		assertEquals(-1, dest[5]);

		assertEquals(1, dest[6]);
		assertEquals(0, dest[7]);

		assertEquals(-1, dest[8]);
		assertEquals(127, dest[9]);
	}

	@Test
	public void testToRawCharArray() {
		byte[] src = new byte[]{0, 0, 0, 65, 0, -1, 1, 0, -1};
		char[] dest = CharUtil.toRawCharArray(src);

		assertEquals(src.length / 2 + src.length % 2, dest.length);

		assertEquals(0, dest[0]);
		assertEquals('A', dest[1]);
		assertEquals(255, dest[2]);
		assertEquals(256, dest[3]);
		assertEquals(0xFF00, dest[4]);

	}

	@Test
	public void testToByte() throws UnsupportedEncodingException {
		char[] src = "tstč".toCharArray();
		assertEquals(4, src.length);
		assertEquals(269, src[3]);

		byte[] dest = CharUtil.toSimpleByteArray(src);
		assertEquals(4, dest.length);
		assertEquals(269 - 256, dest[3]);
		char[] src2 = CharUtil.toSimpleCharArray(dest);
		assertEquals(4, src2.length);
		assertTrue(src[3] != src2[3]);

		byte[] dest2 = CharUtil.toByteArray(src, "US-ASCII");
		assertEquals(4, dest2.length);
		assertEquals(0x3F, dest2[3]);

		byte[] dest3 = CharUtil.toAsciiByteArray(src);
		assertEquals(4, dest3.length);
		assertEquals(0x3F, dest3[3]);

		dest = CharUtil.toByteArray(src, "UTF16");
		assertEquals(8 + 2, dest.length);    // BOM included
		assertEquals(269 - 256, dest[9]);
		assertEquals(1, dest[8]);
		src2 = CharUtil.toCharArray(dest, "UTF16");
		assertEquals(src[3], src2[3]);

		dest = CharUtil.toByteArray(src, "UTF8");
		assertEquals(5, dest.length);
	}

	@Test
	public void testHexToInt() {
		assertEquals(0, CharUtil.hex2int('0'));
		assertEquals(1, CharUtil.hex2int('1'));
		assertEquals(2, CharUtil.hex2int('2'));
		assertEquals(3, CharUtil.hex2int('3'));
		assertEquals(4, CharUtil.hex2int('4'));
		assertEquals(5, CharUtil.hex2int('5'));
		assertEquals(6, CharUtil.hex2int('6'));
		assertEquals(7, CharUtil.hex2int('7'));
		assertEquals(8, CharUtil.hex2int('8'));
		assertEquals(9, CharUtil.hex2int('9'));
		assertEquals(10, CharUtil.hex2int('A'));
		assertEquals(10, CharUtil.hex2int('a'));
		assertEquals(11, CharUtil.hex2int('B'));
		assertEquals(11, CharUtil.hex2int('b'));
		assertEquals(12, CharUtil.hex2int('C'));
		assertEquals(12, CharUtil.hex2int('c'));
		assertEquals(13, CharUtil.hex2int('D'));
		assertEquals(13, CharUtil.hex2int('d'));
		assertEquals(14, CharUtil.hex2int('E'));
		assertEquals(14, CharUtil.hex2int('e'));
		assertEquals(15, CharUtil.hex2int('F'));
		assertEquals(15, CharUtil.hex2int('f'));
	}

}
