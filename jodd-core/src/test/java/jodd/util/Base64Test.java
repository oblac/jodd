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
