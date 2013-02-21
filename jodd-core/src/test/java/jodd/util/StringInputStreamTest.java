// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.io.FastByteArrayOutputStream;
import jodd.io.StreamUtil;
import jodd.io.StringInputStream;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StringInputStreamTest {

	@Test
	public void testAllMode() throws IOException {
		String string = "1W\uAACC";

		StringInputStream sis = new StringInputStream(string, StringInputStream.Mode.ALL);

		FastByteArrayOutputStream out = new FastByteArrayOutputStream();
		StreamUtil.copy(sis, out);
		byte[] bytes = out.toByteArray();

		assertEquals(6, bytes.length);

		assertEquals(0, bytes[0]);
		assertEquals('1', bytes[1]);
		assertEquals(0, bytes[2]);
		assertEquals('W', bytes[3]);
		assertEquals((byte)0xAA, bytes[4]);
		assertEquals((byte)0xCC, bytes[5]);
	}

	@Test
	public void testStripMode() throws IOException {
		String string = "1W\uAACC";

		StringInputStream sis = new StringInputStream(string, StringInputStream.Mode.STRIP);

		FastByteArrayOutputStream out = new FastByteArrayOutputStream();
		StreamUtil.copy(sis, out);
		byte[] bytes = out.toByteArray();

		assertEquals(3, bytes.length);

		assertEquals('1', bytes[0]);
		assertEquals('W', bytes[1]);
		assertEquals((byte)0xCC, bytes[2]);
	}

	@Test
	public void testASCIIMode() throws IOException {
		String string = "1W\uAACC";

		StringInputStream sis = new StringInputStream(string, StringInputStream.Mode.ASCII);

		FastByteArrayOutputStream out = new FastByteArrayOutputStream();
		StreamUtil.copy(sis, out);
		byte[] bytes = out.toByteArray();

		assertEquals(3, bytes.length);

		assertEquals('1', bytes[0]);
		assertEquals('W', bytes[1]);
		assertEquals((byte)0x3F, bytes[2]);
	}
}
