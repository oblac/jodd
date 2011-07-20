// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.io.File;

/**
 * Various system utilities.
 */
public class SystemUtil {

	/**
	 * Calculates and returns java system timer resolution in miliseconds.
	 * Resolution of a timer depends on platform and java version.
	 */
	public static double systemTimerResolution() {
		long t1, t2;
		int sumres = 0;
		//noinspection CallToSystemGC
		System.gc();
		int loops = 20;
		for (int i = 0; i < loops; ++i) {
			t1 = System.currentTimeMillis();
			while (true) {
				t2 = System.currentTimeMillis();
				if (t2 != t1) {
					sumres += (int) (t2 - t1);
					break;
				}
			}
		}
		return (sumres / (double) loops);
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

	/**
	 * Returns current working folder.
	 */
	public static String getUserDir() {
		return System.getProperty(USER_DIR);
	}

	/**
	 * Returns current user.
	 */
	public static String getUserName() {
		return System.getProperty(USER_NAME);
	}

	/**
	 * Returns user home folder.
	 */
	public static String getUserHome() {
		return System.getProperty(USER_HOME);
	}

	/**
	 * Returns current working folder.
	 * Just a better name for {@link #getUserDir()}.
	 */
	public static String getWorkingFolder() {
		return System.getProperty(USER_DIR);
	}

	/**
	 * Returns JRE home.
	 */
	public static String getJavaJreHome() {
		return System.getProperty(JAVA_HOME);
	}

	/**
	 * Returns JAVA_HOME which is not equals to "java.home" property
	 * since it points to JAVA_HOME/jre folder.
	 */
	public static String getJavaHome() {
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
	public static String getTempDir() {
		return System.getProperty(TEMP_DIR);
	}

	/**
	 * Returns OS name.
	 */
	public static String getOsName() {
		return System.getProperty(OS_NAME);
	}

	/**
	 * Returns OS version.
	 */
	public static String getOsVersion() {
		return System.getProperty(OS_VERSION);
	}

	/**
	 * Returns Java version.
	 * @see #getJavaSpecificationVersion()
	 */
	public static String getJavaVersion() {
		return System.getProperty(JAVA_VERSION);
	}

	/**
	 * Retrieves the version of the currently running JVM.
	 */
	public static double getJavaSpecificationVersion() {
		return Double.parseDouble(System.getProperty(JAVA_SPECIFICATION_VERSION));
	}

	/**
	 * Returns Java vendor.
	 */
	public static String getJavaVendor() {
		return System.getProperty(JAVA_VENDOR);
	}

	/**
	 * Returns system class path.
	 */
	public static String getClassPath() {
		return System.getProperty(JAVA_CLASSPATH);
	}

	/**
	 * Returns path separator.
	 */
	public static String getPathSeparator() {
		return System.getProperty(PATH_SEPARATOR);
	}

	/**
	 * Returns file encoding.
	 */
	public static String getFileEncoding() {
		return System.getProperty(FILE_ENCODING);
	}

	/**
	 * Checks if the currently running JVM is at least compliant with JDK 1.5.
	 */
	public static boolean isAtLeastJdk15() {
		return getJavaSpecificationVersion() >= 1.5;
	}

	/**
	 * Checks if the currently running JVM is at least compliant with JDK 1.6.
	 */
	public static boolean isAtLeastJdk16() {
		return getJavaSpecificationVersion() >= 1.6;
	}


	/**
	 * Returns <code>true</code> if host is Windows.
	 */
	public static boolean isHostWindows() {
		return getOsName().toUpperCase().startsWith("WINDOWS");
	}

	/**
	 * Returns <code>true</code> if host is Linux.
	 */
	public static boolean isHostLinux() {
		return getOsName().toUpperCase().startsWith("LINUX");
	}

	/**
	 * Returns <code>true</code> if host is a general unix.
	 */
	public static boolean isHostUnix() {
		return File.pathSeparator.equals(StringPool.COLON);
	}

	/**
	 * Returns <code>true</code> if host is Mac.
	 */
	public static boolean isHostMac() {
		return getOsName().toUpperCase().startsWith("MAC OS X");
	}

	/**
	 * Returns <code>true</code> if host is Solaris.
	 */
	public static boolean isHostSolaris() {
		return getOsName().toUpperCase().startsWith("SUNOS");
	}

	/**
	 * Returns <code>true<</code> if host is AIX.
	 */
	public static boolean isHostAix() {
		return getOsName().toUpperCase().equals("AIX");
	}

	/**
	 * Returns sun bootstrap class path.
	 */
	public static String getSunBoothClassPath() {
		return System.getProperty(SUN_BOOT_CLASS_PATH);
	}

	// ---------------------------------------------------------------- set

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
