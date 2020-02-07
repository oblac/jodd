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

package jodd.joy;

import jodd.io.findfile.ClassScanner;
import jodd.typeconverter.Converter;
import jodd.util.ClassPathURLs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tiny JoyScanner kickstart.
 */
public class JoyScanner extends JoyBase implements JoyScannerConfig {

	// ---------------------------------------------------------------- config

	/**
	 * Scanning entries that will be examined by various
	 * Jodd auto-magic tools.
	 */
	private final List<String> includedEntries = new ArrayList<>();

	/**
	 * Included jars.
	 */
	private final List<String> includedJars = new ArrayList<>();

	/**
	 * Excluded jars.
	 */
	private final List<String> excludedJars = new ArrayList<>();

	/**
	 * List of APP classes.
	 */
	private final List<Class> appClasses = new ArrayList<>();

	/**
	 * Should scanning ignore the exception.
	 */
	private boolean ignoreExceptions;

	@Override
	public JoyScanner setIncludedEntries(final String... includedEntries) {
		requireNotStarted(classScanner);
		Collections.addAll(this.includedEntries, includedEntries);
		return this;
	}

	@Override
	public JoyScanner setIncludedJars(final String... includedJars) {
		requireNotStarted(classScanner);
		Collections.addAll(this.includedJars, includedJars);
		return this;
	}

	@Override
	public JoyScanner setExcludedJars(final String... excludedJars) {
		requireNotStarted(classScanner);
		Collections.addAll(this.excludedJars, excludedJars);
		return this;
	}

	@Override
	public JoyScanner setIgnoreExceptions(final boolean ignoreExceptions) {
		requireNotStarted(classScanner);
		this.ignoreExceptions = ignoreExceptions;
		return this;
	}

	/**
	 * Defines class and it's classloader to scan. This is not required in Java8
	 * and would not hurt anything if called. However, for Java9, you should
	 * pass <i>any</i> user-application class, so Jodd can figure out the real
	 * class path to scan.
	 */
	@Override
	public JoyScanner scanClasspathOf(final Class applicationClass) {
		requireNotStarted(classScanner);
		appClasses.add(applicationClass);
		return this;
	}

	/**
	 * Shortcut for {@link #scanClasspathOf(Class)}.
	 */
	@Override
	public JoyScanner scanClasspathOf(final Object applicationObject) {
		requireNotStarted(classScanner);
		return scanClasspathOf(applicationObject.getClass());
	}

	// ---------------------------------------------------------------- runtime

	/**
	 * Returns class scanner.
	 */
	public ClassScanner getClassScanner() {
		return requireStarted(classScanner);
	}

	// ---------------------------------------------------------------- lifecycle

	protected ClassScanner classScanner;

	/**
	 * Configures scanner class finder. Works for all three scanners:
	 * Petite, DbOom and Madvoc. All scanners by default include all jars,
	 * but exclude all entries.
	 */
	@Override
	public void start() {
		initLogger();

		log.info("SCANNER start ----------");

		classScanner = new ClassScanner() {
			@Override
			protected void scanJarFile(final File file) {
				log.debug("Scanning jar: " + file);
				super.scanJarFile(file);
			}

			@Override
			protected void scanClassPath(final File root) {
				log.debug("Scanning path: " + root);
				super.scanClassPath(root);
			}
		};

		if (log.isDebugEnabled()) {
			log.debug("Scan entries: " + Converter.get().toString(includedEntries));
			log.debug("Scan jars: " + Converter.get().toString(includedJars));
			log.debug("Scan exclude jars: " + Converter.get().toString(excludedJars));
			log.debug("Scan ignore exception: " + ignoreExceptions);
		}

		classScanner.excludeCommonEntries();
		classScanner.excludeCommonJars();
		classScanner.excludeJars(excludedJars.toArray(new String[0]));

		if (includedEntries.isEmpty() && includedJars.isEmpty()) {
			// nothing was explicitly included
			classScanner.excludeAllEntries(false);
		}
		else {
			// something was included by user
			classScanner.excludeAllEntries(true);
			includedEntries.add("jodd.*");
		}

		classScanner
			.detectEntriesMode(true)
			.includeEntries(includedEntries.toArray(new String[0]))
			.includeJars(includedJars.toArray(new String[0]))
			.ignoreException(ignoreExceptions)
			.scanDefaultClasspath();

		appClasses.forEach(clazz -> classScanner.scan(ClassPathURLs.of(null, clazz)));

		log.info("SCANNER OK!");
	}

	@Override
	public void stop() {
		classScanner = null;
	}

}
