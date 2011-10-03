// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import junit.framework.Test;
import junit.framework.TestSuite;

public class LagartoTests extends TestSuite {

	public LagartoTests() {
		super("jodd.lagarto test suite");
		addTestSuite(LagartoParserTest.class);
		addTestSuite(TagTypeTest.class);
	}

	public static Test suite() {
		return new LagartoTests();
	}
}