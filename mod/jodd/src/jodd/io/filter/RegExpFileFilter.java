// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.filter;

import java.io.File;
import java.util.regex.Pattern;

/**
 * FileFilter that matches files with use of Regular Expression.
 *
 * Some tips for regular expressions:
 * <ul>
 *   <li>.* : matches any number of character</li>
 *   <li>.? : matches zero or one character</li>
 * </ul>
 */
public class RegExpFileFilter extends FileFilterBase {

	private Pattern regexpPattern;

	public RegExpFileFilter(String pattern) {
		regexpPattern = Pattern.compile(pattern);
	}

	@Override
	public boolean accept(File dir, String name) {
		return regexpPattern.matcher(name).matches();
	}
}

