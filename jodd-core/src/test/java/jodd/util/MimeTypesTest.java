// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

public class MimeTypesTest extends TestCase {

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
}
