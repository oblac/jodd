// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.filter;

import java.io.File;

import jodd.util.Wildcard;

/**
 * FileFilter that matches file names against wildcard pattern (* and ?).
 */
public class WildcardFileFilter extends FileFilterBase {

	private String pattern;

	/**
	 * Wildcard file filter.
	 */
	public WildcardFileFilter(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean accept(File dir, String name) {
		return Wildcard.match(name, pattern);
	}
}

