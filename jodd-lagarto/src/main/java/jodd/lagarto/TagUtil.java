// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.CharUtil;

// todo add to stringutil?
public class TagUtil {

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

	public static boolean equals(CharSequence charSequence1, CharSequence charSequence2) {
		int len = charSequence1.length();

		if (len != charSequence2.length()) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			if (charSequence1.charAt(i) != charSequence2.charAt(i)) {
				return false;
			}
		}

		return true;
	}

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

	public static boolean equalsIgnoreCase(CharSequence charSequence1, CharSequence charSequence2) {
		int len = charSequence1.length();

		if (len != charSequence2.length()) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			char c1 = charSequence1.charAt(i);
			c1 = CharUtil.toLowerAscii(c1);

			char c2 = charSequence2.charAt(i);
			c2 = CharUtil.toLowerAscii(c2);

			if (c1 != c2) {
				return false;
			}
		}
		return true;
	}

	public static boolean equalsIgnoreCase(CharSequence charSequence1, char[] chars2) {
		int len = charSequence1.length();

		if (len != chars2.length) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			char c1 = charSequence1.charAt(i);
			c1 = CharUtil.toLowerAscii(c1);

			char c2 = chars2[i];
			c2 = CharUtil.toLowerAscii(c2);

			if (c1 != c2) {
				return false;
			}
		}

		return true;
	}


}