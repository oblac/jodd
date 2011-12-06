// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.filter;

import jodd.util.Wildcard;

import java.io.File;

/**
 * <code>FileFilter</code> that matches file names against {@link Wildcard wildcard} patterns (*, ? and **).
 */
public class WildcardPathFilter extends FileFilterBase {

	private final String pattern;

	/**
	 * Wildcard file filter.
	 */
	public WildcardPathFilter(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(File dir, String name) {
		return Wildcard.matchPath(name, pattern);
	}

}
