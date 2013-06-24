// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Simple {@link FindFile} that matches file names with regular expression pattern.
 * @see jodd.io.findfile.WildcardFindFile
 */
public class RegExpFindFile extends FindFile<RegExpFindFile> {

	private HashMap<String, Pattern> searchPatterns;

	@Override
	protected boolean match(String path, String patternString) {
		if (searchPatterns == null) {
			searchPatterns = new HashMap<String, Pattern>();
		}

		Pattern pattern = searchPatterns.get(patternString);

		if (pattern == null) {
			pattern = Pattern.compile(patternString);
			searchPatterns.put(patternString, pattern);
		}

		return pattern.matcher(path).matches();
	}
}
