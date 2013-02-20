// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Simple {@link FindFile} that matches file names with regular expression pattern.
 * @see jodd.io.findfile.WildcardFindFile
 */
public class RegExpFindFile extends FindFile<RegExpFindFile> {

	private Pattern searchPattern;

	public RegExpFindFile() {
	}

	public RegExpFindFile(String pattern) {
		searchPattern = Pattern.compile(pattern);
	}

	/**
	 * Returns regular expression search pattern.
	 */
	public Pattern getSearchPattern() {
		return searchPattern;
	}

	/**
	 * Sets regular expression search pattern.
	 */
	public RegExpFindFile setSearchPattern(Pattern searchPattern) {
		this.searchPattern = searchPattern;
		return this;
	}

	@Override
	protected boolean acceptFile(File file) {
		String path = getMatchingFilePath(file);

		return searchPattern.matcher(path).matches();
	}
}
