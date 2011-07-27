// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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

	protected final String wildcard;
	protected boolean usePathWildcards;

	public WildcardFindFile(String wildcard) {
		this.wildcard = wildcard;
	}

	public WildcardFindFile(String wildcard, boolean usePathWildcards) {
		this.wildcard = wildcard;
		this.usePathWildcards = usePathWildcards;
	}


	public boolean isUsePathWildcards() {
		return usePathWildcards;
	}

	/**
	 * Set path wildcard matching algorithm.
	 */
	public void setUsePathWildcards(boolean usePathWildcards) {
		this.usePathWildcards = usePathWildcards;
	}

	@Override
	protected boolean acceptFile(File currentFile) {
		String path = currentFile.getAbsolutePath();
		path = FileNameUtil.separatorsToUnix(path);

		return usePathWildcards ?
				Wildcard.matchPath(path, wildcard) :
				Wildcard.match(path, wildcard) ;
	}
}
