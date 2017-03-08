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

package jodd.util;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

/**
 * System utilities.
 */
public class SystemUtil {

	// ---------------------------------------------------------------- properties

	/**
	 * Get system property. If key is not available, returns the default value.
	 */
	public static String get(final String key, String def) {
		if (key.isEmpty()) {
			throw new IllegalArgumentException("key must not be empty.");
		}

		String value = null;
		try {
			if (System.getSecurityManager() == null) {
				value = System.getProperty(key);
			} else {
				value = AccessController.doPrivileged(new PrivilegedAction<String>() {
					@Override
					public String run() {
						return System.getProperty(key);
					}
				});
			}
		} catch (Exception ignore) {
		}

		if (value == null) {
			return def;
		}

		return value;
	}


	// ---------------------------------------------------------------- unsafe

	// IMPORTANT - order of declaration here is important! we need to detect
	// first the Android, and then to check for the unsafe field;
	// because `hasUnsafe0` method relies on the `isHostAndroid` information.

	private static final boolean IS_ANDROID = isAndroid0();
	private static final boolean HAS_UNSAFE = hasUnsafe0();

	/**
	 * Returns <code>true</code> if system has the <code>Unsafe</code>.
	 */
	public static boolean hasUnsafe() {
		return HAS_UNSAFE;
	}

	private static boolean hasUnsafe0() {
		if (isHostAndroid()) {
			return false;
		}

		try {
			return PlatformInternal.hasUnsafe();
		}
		catch (Throwable t) {
			return false;
		}
	}

	// ---------------------------------------------------------------- android

	/**
	 * Returns <code>true</code> if system is android.
	 */
	public static boolean isHostAndroid() {
		return IS_ANDROID;
	}

