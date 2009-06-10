// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.ClassLoaderUtil;

import java.net.URL;
import java.io.File;

/**
 * Public-oriented version of {@link FindClass}, a classpath scanner.
 */
public abstract class ClasspathScanner extends FindClass {

	// ---------------------------------------------------------------- missing accessors

	public boolean isIncludeResources() {
		return includeResources;
	}

	public void setIncludeResources(boolean includeResources) {
		this.includeResources = includeResources;
	}

	public ClasspathScanner includeResources(boolean includeResources) {
		setIncludeResources(includeResources);
		return this;
	}

	// ---------------------------------------------------------------- fluent interface

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


	// ---------------------------------------------------------------- public scanners

	/**
	 * Scans provided classpath.
	 */
	public void scan(URL... urls) {
		scanUrls(urls);
	}

	/**
	 * Scans full class path.
	 */
	public void scanFullClasspath() {
		scanUrls(ClassLoaderUtil.getFullClassPath(ClasspathScanner.class));
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
