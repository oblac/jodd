// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc;

import jodd.io.FileUtil;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.net.URL;

/**
 * Embedded Tomcat server for integration tests.
 */
public class TomcatTestServer {

	private final String webXml;

	public TomcatTestServer(String webXml) {
		this.webXml = webXml;
	}

	// ---------------------------------------------------------------- instance

	protected File webRoot;
	protected Tomcat tomcat;

	public void start() throws Exception {
		prepareWebApplication();

		String workingDir = System.getProperty("java.io.tmpdir");

		tomcat = new Tomcat();
		tomcat.setPort(8173);
		tomcat.setBaseDir(workingDir);
		tomcat.addWebapp("", webRoot.getAbsolutePath());

		tomcat.start();
	}

	protected void prepareWebApplication() throws Exception {
		webRoot = FileUtil.createTempDirectory("jodd-madvoc", "test-int");
		webRoot.deleteOnExit();

		// web-inf

		File webInfFolder = new File(webRoot, "WEB-INF");
		webInfFolder.mkdir();

		// web.xml

		URL webXmlUrl = TomcatTestServer.class.getResource(webXml);
		File webXmlFile = FileUtil.toFile(webXmlUrl);
		if (webXmlFile == null) {
			throw new Exception("Test resource files can not be found.");
		}
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

		// classes/madvoc.props

		URL madvocPropsUrl = TomcatTestServer.class.getResource("madvoc.props");
		File madvocPropsFile = FileUtil.toFile(madvocPropsUrl);

		FileUtil.copyFileToDir(madvocPropsFile, classes);
	}

	public void stop() throws Exception {
		tomcat.stop();
		tomcat.destroy();
		FileUtil.deleteDir(webRoot);
	}
}
