// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

import jodd.util.Wildcard;

/**
 * Wildcard path macro matcher.
 */
public class WildcardPathMacros extends BasePathMacros {

	@Override
	protected boolean matchValue(int macroIndex, String value) {
		return Wildcard.matchPath(value, patterns[macroIndex]);
	}
}
