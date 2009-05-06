// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

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

	public static final String SYS_USER_DIR = "user.dir";
	public static final String SYS_JAVA_HOME = "java.home";
	public static final String SYS_TEMP_DIR = "java.io.tmpdir";
	public static final String SYS_OS_NAME = "os.name";
	public static final String SYS_OS_VERSION = "os.version";
	public static final String SYS_JAVA_VERSION = "java.version";
	public static final String SYS_JAVA_VENDOR = "java.vendor";
	public static final String SYS_JAVA_CLASSPATH = "java.class.path";
	public static final String SYS_PATH_SEPARATOR = "path.separator";

	/**
	 * Returns current working folder.
	 */
	public static String getUserDir() {
		return System.getProperty(SYS_USER_DIR);
	}

	public static String getJavaJreHome() {
		return System.getProperty(SYS_JAVA_HOME);
	}

	/**
	 * Returns JAVA_HOME which is not equals to "java.home" property
	 * since it points to JAVA_HOME/jre folder.
	 */
	public static String getJavaHome() {
		String home = System.getProperty(SYS_JAVA_HOME);
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

	public static String getTempDir() {
		return System.getProperty(SYS_TEMP_DIR);
	}

	public static String getOsName() {
		return System.getProperty(SYS_OS_NAME);
	}

	public static String getOsVersion() {
		return System.getProperty(SYS_OS_VERSION);
	}

	public static String getJavaVersion() {
		return System.getProperty(SYS_JAVA_VERSION);
	}

	public static String getJavaVendor() {
		return System.getProperty(SYS_JAVA_VENDOR);
	}

	public static String getClassPath() {
		return System.getProperty(SYS_JAVA_CLASSPATH);
	}

	public static String getPathSeparator() {
		return System.getProperty(SYS_PATH_SEPARATOR);
	}



}
