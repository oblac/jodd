// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.util.CharUtil;

/**
 * Some <code>CharSequence</code> and <code>char[]</code> utils.
 */
public class TagUtil {

	// ---------------------------------------------------------------- equals

	public static boolean equals(char[] a1, char[] a2) {
		int length = a1.length;
		if (a2.length != length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean equals(CharSequence charSequence, char[] chars) {
		int length = chars.length;

		if (charSequence.length() != length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
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

	// ---------------------------------------------------------------- equals to lowercase

	public static boolean equalsToLowercase(CharSequence charSequence, char[] chars) {
		int length = chars.length;
		if (charSequence.length() != length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			char c = charSequence.charAt(i);

			c = CharUtil.toLowerAscii(c);

			if (c != chars[i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean equalsToLowercase(CharSequence charSequence, CharSequence name) {
		int len = charSequence.length();

		if (len != name.length()) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			char c = charSequence.charAt(i);

			c = CharUtil.toLowerAscii(c);

			if (c != name.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	public static boolean startsWithLowercase(CharSequence charSequence, char[] chars) {
		int length = chars.length;
		if (charSequence.length() < length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			char c = charSequence.charAt(i);

			c = CharUtil.toLowerAscii(c);

			if (c != chars[i]) {
				return false;
			}
		}

		return true;
	}

	// ---------------------------------------------------------------- equals ignore case

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

}