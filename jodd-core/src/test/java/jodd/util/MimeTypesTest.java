// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.typeconverter.Convert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

		String extensions = Convert.toString(extensionArray) + ',';

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

		String extensions = Convert.toString(extensionArray) + ',';

		assertTrue(extensions.length() > 3);

		assertTrue(extensions.contains("jpe,"));
		assertTrue(extensions.contains("jpg,"));
		assertTrue(extensions.contains("jpeg,"));
		assertTrue(extensions.contains("bmp,"));
		assertTrue(extensions.contains("png,"));
	}

}
