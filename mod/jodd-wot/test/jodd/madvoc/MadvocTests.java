// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.InterceptorManagerTest;
import junit.framework.TestSuite;
import junit.framework.Test;
import jodd.madvoc.component.ActionPathMapperTest;

public class MadvocTests extends TestSuite {

	public MadvocTests() {
		super("jodd.madvoc test suite");
		addTestSuite(ActionMethodParserTest.class);
		addTestSuite(ActionResultTest.class);
		addTestSuite(ActionPathMapperTest.class);
		addTestSuite(MadvocUtilTest.class);
		addTestSuite(InterceptorManagerTest.class);
	}

	public static Test suite() {
		return new MadvocTests();
	}


}
