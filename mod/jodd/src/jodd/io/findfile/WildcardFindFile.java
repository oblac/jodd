// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.io.FileNameUtil;
import jodd.util.Wildcard;

import java.io.File;

/**
 * {@link FindFile} that matches file names using <code>*</code>, <code>?</code>
 * and <code>**</code> wildcards.
 *
 * @see jodd.io.findfile.RegExpFindFile
 */
public class WildcardFindFile extends FindFile {

	protected String pattern;

	public WildcardFindFile() {
	}

	public WildcardFindFile(String pattern) {
		this.pattern = pattern;
	}

	// ---------------------------------------------------------------- properties

	/**
	 * Returns the matching wildcard pattern.
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the matching wildcard pattern.
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	// ---------------------------------------------------------------- accept

	@Override
	protected boolean acceptFile(File file) {
		String path = file.getAbsolutePath();
		path = FileNameUtil.separatorsToUnix(path);

		return Wildcard.matchPath(path, pattern);
	}
}
