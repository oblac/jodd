// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

import jodd.io.findfile.ClassFinder;
import jodd.typeconverter.Convert;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

/**
 * <code>AppScanner</code> defines entries that will be included/excluded in
 * scanning process, when configuring Jodd frameworks.
 * By default, scanning entries includes all classes that belongs
 * to the project and to the Jodd.
 */
public class AppScanner {

	private static final Logger log = LoggerFactory.getLogger(AppScanner.class);

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

	public String[] getIncludedEntries() {
		return includedEntries;
	}

	public void setIncludedEntries(String... includedEntries) {
		this.includedEntries = includedEntries;
	}

	public String[] getIncludedJars() {
		return includedJars;
	}

	public void setIncludedJars(String... includedJars) {
		this.includedJars = includedJars;
	}

	public boolean isIgnoreExceptions() {
		return ignoreExceptions;
	}

	public void setIgnoreExceptions(boolean ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
	}

	// ---------------------------------------------------------------- props


	/**
	 * Configures scanner class finder. Works for all three scanners:
	 * Petite, DbOom and Madvoc.
	 */
	public void configure(ClassFinder classFinder) {

		classFinder.setExcludeAllEntries(true);

		if (includedEntries == null) {
			includedEntries = new String[] {
					appCore.getClass().getPackage().getName() + ".*",
					"jodd.*"
			};
		}

		if (log.isDebugEnabled()) {
			log.debug("Scan entries: " + Convert.toString(includedEntries));
			log.debug("Scan jars: " + Convert.toString(includedJars));
			log.debug("Scan ignore exception: " + ignoreExceptions);
		}

		if (includedEntries != null) {
			classFinder.setIncludedEntries(includedEntries);
		}

		if (includedJars != null) {
			classFinder.setIncludedJars(includedJars);
		}

		classFinder.setIgnoreException(ignoreExceptions);
	}

}