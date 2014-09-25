// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.form;

import jodd.exception.UncheckedException;

public abstract class LagartoFormSuiteBase {

	public static boolean isSuite;

	// ---------------------------------------------------------------- tomcat

	protected static TomcatTestServer server;

	/**
	 * Starts Tomcat.
	 */
	protected static void startTomcat(String webXmlFileName) {
		if (server != null) {
			return;
		}
		server = new TomcatTestServer(webXmlFileName);
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
		if (isSuite) {	// don't stop tomcat if it we are still running in the suite!
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
