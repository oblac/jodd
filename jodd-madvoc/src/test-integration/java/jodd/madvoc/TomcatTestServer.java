// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.exception.UncheckedException;
import jodd.io.FileUtil;
import jodd.io.FileUtilParams;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.net.URL;

/**
 * Embedded Tomcat server for integration tests.
 */
public class TomcatTestServer {

	private static int counter;
	private static TomcatTestServer server;

	/**
	 * Starts Tomcat.
	 */
	public static void startTomcat() {
		if (counter == 0) {
			server = new TomcatTestServer();
			try {
				server.start();
				System.out.println("Tomcat test server started");
			} catch (Exception e) {
				throw new UncheckedException(e);
			}
		}
		counter++;
	}

	/**
	 * Stops Tomcat.
	 */
	public static void stopTomcat() {
		counter--;
		if (counter < 0) {
			throw new UncheckedException("Negative counter");
		}
		if (counter == 0) {
			try {
				server.stop();
			} catch (Exception ignore) {
			} finally {
				System.out.println("Tomcat test server stopped");
			}
			server = null;
		}
	}

	// ---------------------------------------------------------------- instance

	protected File webRoot;
	protected Tomcat tomcat;

	public void start() throws Exception {
		prepareWebApplication();

		String workingDir = System.getProperty("java.io.tmpdir");

		tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.setBaseDir(workingDir);
		tomcat.addWebapp("/", webRoot.getAbsolutePath());

		tomcat.start();
	}

	protected void prepareWebApplication() throws Exception {
		webRoot = FileUtil.createTempDirectory("jodd-madvoc", "test-int");
		webRoot.deleteOnExit();

		System.out.println(webRoot);

		// web-inf

		File webInfFolder = new File(webRoot, "WEB-INF");
		webInfFolder.mkdir();

		// web.xml

		URL webXmlUrl = TestServer.class.getResource("web-test-int.xml");
		File webXmlFile = FileUtil.toFile(webXmlUrl);

		FileUtil.copyFile(webXmlFile, new File(webInfFolder, "web.xml"));

		// jsp
		File jspFolder = new File(webXmlFile.getParent(), "jsp");
		FileUtil.copyDir(jspFolder, webRoot);

		// lib folder

		File libFolder = new File(webInfFolder, "lib");
		libFolder.mkdir();

		// classes

		File classes = new File(webInfFolder, "classes");
		classes.mkdirs();
	}

	public void stop() throws Exception {
		tomcat.stop();
		tomcat.destroy();
		webRoot.delete();
	}
}
