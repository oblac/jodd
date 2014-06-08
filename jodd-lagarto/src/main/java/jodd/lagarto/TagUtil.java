// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.CharUtil;

import static jodd.lagarto.LagartoParser.RAWTEXT_TAGS;

public class TagUtil {

	public static boolean isRawTagName(CharSequence name) {
		for (char[] RAWTEXT_TAG : RAWTEXT_TAGS) {
			if (equalsToLowercase(name, RAWTEXT_TAG)) {
				return true;
			}
		}
		return false;
	}

	// todo to string util
	public static boolean equals(CharSequence charSequence, char[] chars) {
		if (charSequence.length() != chars.length) {
			return false;
		}

		for (int i = 0; i < chars.length; i++) {
			if (charSequence.charAt(i) != chars[i]) {
				return false;
			}
		}

		return true;
	}

	// todo to string util
	public static boolean equalsToLowercase(CharSequence charSequence, char[] chars) {
		if (charSequence.length() != chars.length) {
			return false;
		}

		for (int i = 0; i < chars.length; i++) {
			char c = charSequence.charAt(i);

			c = CharUtil.toLowerAscii(c);

			if (c != chars[i]) {
				return false;
			}
		}

		return true;
	}

}