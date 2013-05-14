package jodd.http;

import jodd.io.FileUtil;

import java.io.File;
import java.net.URL;


/**
 * Common server content.
 */
public abstract class TestServer {

	protected File webRoot;

	public void start() throws Exception {
		webRoot = FileUtil.createTempDirectory("jodd-http", "test");
		webRoot.deleteOnExit();

		// web-inf

		File webInfFolder = new File(webRoot, "WEB-INF");
		webInfFolder.mkdir();

		// web.xml

		URL webXmlUrl = TestServer.class.getResource("web.xml");
		File webXmlFile = FileUtil.toFile(webXmlUrl);

		FileUtil.copy(webXmlFile, webInfFolder);

		// lib folder

		File libFolder = new File(webInfFolder, "lib");
		libFolder.mkdir();

		// classes

		File classes = new File(webInfFolder, "classes/jodd/http");
		classes.mkdirs();

		URL echoServletUrl = TestServer.class.getResource("EchoServlet.class");
		File echoServletFile = FileUtil.toFile(echoServletUrl);
		FileUtil.copyFileToDir(echoServletFile, classes);

		echoServletUrl = TestServer.class.getResource("Echo2Servlet.class");
		echoServletFile = FileUtil.toFile(echoServletUrl);
		FileUtil.copyFileToDir(echoServletFile, classes);

	}

	public void stop() throws Exception {
		webRoot.delete();
	}
}