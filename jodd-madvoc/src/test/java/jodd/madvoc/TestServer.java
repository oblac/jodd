// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.io.FileUtil;

import java.io.File;
import java.net.URL;


/**
 * Common server content.
 */
public abstract class TestServer {

	protected File webRoot;

	public void start() throws Exception {
		webRoot = FileUtil.createTempDirectory("jodd-madvoc", "test");
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

		File classes = new File(webInfFolder, "classes");
		classes.mkdirs();
	}

	public void stop() throws Exception {
		webRoot.delete();
	}
}