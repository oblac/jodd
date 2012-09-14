// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.ClassLoaderUtil;

import java.net.URL;
import java.io.File;

/**
 * A class scanner, user-friendly version of {@link ClassFinder} a class scanner.
 * Since <code>ClassFinder</code> is an abstract class, it can not be used directly,
 * <code>ClassScanner</code> can.
 */
public abstract class ClassScanner extends ClassFinder {

	/**
	 * Scans provided classpath.
	 */
	public void scan(URL... urls) {
		scanUrls(urls);
	}

	/**
	 * Scans {@link jodd.util.ClassLoaderUtil#getDefaultClasspath() default class path}.
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
