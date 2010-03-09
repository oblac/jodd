// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncoderTest extends TestCase {

	public void testSimple() throws UnsupportedEncodingException {
		assertEquals(URLEncoder.encode("d a d a", "UTF-8"), UrlEncoder.encode("d a d a"));
		assertEquals(URLEncoder.encode("dšačdža", "UTF-8"), UrlEncoder.encode("dšačdža"));
	}
}
