// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.Wildcard;

import java.io.File;

/**
 * Simple {@link FindFile} that matches file names using *, ? and ** wildcards.
 */
public class WildcardFindPath extends FindFile {

	protected final String wildcard;

	public WildcardFindPath(String wildcard) {
		this.wildcard = wildcard;
	}

	public WildcardFindPath(String searchPath, String wildcard) {
		this.wildcard = wildcard;
		searchPath(searchPath);
	}

	public WildcardFindPath(File searchPath, String wildcard) {
		this.wildcard = wildcard;
		searchPath(searchPath);
	}

	public WildcardFindPath(String[] searchPath, String wildcard) {
		this.wildcard = wildcard;
		searchPath(searchPath);
	}

	@Override
	protected boolean acceptFile(File currentFile) {
		return Wildcard.matchPath(currentFile.getName(), wildcard);
	}

}
