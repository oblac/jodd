// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.wrapper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ContentTypeHeaderResolverTest {

	@Test
	public void testResolver() {
		ContentTypeHeaderResolver cthr = new ContentTypeHeaderResolver("text/html; charset=utf-8");
		assertEquals("text/html", cthr.getMimeType());
		assertEquals("utf-8", cthr.getEncoding());

		cthr = new ContentTypeHeaderResolver("text/html");
		assertEquals("text/html", cthr.getMimeType());
		assertNull(cthr.getEncoding());
	}
}
