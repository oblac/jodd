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

import jodd.util.ClassLoaderUtil;

abstract class OsInfo extends JvmInfo {

	private final String OS_VERSION = SystemUtil.get("os.version");
	private final String OS_ARCH = SystemUtil.get("os.arch");
	private final String OS_NAME = SystemUtil.get("os.name");

	private final boolean IS_ANDROID = isAndroid0();
	private final boolean IS_OS_AIX = matchOS("AIX");
	private final boolean IS_OS_HP_UX = matchOS("HP-UX");
	private final boolean IS_OS_IRIX = matchOS("Irix");
	private final boolean IS_OS_LINUX = matchOS("Linux") || matchOS("LINUX");
	private final boolean IS_OS_MAC = matchOS("Mac");
	private final boolean IS_OS_MAC_OSX = matchOS("Mac OS X");
	private final boolean IS_OS_OS2 = matchOS("OS/2");
	private final boolean IS_OS_SOLARIS = matchOS("Solaris");
	private final boolean IS_OS_SUN_OS = matchOS("SunOS");
	private final boolean IS_OS_WINDOWS = matchOS("Windows");
	private final boolean IS_OS_WINDOWS_2000 = matchOS("Windows", "5.0");
	private final boolean IS_OS_WINDOWS_95 = matchOS("Windows 9", "4.0");
	private final boolean IS_OS_WINDOWS_98 = matchOS("Windows 9", "4.1");
	private final boolean IS_OS_WINDOWS_ME = matchOS("Windows", "4.9");
	private final boolean IS_OS_WINDOWS_NT = matchOS("Windows NT");
	private final boolean IS_OS_WINDOWS_XP = matchOS("Windows", "5.1");

	private final String FILE_SEPARATOR = SystemUtil.get("file.separator");
	private final String LINE_SEPARATOR = SystemUtil.get("line.separator");
	private final String PATH_SEPARATOR = SystemUtil.get("path.separator");
	private final String FILE_ENCODING = SystemUtil.get("file.encoding");

	public final String getOsArchitecture() {
		return OS_ARCH;
	}

	public final String getOsName() {
		return OS_NAME;
	}

	public final String getOsVersion() {
		return OS_VERSION;
	}

	/**
	 * Returns <code>true</code> if system is android.
	 */
	public boolean isAndroid() {
		return IS_ANDROID;
	}

	private static boolean isAndroid0() {
		try {
			Class.forName("android.app.Application", false, ClassLoaderUtil.getSystemClassLoader());
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	public final boolean isAix() {
		return IS_OS_AIX;
	}

	public final boolean isHpUx() {
		return IS_OS_HP_UX;
	}

	public final boolean isIrix() {
		return IS_OS_IRIX;
	}

	public final boolean isLinux() {
		return IS_OS_LINUX;
	}

	public final boolean isMac() {
		return IS_OS_MAC;
	}

	public final boolean isMacOsX() {
		return IS_OS_MAC_OSX;
	}

	public final boolean isOs2() {
		return IS_OS_OS2;
	}

	public final boolean isSolaris() {
		return IS_OS_SOLARIS;
	}

	public final boolean isSunOS() {
		return IS_OS_SUN_OS;
	}

	public final boolean isWindows() {
		return IS_OS_WINDOWS;
	}

	public final boolean isWindows2000() {
		return IS_OS_WINDOWS_2000;
	}

	public final boolean isWindows95() {
		return IS_OS_WINDOWS_95;
	}

	public final boolean isWindows98() {
		return IS_OS_WINDOWS_98;
	}

	public final boolean isWindowsME() {
		return IS_OS_WINDOWS_ME;
	}

	public final boolean isWindowsNT() {
		return IS_OS_WINDOWS_NT;
	}

	public final boolean isWindowsXP() {
		return IS_OS_WINDOWS_XP;
	}

	// ---------------------------------------------------------------- file

	public final String getFileSeparator() {
		return FILE_SEPARATOR;
	}

	public final String getLineSeparator() {
		return LINE_SEPARATOR;
	}

	public final String getPathSeparator() {
		return PATH_SEPARATOR;
	}

	public final String getFileEncoding() {
		return FILE_ENCODING;
	}

	// ---------------------------------------------------------------- util

	private boolean matchOS(final String osNamePrefix) {
		if (OS_NAME == null) {
			return false;
		}

		return OS_NAME.startsWith(osNamePrefix);
	}

	private boolean matchOS(final String osNamePrefix, final String osVersionPrefix) {
		if ((OS_NAME == null) || (OS_VERSION == null)) {
			return false;
		}

		return OS_NAME.startsWith(osNamePrefix) && OS_VERSION.startsWith(osVersionPrefix);
	}

	@Override
	public String toString() {
		return super.toString() +
			 "\nOS architecture: " + getOsArchitecture() +
			 "\nOS name:         " + getOsName() +
			 "\nOS version:      " + getOsVersion() +
			 "\nFile separator:  " + getFileSeparator() +
			 "\nLine separator:  " + getLineSeparator() +
			 "\nPath separator:  " + getPathSeparator() +
			 "\nFile encoding:   " + getFileEncoding();
	}

}
