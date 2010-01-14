// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;

public class CharUtilTest extends TestCase {

	public void testToByte() throws UnsupportedEncodingException {
		char[] src = "tstč".toCharArray();
		assertEquals(4, src.length);
		assertEquals(269, src[3]);

		byte[] dest = CharUtil.toByteArray(src);
		assertEquals(4, dest.length);
		assertEquals(269 - 256, dest[3]);
		char[] src2 = CharUtil.toCharArray(dest);
		assertEquals(4, src2.length);
		assertTrue(src[3] != src2[3]);

		byte[] dest2 = CharUtil.toByteArray(src, "US-ASCII");
		assertEquals(4, dest2.length);
		assertEquals(0x3F, dest2[3]);

		byte[] dest3 = CharUtil.toAsciiArray(src);
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
