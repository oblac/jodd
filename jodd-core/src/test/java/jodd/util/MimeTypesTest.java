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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MimeTypesTest {

	@Test
	public void testSimpleMime() {
		assertEquals("application/atom+xml", MimeTypes.getMimeType("atom"));
		assertEquals("audio/x-wav", MimeTypes.getMimeType("wav"));
		assertEquals("image/jpeg", MimeTypes.getMimeType("jpg"));
		assertEquals("text/x-asm", MimeTypes.getMimeType("asm"));
		assertEquals("video/mp4", MimeTypes.getMimeType("mp4"));

		assertEquals("image/jpeg", MimeTypes.lookupMimeType("jpg"));
		assertEquals("application/octet-stream", MimeTypes.getMimeType("xxx"));
		assertNull(MimeTypes.lookupMimeType("xxx"));
	}

	@Test
	public void testFind() {
		String[] extensionArray = MimeTypes.findExtensionsByMimeTypes("image/jpeg", false);

		String extensions = ArraysUtil.toString(extensionArray) + ',';

		assertEquals(3, extensionArray.length);

		assertTrue(extensions.contains("jpe,"));
		assertTrue(extensions.contains("jpg,"));
		assertTrue(extensions.contains("jpeg,"));

		String[] extensionArray2 = MimeTypes.findExtensionsByMimeTypes("image/png", false);
		String[] extensionArray3 = MimeTypes.findExtensionsByMimeTypes("image/jpeg, image/png", false);

		assertEquals(extensionArray3.length, extensionArray2.length + extensionArray.length);
	}

	@Test
	public void testFindWithWildcards() {
		String[] extensionArray = MimeTypes.findExtensionsByMimeTypes("image/*", true);

		String extensions = ArraysUtil.toString(extensionArray) + ',';

		assertTrue(extensions.length() > 3);

		assertTrue(extensions.contains("jpe,"));
		assertTrue(extensions.contains("jpg,"));
		assertTrue(extensions.contains("jpeg,"));
		assertTrue(extensions.contains("bmp,"));
		assertTrue(extensions.contains("png,"));
	}

}
