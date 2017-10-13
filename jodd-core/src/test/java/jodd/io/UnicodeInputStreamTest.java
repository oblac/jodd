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

import jodd.util.Bits;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UnicodeInputStreamTest {

	@Test
	public void testUtf8() throws IOException {
		byte[] bytes = new byte[4];
		Bits.putInt(bytes, 0, 0xEFBBBF65);

		ByteArrayInputStream basis = new ByteArrayInputStream(bytes);
		UnicodeInputStream uis = new UnicodeInputStream(basis, null);
		uis.init();

		assertEquals(3, uis.getBOMSize());
		assertEquals("UTF-8", uis.getDetectedEncoding());
	}

	@Test
	public void testUtf16BE() throws IOException {
		byte[] bytes = new byte[4];
		Bits.putInt(bytes, 0, 0xFEFF6565);

		ByteArrayInputStream basis = new ByteArrayInputStream(bytes);
		UnicodeInputStream uis = new UnicodeInputStream(basis, null);
		uis.init();

		assertEquals(2, uis.getBOMSize());
		assertEquals("UTF-16BE", uis.getDetectedEncoding());
	}

	@Test
	public void testUtf16LE() throws IOException {
		byte[] bytes = new byte[4];
		Bits.putInt(bytes, 0, 0xFFFE6565);

		ByteArrayInputStream basis = new ByteArrayInputStream(bytes);
		UnicodeInputStream uis = new UnicodeInputStream(basis, null);
		uis.init();

		assertEquals(2, uis.getBOMSize());
		assertEquals("UTF-16LE", uis.getDetectedEncoding());
	}

	@Test
	public void testUtf32BE() throws IOException {
		byte[] bytes = new byte[4];
		Bits.putInt(bytes, 0, 0x0000FEFF);

		ByteArrayInputStream basis = new ByteArrayInputStream(bytes);
		UnicodeInputStream uis = new UnicodeInputStream(basis, null);
		uis.init();

		assertEquals(4, uis.getBOMSize());
		assertEquals("UTF-32BE", uis.getDetectedEncoding());
	}

	@Test
	public void testUtf32LE() throws IOException {
		byte[] bytes = new byte[4];
		Bits.putInt(bytes, 0, 0xFFFE0000);

		ByteArrayInputStream basis = new ByteArrayInputStream(bytes);
		UnicodeInputStream uis = new UnicodeInputStream(basis, null);
		uis.init();

		assertEquals(4, uis.getBOMSize());
		assertEquals("UTF-32LE", uis.getDetectedEncoding());
	}

	@Test
	public void testNoUtf() throws IOException {
		byte[] bytes = new byte[4];
		Bits.putInt(bytes, 0, 0x11223344);

		ByteArrayInputStream basis = new ByteArrayInputStream(bytes);
		UnicodeInputStream uis = new UnicodeInputStream(basis, null);
		uis.init();

		assertEquals(0, uis.getBOMSize());
		assertNull(uis.getDetectedEncoding());
	}
}
