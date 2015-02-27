// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy;

import jodd.exception.UncheckedException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		JsppLiveTest.class
})
public class JoySuite {

	public static boolean isSuite;

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

	// ---------------------------------------------------------------- tomcat

	private static JoyTomcatTestServer server;

	/**
	 * Starts Tomcat.
	 */
	public static void startTomcat() {
		if (server != null) {
			return;
		}
		server = new JoyTomcatTestServer();
		try {
			server.start();
			System.out.println("Tomcat test server started");
		} catch (Exception e) {
			throw new UncheckedException(e);
		}
	}

	/**
	 * Stops Tomcat if not in the suite.
	 */
	public static void stopTomcat() {
		if (server == null) {
			return;
		}
		if (isSuite) {	// dont stop tomcat if it we are still running in the suite!
			return;
		}
		try {
			server.stop();
		} catch (Exception ignore) {
		} finally {
			System.out.println("Tomcat test server stopped");
			server = null;
		}
	}

}