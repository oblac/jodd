// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.ClassLoaderUtil;

import java.net.URL;
import java.io.IOException;

/**
 * Public-oriented version of {@link FindClass}, a classpath scanner.
 */
public abstract class ClasspathScanner extends FindClass {

	// ---------------------------------------------------------------- missing accessors

	public boolean isCreateInputStream() {
		return createInputStream;
	}

	public void setCreateInputStream(boolean createInputStream) {
		this.createInputStream = createInputStream;
	}

	public boolean isIgnoreExceptions() {
		return ignoreExceptions;
	}

	public void setIgnoreExceptions(boolean ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
	}

	public boolean isIncludeResources() {
		return includeResources;
	}

	public void setIncludeResources(boolean includeResources) {
		this.includeResources = includeResources;
	}

	// ---------------------------------------------------------------- public scanners

	/**
	 * Scans provided classpath.
	 */
	public void scan(URL... urls) throws IOException {
		scanUrls(urls);
	}

	/**
	 * Scans full class path.
	 */
	public void scan() throws IOException {
		scanUrls(ClassLoaderUtil.getFullClassPath(ClasspathScanner.class));
	}

}
