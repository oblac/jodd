// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

import jodd.util.Wildcard;

/**
 * Wildcard path macro matcher.
 */
public class WildcardPathMacro extends BasePathMacro {

	@Override
	protected boolean matchValue(String value) {
		return Wildcard.matchPath(value,  pattern);
	}
}
