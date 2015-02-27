// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.form;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FormTextTest.class,
})
public class LagartoFormSuite extends LagartoFormSuiteBase {

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
