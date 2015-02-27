// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

import java.util.regex.Pattern;

/**
 * Regular expression path macro.
 * Matches paths using regular expressions.
 */
public class RegExpPathMacros extends BasePathMacros {

	protected Pattern[] regexpPattern;

	@Override
	public boolean init(String actionPath, String[] separators) {
		boolean hasMacros = super.init(actionPath, separators);
		if (hasMacros) {
			regexpPattern = new Pattern[macrosCount];
		}
		return hasMacros;
	}

	@Override
	protected boolean matchValue(int macroIndex, String value) {
		if (regexpPattern[macroIndex] == null) {
			regexpPattern[macroIndex] = Pattern.compile(patterns[macroIndex]);
		}

		return regexpPattern[macroIndex].matcher(value).matches();
	}
}
