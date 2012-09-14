// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.filter;

import java.io.File;

import jodd.util.Wildcard;

/**
 * <code>FileFilter</code> that matches file names against
 * {@link Wildcard#matchPath(String, String) wildcard} pattern (*, ? and **).
 */
public class WildcardFileFilter extends FileFilterBase {

	private final String pattern;

	/**
	 * Wildcard file filter.
	 */
	public WildcardFileFilter(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(File dir, String name) {
		return Wildcard.matchPath(name, pattern);
	}
}
