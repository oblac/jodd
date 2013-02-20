// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.Wildcard;

import java.io.File;

/**
 * {@link FindFile} that matches file names using <code>*</code>, <code>?</code>
 * and <code>**</code> wildcards.
 *
 * @see jodd.io.findfile.RegExpFindFile
 */
public class WildcardFindFile extends FindFile<WildcardFindFile> {

	protected String searchPattern;

	public WildcardFindFile() {
	}

	public WildcardFindFile(String searchPattern) {
		this.searchPattern = searchPattern;
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Returns the matching wildcard search pattern.
	 */
	public String getSearchPattern() {
		return searchPattern;
	}

	/**
	 * Sets the matching wildcard search pattern.
	 */
	public WildcardFindFile setSearchPattern(String searchPattern) {
		this.searchPattern = searchPattern;
		return this;
	}

	// ---------------------------------------------------------------- accept

	@Override
	protected boolean acceptFile(File file) {
		String path = getMatchingFilePath(file);

		return Wildcard.matchPath(path, searchPattern);
	}
}