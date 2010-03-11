package jodd.joy;

import jodd.io.FileNameUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.SystemUtil;

import java.net.URL;

/**
 * Commong application configuration utilities.
 */
public class AppUtil {

	public static final String APP_DIR = "app.dir";
	public static final String APP_WEB = "app.web";
	public static final String CLASSPATH_DIR = "classpath.dir";
	public static final String LOG_DIR = "log.dir";


	/**
	 * Resolves application and classpath root folders and sets system properties.
	 * Usually invoked on the very beginning, <b>before</b> application initialization.
	 * <p>
	 * If application is started as web application, root folder is one below the WEB-INF folder.
	 * Otherwise, the root folder is equal to the working folder.
	 * <p>
	 * Classpath is located by finding provided file on it.
	 * <p>
	 * Returns <code>true</code> is app core is in the web application.
	 */
	public static boolean resolveAppDirs(String classPathFileName) {
		URL url = ClassLoaderUtil.getResourceUrl(classPathFileName, AppUtil.class);
		String root = url.getFile();
		String classpath = root.substring(0, root.length() - classPathFileName.length());
		int ndx = root.indexOf("WEB-INF");
		boolean isWebApplication = (ndx != -1);
		root = isWebApplication ? root.substring(0, ndx) : SystemUtil.getWorkingFolder();
		System.setProperty(APP_DIR, root);
		System.setProperty(CLASSPATH_DIR, classpath);
		System.setProperty(APP_WEB, Boolean.toString(isWebApplication));
		return isWebApplication;
	}

	/**
	 * Prepares log root directory given as absolute path.
	 */
	public static void prepareAbsoluteLogDir(String log) {
		System.setProperty(LOG_DIR, log);
	}

	/**
	 * Prepares log root directory relative from {@link #getAppDir() application root folder}.
	 */
	public static void prepareAppLogDir(String log) {
		System.setProperty(LOG_DIR, FileNameUtil.concat(getAppDir(), log));
	}

	/**
	 * Returns application directory.
	 */
	public static String getAppDir() {
		return System.getProperty(APP_DIR);
	}

	/**
	 * Returns class path directory.
	 */
	public static String getClasspathDir() {
		return System.getProperty(CLASSPATH_DIR);
	}

	/**
	 * Returns log directory.
	 */
	public static String getLogDir() {
		return System.getProperty(LOG_DIR);
	}

	/**
	 * Returns if this application is web application.
	 */
	public static boolean isWebApplication() {
		return Boolean.parseBoolean(System.getProperty(APP_WEB));
	}

}