	private static boolean isAndroid0() {
		try {
			Class.forName("android.app.Application", false, ClassLoaderUtil.getSystemClassLoader());
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	// ---------------------------------------------------------------- properties

	public static final String USER_DIR = "user.dir";
	public static final String USER_NAME = "user.name";
	public static final String USER_HOME = "user.home";
	public static final String JAVA_HOME = "java.home";
	public static final String TEMP_DIR = "java.io.tmpdir";
	public static final String OS_NAME = "os.name";
	public static final String OS_VERSION = "os.version";
	public static final String JAVA_VERSION = "java.version";
	public static final String JAVA_SPECIFICATION_VERSION = "java.specification.version";
	public static final String JAVA_VENDOR = "java.vendor";
	public static final String JAVA_CLASSPATH = "java.class.path";
	public static final String PATH_SEPARATOR = "path.separator";
	public static final String HTTP_PROXY_HOST = "http.proxyHost";
	public static final String HTTP_PROXY_PORT = "http.proxyPort";
	public static final String HTTP_PROXY_USER = "http.proxyUser";
	public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
	public static final String FILE_ENCODING = "file.encoding";
	public static final String SUN_BOOT_CLASS_PATH = "sun.boot.class.path";

	private static int javaVersionNumber;
	private static final String WORKING_FOLDER;

	static {

		// determine the Java version by looking at available classes.

		try {
			// 1.0
			javaVersionNumber = 10;

			Class.forName("java.lang.Void");
			// 1.1
			javaVersionNumber++;

			Class.forName("java.lang.ThreadLocal");
			// 1.2
			javaVersionNumber++;

			Class.forName("java.lang.StrictMath");
			// 1.3
			javaVersionNumber++;

			Class.forName("java.lang.CharSequence");
			//1.4
			javaVersionNumber++;

			Class.forName("java.net.Proxy");
			// 1.5
			javaVersionNumber++;

			Class.forName("java.net.CookieStore");
			// 1.6
			javaVersionNumber++;

			Class.forName("java.nio.file.FileSystem");
			// 1.7
			javaVersionNumber++;

			Class.forName("java.lang.reflect.Executable");
			// 1.8
			javaVersionNumber++;
		} catch (Throwable ignore) {
		}

		// working folder

		File workingDir = new File(StringPool.EMPTY);

		WORKING_FOLDER = workingDir.getAbsolutePath();
	}

	private static String[] jrePackages;

	/**
	 * Returns list of packages, build into runtime jars.
	 */
	public static String[] jrePackages() {
		if (jrePackages == null) {
			buildJrePackages();
		}
		return jrePackages;
	}

	/**
	 * Builds a set of java core packages.
	 */
	private static void buildJrePackages() {
		ArrayList<String> packages = new ArrayList<>();

		switch (javaVersionNumber) {
			case 18:
			case 17:
			case 16:
			case 15:
				// in Java1.5, the apache stuff moved
				packages.add("com.sun.org.apache");
				// fall through...
			case 14:
				if (javaVersionNumber == 14) {
					packages.add("org.apache.crimson");
					packages.add("org.apache.xalan");
					packages.add("org.apache.xml");
					packages.add("org.apache.xpath");
				}
				packages.add("org.ietf.jgss");
				packages.add("org.w3c.dom");
				packages.add("org.xml.sax");
				// fall through...
			case 13:
				packages.add("org.omg");
				packages.add("com.sun.corba");
				packages.add("com.sun.jndi");
				packages.add("com.sun.media");
				packages.add("com.sun.naming");
				packages.add("com.sun.org.omg");
				packages.add("com.sun.rmi");
				packages.add("sunw.io");
				packages.add("sunw.util");
				// fall through...
			case 12:
				packages.add("com.sun.java");
				packages.add("com.sun.image");
				// fall through...
			case 11:
			default:
				// core stuff
				packages.add("sun");
				packages.add("java");
				packages.add("javax");
				break;
		}

		jrePackages = packages.toArray(new String[packages.size()]);
	}


	/**
	 * Returns current working folder.
	 */
	public static String userDir() {
		return System.getProperty(USER_DIR);
	}

	/**
	 * Returns current user.
	 */
	public static String userName() {
		return System.getProperty(USER_NAME);
	}

	/**
	 * Returns user home folder.
	 */
	public static String userHome() {
		return System.getProperty(USER_HOME);
	}

	/**
	 * Returns current working folder.
	 * This is <b>NOT</b> a user folder.
	 */
	public static String workingFolder() {
		return WORKING_FOLDER;
	}

	/**
	 * Returns JRE home.
	 */
	public static String javaJreHome() {
		return System.getProperty(JAVA_HOME);
	}

	/**
	 * Returns JAVA_HOME which is not equals to "java.home" property
	 * since it points to JAVA_HOME/jre folder.
	 */
	public static String javaHome() {
		String home = System.getProperty(JAVA_HOME);
		if (home == null) {
			return null;
		}
		int i = home.lastIndexOf('\\');
		int j = home.lastIndexOf('/');
		if (j > i) {
			i = j;
		}
		return home.substring(0, i);
	}

	/**
	 * Returns system temp dir.
	 */
	public static String tempDir() {
		return System.getProperty(TEMP_DIR);
	}

	/**
	 * Returns OS name.
	 */
	public static String osName() {
		return System.getProperty(OS_NAME);
	}

	/**
	 * Returns OS version.
	 */
	public static String osVersion() {
		return System.getProperty(OS_VERSION);
	}

	/**
	 * Returns Java version string, as specified in system property.
	 * Returned string contain major version, minor version and revision.
	 * @see #javaSpecificationVersion()
	 */
	public static String javaVersion() {
		return System.getProperty(JAVA_VERSION);
	}

	/**
	 * Retrieves the version of the currently running JVM.
	 */
	public static String javaSpecificationVersion() {
		return System.getProperty(JAVA_SPECIFICATION_VERSION);
	}

	/**
	 * Returns detected java version. Returned number is
	 * a number 10x the <code>major.minor</code>, e.g.
	 * Java1.5 returns <code>15</code>.
	 */
	public static int javaVersionNumber() {
		return javaVersionNumber;
	}

	/**
	 * Returns Java vendor.
	 */
	public static String javaVendor() {
		return System.getProperty(JAVA_VENDOR);
	}

	/**
	 * Checks if the currently running JVM is at least compliant
	 * with provided JDK version.
	 * @param version java version multiplied by 10, e.g. <code>1.5</code> is <code>15</code>
	 */
	public static boolean isAtLeastJavaVersion(int version) {
		return javaVersionNumber >= version;
	}

	/**
	 * Checks if the currently running JVM is equal to provided version.
	 * @param version java version, multiplied by 10, e.g. <code>1.5</code> is <code>15</code>.
	 */
	public static boolean isJavaVersion(int version) {
		return javaVersionNumber == version;
	}

	/**
	 * Returns system class path.
	 */
	public static String systemClassPath() {
		return System.getProperty(JAVA_CLASSPATH);
	}

	/**
	 * Returns path separator.
	 */
	public static String pathSeparator() {
		return System.getProperty(PATH_SEPARATOR);
	}

	/**
	 * Returns file encoding.
	 */
	public static String fileEncoding() {
		return System.getProperty(FILE_ENCODING);
	}

	/**
	 * Returns <code>true</code> if host is Windows.
	 */
	public static boolean isHostWindows() {
		return osName().toUpperCase().startsWith("WINDOWS");
	}

	/**
	 * Returns <code>true</code> if host is Linux.
	 */
	public static boolean isHostLinux() {
		return osName().toUpperCase().startsWith("LINUX");
	}

	/**
	 * Returns <code>true</code> if host is a general unix.
	 */
	public static boolean isHostUnix() {
		boolean unixVariant = isHostAix() | isHostLinux() | isHostMac() | isHostSolaris();

		return (!unixVariant && File.pathSeparator.equals(StringPool.COLON));
	}

	/**
	 * Returns <code>true</code> if host is Mac.
	 */
	public static boolean isHostMac() {
		return osName().toUpperCase().startsWith("MAC OS X");
	}

	/**
	 * Returns <code>true</code> if host is Solaris.
	 */
	public static boolean isHostSolaris() {
		return osName().toUpperCase().startsWith("SUNOS");
	}

	/**
	 * Returns <code>true</code> if host is AIX.
	 */
	public static boolean isHostAix() {
		return osName().equalsIgnoreCase("AIX");
	}

	/**
	 * Returns bootstrap class path.
	 */
	public static String getSunBootClassPath() {
		return System.getProperty(SUN_BOOT_CLASS_PATH);
	}

	// ---------------------------------------------------------------- http proxy

	/**
	 * Sets HTTP proxy settings.
	 */
	public static void setHttpProxy(String host, String port, String username, String password) {
		System.getProperties().put(HTTP_PROXY_HOST, host);
		System.getProperties().put(HTTP_PROXY_PORT, port);
		System.getProperties().put(HTTP_PROXY_USER, username);
		System.getProperties().put(HTTP_PROXY_PASSWORD, password);
	}

	/**
	 * Sets HTTP proxy settings.
	 */
	public static void setHttpProxy(String host, String port) {
		System.getProperties().put(HTTP_PROXY_HOST, host);
		System.getProperties().put(HTTP_PROXY_PORT, port);
	}

}