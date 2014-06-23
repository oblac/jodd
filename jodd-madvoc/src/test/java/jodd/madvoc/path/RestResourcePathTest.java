// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.path;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestResourcePathTest {

	@Test
	public void testResolve() {
		RestResourcePath restResourcePath = new RestResourcePath();

		assertEquals("GET", restResourcePath.resolveHttpMethodFromMethodName("get"));
		assertEquals("GET", restResourcePath.resolveHttpMethodFromMethodName("getUser"));
		assertEquals("POST", restResourcePath.resolveHttpMethodFromMethodName("post"));
		assertEquals(null, restResourcePath.resolveHttpMethodFromMethodName("pOst"));
	}

}