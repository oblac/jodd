// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.filter;

import java.io.File;

import jodd.util.Wildcard;

/**
 * <code>FileFilter</code> that matches file names against {@link Wildcard wildcard} pattern (*, ? and **).
 */
public class WildcardFileFilter extends FileFilterBase {

	private final String pattern;
	protected boolean usePathWildcards;

	/**
	 * Wildcard file filter.
	 */
	public WildcardFileFilter(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Wildcard file filter.
	 */
	public WildcardFileFilter(String pattern, boolean usePathWildcards) {
		this.pattern = pattern;
		this.usePathWildcards = usePathWildcards;
	}

	@Override
	public boolean accept(File dir, String name) {

		return usePathWildcards ?
				Wildcard.matchPath(name, pattern) :
				Wildcard.match(name, pattern) ;
	}
}

