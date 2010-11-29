// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Simple {@link FindFile} that matches file names with regular expression pattern.
 * @see jodd.io.findfile.WildcardFindFile
 */
public class RegExpFindFile extends FindFile {

	private final Pattern regexpPattern;

	public RegExpFindFile(String pattern) {
		regexpPattern = Pattern.compile(pattern);
	}

	public RegExpFindFile(String searchPath, String pattern) {
		regexpPattern = Pattern.compile(pattern);
		searchPath(searchPath);
	}

	public RegExpFindFile(File searchPath, String pattern) {
		regexpPattern = Pattern.compile(pattern);
		searchPath(searchPath);
	}

	public RegExpFindFile(String[] searchPath, String pattern) {
		regexpPattern = Pattern.compile(pattern);
		searchPath(searchPath);
	}

	@Override
	protected boolean acceptFile(File currentFile) {
		return regexpPattern.matcher(currentFile.getName()).matches();
	}
}
