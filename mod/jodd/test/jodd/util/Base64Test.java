// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class Base64Test extends TestCase {

	String text = "Man is distinguished, not only by his reason, but by this singular passion from other animals," +
			" which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge," +
			" exceeds the short vehemence of any carnal pleasure.";

	String enc = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz" +
			"IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg" +
			"dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu" +
			"dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo" +
			"ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";

	public void testEncoding() {
		assertEquals(enc, Base64.encode(text));
	}

	public void testDecode() {
		assertEquals(text, new String(Base64.decode(enc)));
	}
}
