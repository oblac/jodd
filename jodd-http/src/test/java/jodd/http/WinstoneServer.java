// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileUtil;
import jodd.util.ThreadUtil;
import winstone.Launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WinstoneServer {

	protected Launcher winstone;
	protected File webRoot;

	public void start() throws IOException {
		webRoot = FileUtil.createTempDirectory("jodd", "test");
		webRoot.deleteOnExit();

		// web-inf

		File webInfFolder = new File(webRoot, "WEB-INF");
		webInfFolder.mkdir();

		// web.xml

		URL webXmlUrl = WinstoneServer.class.getResource("web.xml");
		File webXmlFile = FileUtil.toFile(webXmlUrl);

		FileUtil.copy(webXmlFile, webInfFolder);

		// lib folder

		File libFolder = new File(webInfFolder, "lib");
		libFolder.mkdir();

		// classes

		File classes = new File(webInfFolder, "classes/jodd/http");
		classes.mkdirs();

		URL echoServletUrl = WinstoneServer.class.getResource("EchoServlet.class");
		File echoServletFile = FileUtil.toFile(echoServletUrl);
		FileUtil.copyFileToDir(echoServletFile, classes);

		echoServletUrl = WinstoneServer.class.getResource("Echo2Servlet.class");
		echoServletFile = FileUtil.toFile(echoServletUrl);
		FileUtil.copyFileToDir(echoServletFile, classes);

		// start

		Map<String, String> args = new HashMap<String, String>();
		args.put("webroot", webRoot.getAbsolutePath());
		Launcher.initLogger(args);
		winstone = new Launcher(args);

		ThreadUtil.sleep(300);
	}

	public void stop() {
		winstone.shutdown();
		while (winstone.isRunning()) {
			ThreadUtil.sleep(100);
		}
		webRoot.delete();
	}
}