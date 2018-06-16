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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

public class SystemUtil {

	/**
	 * Returns system property or {@code null} if not set.
	 */
	public static String get(final String name) {
		return get(name, null);
	}

	/**
	 * Returns system property. If key is not available, returns the default value.
	 */
	public static String get(final String name, final String defaultValue) {
		Objects.requireNonNull(name);

		String value = null;
		try {
			if (System.getSecurityManager() == null) {
				value = System.getProperty(name);
			} else {
				value = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty(name));
			}
		} catch (Exception ignore) {
		}

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	/**
	 * Returns system property as boolean.
	 */
	public static boolean getBoolean(final String name, final boolean defaultValue) {
		String value = get(name);
		if (value == null) {
			return defaultValue;
		}

		value = value.trim().toLowerCase();

		switch (value) {
			case "true" :
			case "yes"  :
			case "1"    :
			case "on"   :
				return true;
			case "false":
			case "no"   :
			case "0"    :
			case "off"  :
				return false;
			default:
				return defaultValue;
		}
	}

	/**
	 * Returns system property as an int.
	 */
	public static long getInt(final String name, final int defaultValue) {
		String value = get(name);
		if (value == null) {
			return defaultValue;
		}

		value = value.trim().toLowerCase();
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException nfex) {
			return defaultValue;
		}
	}

	/**
	 * Returns system property as a long.
	 */
	public static long getLong(final String name, final long defaultValue) {
		String value = get(name);
		if (value == null) {
			return defaultValue;
		}

		value = value.trim().toLowerCase();
		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException nfex) {
			return defaultValue;
		}
	}

	// ---------------------------------------------------------------- infos

	private static final SystemInfo systemInfo = new SystemInfo();

	/**
	 * Returns system information.
	 */
	public static SystemInfo info() {
		return systemInfo;
	}

	/**
	 * Dump all information to the console.
	 */
	public static void printoutInfo() {
		System.out.println(systemInfo.toString());
	}

	public static void main(String[] args) {
		printoutInfo();
	}
}
