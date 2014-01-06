// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

	@Override
	protected boolean match(String path, String pattern) {
		return Wildcard.matchPath(path, pattern);
	}

}