// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.Wildcard;

import java.io.File;

/**
 * Simple {@link FindFile} that matches file names using wildcards.
 * @see jodd.io.findfile.RegExpFindFile
 */
public class WildcardFindFile extends FindFile {

	protected String wildcard;

	public WildcardFindFile(String wildcard) {
		this.wildcard = wildcard;

	}

	public WildcardFindFile(String searchPath, String wildcard) {
		this.wildcard = wildcard;
		searchPath(searchPath);
	}

	public WildcardFindFile(File searchPath, String wildcard) {
		this.wildcard = wildcard;
		searchPath(searchPath);
	}


	public WildcardFindFile(String[] searchPath, String wildcard) {
		this.wildcard = wildcard;
		searchPath(searchPath);
	}

	@Override
	protected boolean acceptFile(File currentFile) {
		return Wildcard.match(currentFile.getName(), wildcard);
	}
}
