// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.ClassLoaderUtil;

import java.net.URL;
import java.io.File;

/**
 * Developer-friendly version of {@link FindClass}, a classpath scanner.
 */
public abstract class ClasspathScanner extends FindClass {

	// ---------------------------------------------------------------- fluent interface

	public ClasspathScanner includeResources(boolean includeResources) {
		setIncludeResources(includeResources);
		return this;
	}

	public ClasspathScanner ignoreException(boolean ignoreException) {
		setIgnoreException(ignoreException);
		return this;
	}

	public ClasspathScanner systemJars(String... jars) {
		setSystemJars(jars);
		return this;
	}

	public ClasspathScanner includeJars(String... jars) {
		setIncludedJars(jars);
		return this;
	}

	public ClasspathScanner excludeJars(String... jars) {
		setExcludedJars(jars);
		return this;
	}

	public ClasspathScanner include(String... entries) {
		setIncludedEntries(entries);
		return this;
	}

	public ClasspathScanner exclude(String... entries) {
		setExcludedEntries(entries);
		return this;
	}

	public ClasspathScanner usePathWildcards(boolean value) {
		setUsePathWildcards(value);
		return this;
	}

	// ---------------------------------------------------------------- public scanners

	/**
	 * Scans provided classpath.
	 */
	public void scan(URL... urls) {
		scanUrls(urls);
	}

	/**
	 * Scans default class path.
	 */
	public void scanDefaultClasspath() {
		scan(ClassLoaderUtil.getDefaultClasspath());
	}

	/**
	 * Scans provided paths.
	 */
	public void scan(File... paths) {
		scanPaths(paths);
	}

	/**
	 * Scans provided paths.
	 */
	public void scan(String... paths) {
		scanPaths(paths);
	}

}
