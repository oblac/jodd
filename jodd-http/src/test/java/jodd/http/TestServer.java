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

		File classes = new File(webInfFolder, "classes/jodd/http/fixture");
		classes.mkdirs();

		URL echoServletUrl = TestServer.class.getResource("fixture/EchoServlet.class");
		File echoServletFile = FileUtil.toFile(echoServletUrl);
		FileUtil.copyFileToDir(echoServletFile, classes);

		echoServletUrl = TestServer.class.getResource("fixture/Echo2Servlet.class");
		echoServletFile = FileUtil.toFile(echoServletUrl);
		FileUtil.copyFileToDir(echoServletFile, classes);

		echoServletUrl = TestServer.class.getResource("fixture/Echo3Servlet.class");
		echoServletFile = FileUtil.toFile(echoServletUrl);
		FileUtil.copyFileToDir(echoServletFile, classes);

		URL redirectServletUrl = TestServer.class.getResource("fixture/RedirectServlet.class");
		File redirectServletFile = FileUtil.toFile(redirectServletUrl);
		FileUtil.copyFileToDir(redirectServletFile, classes);

		URL targetServletUrl = TestServer.class.getResource("fixture/TargetServlet.class");
		File targetServletFile = FileUtil.toFile(targetServletUrl);
		FileUtil.copyFileToDir(targetServletFile, classes);
	}

	public void stop() throws Exception {
		webRoot.delete();
	}
}