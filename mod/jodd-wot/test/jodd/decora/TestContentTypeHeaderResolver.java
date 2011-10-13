// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import junit.framework.TestCase;

public class TestContentTypeHeaderResolver extends TestCase {

	public void testResolver() {
		ContentTypeHeaderResolver cthr = new ContentTypeHeaderResolver("text/html; charset=utf-8");
		assertEquals("text/html", cthr.getType());
		assertEquals("utf-8", cthr.getEncoding());

		cthr = new ContentTypeHeaderResolver("text/html");
		assertEquals("text/html", cthr.getType());
		assertNull(cthr.getEncoding());
	}
}
