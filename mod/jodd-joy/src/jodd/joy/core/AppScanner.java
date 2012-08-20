// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

import jodd.io.findfile.ClassFinder;
import jodd.log.Log;
import jodd.props.Props;
import jodd.typeconverter.Convert;

/**
 * <code>AppScanner</code> defines entries that will be included/excluded in
 * scanning process, when configuring Jodd frameworks.
 * By default, scanning entries includes all classes that belongs
 * to the project and to the Jodd.
 */
public class AppScanner {

	private static final Log log = Log.getLogger(AppScanner.class);

	private static final String PROPS_PREFIX = "app-scan";

	protected final DefaultAppCore appCore;

	public AppScanner(DefaultAppCore appCore) {
		this.appCore = appCore;
	}

	/**
	 * Scanning entries that will be examined by various
	 * Jodd auto-magic tools.
	 */
	protected String[] includedEntries;

	/**
	 * Scanning jars.
	 */
	protected String[] includedJars;

	/**
	 * Should scanning ignore the exception.
	 */
	protected boolean ignoreExceptions;


	public void init() {
		Props appProps = appCore.getAppProps();

		// scan included entries
		String value = appProps.getValue(PROPS_PREFIX + ".includedEntries");

		if (value == null) {
			includedEntries = new String[] {
					appCore.getClass().getPackage().getName() + ".*",
					"jodd.*"
			};
		} else {
			includedEntries = Convert.toStringArray(value);
		}

		if (log.isDebugEnabled()) {
			log.debug("Scan entries: " + Convert.toString(includedEntries));
		}

		// scan included jars
		value = appProps.getValue(PROPS_PREFIX + ".includedJars");

		if (value == null) {
			includedJars = null;
		} else {
			includedJars = Convert.toStringArray(value);
		}

		if (log.isDebugEnabled()) {
			log.debug("Scan jars: " + Convert.toString(includedJars));
		}


		// scan ignore exceptions
		value = appProps.getValue(PROPS_PREFIX + ".ignoreExceptions");

		if (value == null) {
			ignoreExceptions = false;
		} else {
			ignoreExceptions = Convert.toBooleanValue(value);
		}

		if (log.isDebugEnabled()) {
			log.debug("Scan ignore exception: " + ignoreExceptions);
		}
	}


	/**
	 * Configures scanner class finder. Works for all three scanners:
	 * Petite, DbOom and Madvoc.
	 */
	public void configure(ClassFinder classFinder) {
		if (includedEntries != null) {
			classFinder.setIncludedEntries(includedEntries);
		}

		if (includedJars != null) {
			classFinder.setIncludedJars(includedJars);
		}

		classFinder.setIgnoreException(ignoreExceptions);
	}


}
