// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	RouterTest.class
})
public class MadvocTwoSuite extends MadvocSuiteBase {

	/**
	 * Starts Tomcat after the suite.
	 */
	@BeforeClass
	public static void beforeClass() {
		isSuite = true;
		startTomcat();
	}

	/**
	 * Stop Tomcat after the suite.
	 */
	@AfterClass
	public static void afterSuite() {
		isSuite = false;
		stopTomcat();
	}

	public static void startTomcat() {
		startTomcat("web-test-2-int.xml");
	}

}