// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.InterceptorManagerTest;
import junit.framework.TestSuite;
import junit.framework.Test;
import jodd.madvoc.component.ActionPathMapperTest;
import jodd.madvoc.component.ActionMethodParser2Test;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.madvoc test suite");
		addTestSuite(ActionMethodParserTest.class);
		addTestSuite(ActionMethodParser2Test.class);
		addTestSuite(ActionResultTest.class);
		addTestSuite(ActionPathMapperTest.class);
		addTestSuite(MadvocUtilTest.class);
		addTestSuite(InterceptorManagerTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}


}
