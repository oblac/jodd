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

package jodd.joy;

import jodd.system.SystemUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringUtil;

import java.net.MalformedURLException;
import java.net.URL;

import static jodd.joy.JoddJoy.APP_DIR;

public class JoyPaths extends JoyBase {

	protected String appDir;

	// ---------------------------------------------------------------- runtime

	/**
	 * Returns resolved app dir.
	 */
	public String getAppDir() {
		return requireStarted(appDir);
	}

	// ---------------------------------------------------------------- lifecycle

	@Override
	public void start() {
		initLogger();

		final String resourceName = StringUtil.replaceChar(JoyPaths.class.getName(), '.', '/') + ".class";

		URL url = ClassLoaderUtil.getResourceUrl(resourceName);

		if (url == null) {
			throw new JoyException("Failed to resolve app dir, missing: " + resourceName);
		}
		final String protocol = url.getProtocol();

		if (!protocol.equals("file")) {
			try {
				url = new URL(url.getFile());
			} catch (MalformedURLException ignore) {
			}
		}

		appDir = url.getFile();

		final int ndx = appDir.indexOf("WEB-INF");

		appDir = (ndx > 0) ? appDir.substring(0, ndx) : SystemUtil.info().getWorkingDir();

		System.setProperty(APP_DIR, appDir);

		log.info("Application folder: " + appDir);
	}

	@Override
	public void stop() {
	}
}