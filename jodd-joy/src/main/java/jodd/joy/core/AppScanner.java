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
	 * Petite, DbOom and Madvoc. All scanners by default include all jars,
	 * but exclude all entries.
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