// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.wrapper;

import junit.framework.TestCase;

public class ContentTypeHeaderResolverTest extends TestCase {

	public void testResolver() {
		ContentTypeHeaderResolver cthr = new ContentTypeHeaderResolver("text/html; charset=utf-8");
		assertEquals("text/html", cthr.getMimeType());
		assertEquals("utf-8", cthr.getEncoding());

		cthr = new ContentTypeHeaderResolver("text/html");
		assertEquals("text/html", cthr.getMimeType());
		assertNull(cthr.getEncoding());
	}
}
