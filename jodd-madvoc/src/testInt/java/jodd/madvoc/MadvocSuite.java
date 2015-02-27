// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		HelloActionTest.class,
		SimpleTest.class,
		RawActionTest.class,
		UrlActionTest.class,
		OneTwoActionTest.class,
		IntcptActionTest.class,
		RestActionTest.class,
		FilterTest.class,
		SessionScopeTest.class,
		AlphaTest.class,
		ArgsTest.class,
		TypesTest.class,
		ExcTest.class,
		UserActionTest.class,
		AsyncTest.class,
		MoveTest.class,
		BookActionTest.class,
		ResultsTest.class,
		TagActionTest.class,
		MissingActionTest.class
})
public class MadvocSuite extends MadvocSuiteBase {

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
		startTomcat("web-test-int.xml");
	}

}