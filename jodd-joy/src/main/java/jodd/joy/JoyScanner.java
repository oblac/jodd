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

import jodd.core.JavaBridge;
import jodd.io.findfile.ClassScanner;
import jodd.typeconverter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * <code>AppScanner</code> defines entries that will be included/excluded in
 * scanning process, when configuring Jodd frameworks.
 * By default, scanning entries includes all classes that belongs
 * to the project and to the Jodd.
 */
public class JoyScanner extends JoyBase implements Consumer<ClassScanner> {

	/**
	 * Scanning entries that will be examined by various
	 * Jodd auto-magic tools.
	 */
	private List<String> includedEntries = new ArrayList<>();

	/**
	 * Included jars.
	 */
	private List<String> includedJars = new ArrayList<>();

	/**
	 * List of APP classes.
	 */
	private List<Class> appClasses = new ArrayList<>();

	/**
	 * Should scanning ignore the exception.
	 */
	private boolean ignoreExceptions;

	public JoyScanner setIncludedEntries(final String... includedEntries) {
		Collections.addAll(this.includedEntries, includedEntries);
		return this;
	}

	public JoyScanner setIncludedJars(final String... includedJars) {
		Collections.addAll(this.includedJars, includedJars);
		return this;
	}

	public JoyScanner setIgnoreExceptions(final boolean ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
		return this;
	}

	/**
	 * Defines class and it's classloader to scan. This is not required in Java8
	 * and would not hurt anything if called. However, for Java9, you should
	 * pass <i>any</i> user-application class, so Jodd can figure out the real
	 * class path to scan.
	 */
	public JoyScanner scanClasspathOf(Class applicationClass) {
		appClasses.add(applicationClass);
		return this;
	}

	/**
	 * Shortcut for {@link #scanClasspathOf(Class)}.
	 */
	public JoyScanner scanClasspathOf(Object applicationObject) {
		return scanClasspathOf(applicationObject.getClass());
	}


	// ---------------------------------------------------------------- start

	@Override
	public void start() {
		initLogger();

		log.info("SCANNER start ----------");

		if (log.isDebugEnabled()) {
			log.debug("Scan entries: " + Converter.get().toString(includedEntries));
			log.debug("Scan jars: " + Converter.get().toString(includedJars));
			log.debug("Scan ignore exception: " + ignoreExceptions);
		}
	}

	/**
	 * Configures scanner class finder. Works for all three scanners:
	 * Petite, DbOom and Madvoc. All scanners by default include all jars,
	 * but exclude all entries.
	 */
	@Override
	public void accept(final ClassScanner classScanner) {
		if (includedEntries.isEmpty() && includedJars.isEmpty()) {
			classScanner.excludeAllEntries(false);
			classScanner.excludeEntries("ch.qos.logback.*");
		}
		else {
			classScanner.excludeAllEntries(true);
			includedEntries.add("jodd.*");
		}

		classScanner
			.includeEntries(includedEntries.toArray(new String[0]))
			.includeJars(includedJars.toArray(new String[0]))
			.ignoreException(ignoreExceptions)
			.scanDefaultClasspath();

		appClasses.forEach(clazz -> classScanner.scan(JavaBridge.getURLs(clazz)));
	}

	@Override
	public void stop() {
		includedEntries.clear();
		includedJars.clear();
	}
}