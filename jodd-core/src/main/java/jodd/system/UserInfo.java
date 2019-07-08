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

package jodd.system;

import jodd.util.StringUtil;

import java.io.File;

/**
 * User information.
 */
abstract class UserInfo extends RuntimeInfo {

	private final String USER_NAME = SystemUtil.get("user.name");
	private final String USER_HOME = nosep(SystemUtil.get("user.home"));
	private final String USER_DIR = nosep(SystemUtil.get("user.dir"));
	private final String USER_LANGUAGE = SystemUtil.get("user.language");
	private final String USER_COUNTRY = ((SystemUtil.get("user.country") == null) ? SystemUtil.get("user.region") : SystemUtil.get("user.country"));
	private final String JAVA_IO_TMPDIR = SystemUtil.get("java.io.tmpdir");
	private final String JAVA_HOME = nosep(SystemUtil.get("java.home"));
	private final String[] SYSTEM_CLASS_PATH = StringUtil.splitc(SystemUtil.get("java.class.path"), File.pathSeparator);

	public final String getUserName() {
		return USER_NAME;
	}

	public final String getHomeDir() {
		return USER_HOME;
	}

	public final String getWorkingDir() {
		return USER_DIR;
	}

	public final String getTempDir() {
		return JAVA_IO_TMPDIR;
	}

	public final String getUserLanguage() {
		return USER_LANGUAGE;
	}

	public final String getUserCountry() {
		return USER_COUNTRY;
	}

	public String getJavaHomeDir() {
		return JAVA_HOME;
	}

	public String[] getSystemClasspath() {
		return SYSTEM_CLASS_PATH;
	}


	@Override
	public final String toString() {
		return super.toString() +
				"\nUser name:        " + getUserName() +
				"\nUser home dir:    " + getHomeDir() +
				"\nUser current dir: " + getWorkingDir() +
				"\nUser temp dir:    " + getTempDir() +
				"\nUser language:    " + getUserLanguage() +
				"\nUser country:     " + getUserCountry();
	}


}
