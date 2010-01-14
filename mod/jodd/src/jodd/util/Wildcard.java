// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Checks whether a string matches a given wildcard pattern.
 * Possible patterns allow to match single characters ('?') or any count of
 * characters ('*'). Wildcard characters can be escaped (by an '\').
 * <p>
 * This method uses recursive matching, as in linux or windows. regexp works the same.
 * This method is very fast, comparing to similar implementations.
 */
public class Wildcard {

	/**
	 * Checks whether a string matches a given wildcard pattern.
	 *
	 * @param string	input string
	 * @param pattern	pattern to match
	 * @return 			<code>true</code> if string matches the pattern, otherwise <code>false</code>
	 */
	public static boolean match(String string, String pattern) {
		return match(string, pattern, 0, 0);
	}

	/**
	 * Checks if two strings are equals or if they {@link #match(String, String)}.
	 * Useful for cases when matching a lot of equal strings and speed is important.
	 */
	public static boolean equalsOrMatch(String string, String pattern) {
		if (string.equals(pattern) == true) {
			return true;
		}
		return match(string, pattern, 0, 0);
	}


	/**
	 * Internal matching recursive function.
	 */
	private static boolean match(String string, String pattern, int stringStartNdx, int patternStartNdx) {
		int pNdx = patternStartNdx;
		int sNdx = stringStartNdx;
		int pLen = pattern.length();
		if (pLen == 1) {
			if (pattern.charAt(0) == '*') {     // speed-up
				return true;
			}
		}
		int sLen = string.length();
		boolean nextIsNotWildcard = false;

		while (true) {

			// check if end of string and/or pattern occurred
			if ((sNdx >= sLen) == true) {		// end of string still may have pending '*' in pattern
				while ((pNdx < pLen) && (pattern.charAt(pNdx) == '*')) {
					pNdx++;
				}
				return pNdx >= pLen;
			}
			if (pNdx >= pLen) {					// end of pattern, but not end of the string
				return false;
			}
			char p = pattern.charAt(pNdx);		// pattern char

			// perform logic
			if (nextIsNotWildcard == false) {

				if (p == '\\') {
					pNdx++;
					nextIsNotWildcard =  true;
					continue;
				}
				if (p == '?') {
					sNdx++; pNdx++;
					continue;
				}
				if (p == '*') {
					char pnext = 0;						// next pattern char
					if (pNdx + 1 < pLen) {
						pnext = pattern.charAt(pNdx + 1);
					}
					if (pnext == '*') {					// double '*' have the same effect as one '*'
						pNdx++;
						continue;
					}
					int i;
					pNdx++;

					// find recursively if there is any substring from the end of the
					// line that matches the rest of the pattern !!!
					for (i = string.length(); i >= sNdx; i--) {
						if (match(string, pattern, i, pNdx) == true) {
							return true;
						}
					}
					return false;
				}
			} else {
				nextIsNotWildcard = false;
			}

			// check if pattern char and string char are equals
			if (p != string.charAt(sNdx)) {
				return false;
			}

			// everything matches for now, continue
			sNdx++; pNdx++;
		}
	}


	// ---------------------------------------------------------------- utilities

	/**
	 * Matches string to at least one pattern.
	 * Returns index of matched pattern, or <code>-1</code> otherwise.
	 * @see #match(String, String)
	 */
	public static int matchOne(String src, String[] patterns) {
		for (int i = 0; i < patterns.length; i++) {
			if (match(src, patterns[i]) == true) {
				return i;
			}
		}
		return -1;
	}

}
