// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Base64Test {

	String text = "Man is distinguished, not only by his reason, but by this singular passion from other animals," +
			" which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge," +
			" exceeds the short vehemence of any carnal pleasure.";

	String enc = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz" +
			"IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg" +
			"dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu" +
			"dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo" +
			"ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";

	@Test
	public void testEncoding() {
		assertEquals(enc, Base64.encodeToString(text));
		assertEquals("TQ==", Base64.encodeToString("M"));
		assertEquals("TWE=", Base64.encodeToString("Ma"));
		assertEquals("TWFu", Base64.encodeToString("Man"));
	}

	@Test
	public void testDecode() {
		assertEquals(text, Base64.decodeToString(enc));
		assertEquals("M", Base64.decodeToString("TQ=="));
		assertEquals("Ma", Base64.decodeToString("TWE="));
		assertEquals("Man", Base64.decodeToString("TWFu"));
	}

	@Test
	public void testUTF8() {
		String utf8string = "Здоровая";

		String encoded = Base64.encodeToString(utf8string);
		String decoded = Base64.decodeToString(encoded);

		assertEquals(utf8string, decoded);

		for (int i = 0; i < 10; i++) {
			utf8string += utf8string;
		}

		assertTrue(utf8string.length() > 76);

		byte[] encodedBytes = Base64.encodeToByte(utf8string, true);
		decoded = Base64.decodeToString(encodedBytes);

		assertEquals(utf8string, decoded);


		encoded = Base64.encodeToString(utf8string, true);
		decoded = Base64.decodeToString(encoded);

		assertEquals(utf8string, decoded);
	}
}
