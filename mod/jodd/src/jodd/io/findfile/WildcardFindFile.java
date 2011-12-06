// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.io.FileNameUtil;
import jodd.util.Wildcard;

import java.io.File;

/**
 * {@link FindFile} that matches file names using <code>*</code>, <code>?</code>
 * and, optionally, <code>**</code> wildcards.
 *
 * @see jodd.io.findfile.RegExpFindFile
 */
public class WildcardFindFile extends FindFile {

	protected String wildcard;
	protected boolean usePathWildcards;

	public WildcardFindFile() {
	}

	public WildcardFindFile(String wildcard) {
		this.wildcard = wildcard;
	}

	public WildcardFindFile(String wildcard, boolean usePathWildcards) {
		this.wildcard = wildcard;
		this.usePathWildcards = usePathWildcards;
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Returns the matching wildcard.
	 */
	public String getWildcard() {
		return wildcard;
	}

	/**
	 * Sets the matching wildcard.
	 */
	public void setWildcard(String wildcard) {
		this.wildcard = wildcard;
	}

	/**
	 * Returns <code>true</code> if path wildcards are in use.
	 */
	public boolean isUsePathWildcards() {
		return usePathWildcards;
	}

	/**
	 * Set path wildcard matching algorithm.
	 */
	public WildcardFindFile setUsePathWildcards(boolean usePathWildcards) {
		this.usePathWildcards = usePathWildcards;
		return this;
	}

	// ---------------------------------------------------------------- accept

	@Override
	protected boolean acceptFile(File currentFile) {
		String path = currentFile.getAbsolutePath();
		path = FileNameUtil.separatorsToUnix(path);

		return usePathWildcards ?
				Wildcard.matchPath(path, wildcard) :
				Wildcard.match(path, wildcard) ;
	}
}
