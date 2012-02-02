// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy;

import jodd.io.FileNameUtil;
import jodd.joy.exception.AppException;
import jodd.util.ClassLoaderUtil;
import jodd.util.SystemUtil;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Common application configuration utilities.
 */
public class AppUtil {

	public static final String APP_DIR = "app.dir";
	public static final String APP_WEB = "app.web";
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
		if (url == null) {
			throw new AppException("Unable to resolve app dirs, missing: '" + classPathFileName + "'.");
		}
		String protocol = url.getProtocol();

		if (protocol.equals("file") == false) {
			try {
				url = new URL(url.getFile());
			} catch (MalformedURLException ignore) {
			}
		}

		String root = url.getFile();

		int ndx = root.indexOf("WEB-INF");
		boolean isWebApplication = (ndx != -1);
		root = isWebApplication ? root.substring(0, ndx) : SystemUtil.getWorkingFolder();
		System.setProperty(APP_DIR, root);
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
