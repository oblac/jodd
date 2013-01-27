// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

import java.util.regex.Pattern;

/**
 * Regular expression path macro.
 * Matches paths using regular expressions.
 */
public class RegExpPathMacro extends BasePathMacro {

	protected Pattern regexpPattern;

	@Override
	protected boolean matchValue(String value) {
		if (regexpPattern == null) {
			this.regexpPattern = Pattern.compile(pattern);
		}

		return regexpPattern.matcher(value).matches();
	}
}
