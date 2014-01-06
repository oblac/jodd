// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

/**
 * Jodd!
 */
public class Jodd {

	static {
		beanLoaded = checkModule("bean");
		dbLoaded = checkModule("db");
		httpLoaded = checkModule("http");
		jtxLoaded = checkModule("jtx");
		lagartoLoaded = checkModule("lagarto");
		logLoaded = checkModule("log");
		madvocLoaded = checkModule("madvoc");
		mailLoaded = checkModule("mail");
		petiteLoaded = checkModule("petite");
		propsLoaded = checkModule("props");
		proxettaLoaded = checkModule("proxetta");
		servletLoaded = checkModule("servlet");
		uploadLoaded = checkModule("upload");
		vtorLoaded = checkModule("vtor");
	}

	/**
	 * Checks if Jodd module is loaded.
	 */
	private static boolean checkModule(String moduleName) {
		ClassLoader classLoader = Jodd.class.getClassLoader();

		moduleName = moduleName.substring(0, 1).toUpperCase() +
				moduleName.substring(1, moduleName.length()).toLowerCase();

		try {
			classLoader.loadClass("jodd.Jodd" + moduleName);
			return true;
		} catch (ClassNotFoundException cnfex) {
			return false;
		}
	}

	private static final boolean beanLoaded;
	private static final boolean dbLoaded;
	private static final boolean httpLoaded;
	private static final boolean jtxLoaded;
	private static final boolean lagartoLoaded;
	private static final boolean logLoaded;
	private static final boolean madvocLoaded;
	private static final boolean mailLoaded;
	private static final boolean petiteLoaded;
	private static final boolean propsLoaded;
	private static final boolean proxettaLoaded;
	private static final boolean servletLoaded;
	private static final boolean uploadLoaded;
	private static final boolean vtorLoaded;

	// ---------------------------------------------------------------- getters

	public static boolean isBeanLoaded() {
		return beanLoaded;
	}

	public static boolean isDbLoaded() {
		return dbLoaded;
	}

	public static boolean isHttpLoaded() {
		return httpLoaded;
	}

	public static boolean isJtxLoaded() {
		return jtxLoaded;
	}

	public static boolean isLagartoLoaded() {
		return lagartoLoaded;
	}

	public static boolean isLogLoaded() {
		return logLoaded;
	}

	public static boolean isMadvocLoaded() {
		return madvocLoaded;
	}

	public static boolean isMailLoaded() {
		return mailLoaded;
	}

	public static boolean isPetiteLoaded() {
		return petiteLoaded;
	}

	public static boolean isPropsLoaded() {
		return propsLoaded;
	}

	public static boolean isProxettaLoaded() {
		return proxettaLoaded;
	}

	public static boolean isServletLoaded() {
		return servletLoaded;
	}

	public static boolean isUploadLoaded() {
		return uploadLoaded;
	}

	public static boolean isVtorLoaded() {
		return vtorLoaded;
	}
}