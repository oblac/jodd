// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;

public class CharUtilTest extends TestCase {

	public void testToSimpleByteArray() {
		char[] src = new char[] {0, 10, 'A', 127, 128, 255, 256};
		byte[] dest = CharUtil.toSimpleByteArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals(65, dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(-128, dest[4]);
		assertEquals(-1, dest[5]);
		assertEquals(0, dest[6]);
	}

	public void testToSimpleCharArray() {
		byte[] src = new byte[] {0, 10, 65, 127, -128, -1};
		char[] dest = CharUtil.toSimpleCharArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals('A', dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(128, dest[4]);
		assertEquals(255, dest[5]);
	}

	public void testToAsciiByteArray() {
		char[] src = new char[] {0, 10, 'A', 127, 128, 255, 256};
		byte[] dest = CharUtil.toAsciiByteArray(src);

		assertEquals(0, dest[0]);
		assertEquals(10, dest[1]);
		assertEquals(65, dest[2]);
		assertEquals(127, dest[3]);
		assertEquals(-128, dest[4]);
		assertEquals(-1, dest[5]);
		assertEquals(0x3F, dest[6]);
	}

	public void testToRawByteArray() {
		char[] src = new char[] {0, 'A', 255, 256, 0xFF7F};
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

	public void testToRawCharArray() {
		byte[] src = new byte[] {0,0, 0,65, 0,-1, 1,0, -1};
		char[] dest = CharUtil.toRawCharArray(src);

		assertEquals(src.length / 2 + src.length % 2, dest.length);

		assertEquals(0, dest[0]);
		assertEquals('A', dest[1]);
		assertEquals(255, dest[2]);
		assertEquals(256, dest[3]);
		assertEquals(0xFF00, dest[4]);

	}

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
		assertEquals(8 + 2, dest.length);	// BOM included
		assertEquals(269 - 256, dest[9]);
		assertEquals(1, dest[8]);
		src2 = CharUtil.toCharArray(dest, "UTF16");
		assertEquals(src[3], src2[3]);

		dest = CharUtil.toByteArray(src, "UTF8");
		assertEquals(5, dest.length);

	}

}
