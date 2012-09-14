// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Simple {@link FindFile} that matches file names with regular expression pattern.
 * @see jodd.io.findfile.WildcardFindFile
 */
public class RegExpFindFile extends FindFile {

	private Pattern regexpPattern;

	public RegExpFindFile() {
	}

	public RegExpFindFile(String pattern) {
		regexpPattern = Pattern.compile(pattern);
	}

	/**
	 * Returns regular expression pattern.
	 */
	public Pattern getRegexpPattern() {
		return regexpPattern;
	}

	/**
	 * Sets regular expression pattern.
	 */
	public void setRegexpPattern(Pattern regexpPattern) {
		this.regexpPattern = regexpPattern;
	}

	@Override
	protected boolean acceptFile(File file) {
		String path = getMatchingFilePath(file);

		return regexpPattern.matcher(path).matches();
	}
}
