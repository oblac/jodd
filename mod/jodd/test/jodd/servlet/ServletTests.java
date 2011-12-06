// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;
import jodd.servlet.tag.FormTagTest;

public class ServletTests extends TestSuite {

	public ServletTests() {
		super("jodd.servlet test suite");
		addTestSuite(HtmlEncoderTest.class);
		addTestSuite(HtmlTagTest.class);
		addTestSuite(ServletUtilTest.class);
		addTestSuite(FormTagTest.class);
		addTestSuite(URLCoderTest.class);
		addTestSuite(HtmlDecoderTest.class);
	}

	public static Test suite() {
		return new ServletTests();
	}

}