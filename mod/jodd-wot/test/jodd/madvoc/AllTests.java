// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import junit.framework.TestSuite;
import junit.framework.Test;
import jodd.madvoc.component.ActionPathMapperTest;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.madvoc test suite");
		addTestSuite(ActionMethodParserTest.class);
		addTestSuite(ActionResultTest.class);
		addTestSuite(ActionPathMapperTest.class);
		addTestSuite(MadvocUtilTest.class);
		addTestSuite(ActionPathMapperTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}


}
