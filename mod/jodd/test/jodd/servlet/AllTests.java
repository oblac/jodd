// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;
import jodd.servlet.tag.FormTagTest;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.servlet test suite");
		addTestSuite(HtmlEncoderTest.class);
		addTestSuite(HtmlTagTest.class);
		addTestSuite(ServletUtilTest.class);
		addTestSuite(FormTagTest.class);
		addTestSuite(UrlEncoderTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}