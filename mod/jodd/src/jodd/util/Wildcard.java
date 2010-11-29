// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Checks whether a string or path matches a given wildcard pattern.
 * Possible patterns allow to match single characters ('?') or any count of
 * characters ('*'). Wildcard characters can be escaped (by an '\').
 * When matching path, deep tree wildcard also can be used ('**').
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

	/**
	 * Matches path to at least one pattern.
	 * Returns index of matched pattern or <code>-1</code> otherwise.
	 * @see #matchPath(String, String) 
	 */
	public static int matchPathOne(String path, String[] patterns) {
		for (int i = 0; i < patterns.length; i++) {
			if (matchPath(path, patterns[i]) == true) {
				return i;
			}
		}
		return -1;
	}

	// ---------------------------------------------------------------- path

	protected static final String PATH_MATCH = "**";
	protected static final String PATH_SEPARATORS = "/\\";

	/**
	 * Matches path against pattern using *, ? and ** wildcards.
	 * Both path and the pattern are tokenized on path separators (both \ and /).
	 * '**' represents deep tree wildcard, as in Ant.
	 */
	public static boolean matchPath(String path, String pattern) {
		String[] pathElements = StringUtil.splitc(path, PATH_SEPARATORS);
		String[] patternElements = StringUtil.splitc(pattern, PATH_SEPARATORS);
		return matchTokens(pathElements, patternElements);
	}

	/**
	 * Match tokenized string and pattern.
	 */
	protected static boolean matchTokens(String[] tokens, String[] patterns) {
		int patNdxStart = 0;
		int patNdxEnd = patterns.length - 1;
		int tokNdxStart = 0;
		int tokNdxEnd = tokens.length - 1;

		while ((patNdxStart <= patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {	// find first **
			String patDir = patterns[patNdxStart];
			if (patDir.equals(PATH_MATCH)) {
				break;
			}
			if (!match(tokens[tokNdxStart], patDir)) {
				return false;
			}
			patNdxStart++;
			tokNdxStart++;
		}
		if (tokNdxStart > tokNdxEnd) {
			for (int i = patNdxStart; i <= patNdxEnd; i++) {	// string is finished
				if (!patterns[i].equals(PATH_MATCH)) {
					return false;
				}
			}
			return true;
		}
		if (patNdxStart > patNdxEnd) {
			return false;	// string is not finished, but pattern is
		}

		while ((patNdxStart <= patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {	// to the last **
			String patDir = patterns[patNdxEnd];
			if (patDir.equals(PATH_MATCH)) {
				break;
			}
			if (!match(tokens[tokNdxEnd], patDir)) {
				return false;
			}
			patNdxEnd--;
			tokNdxEnd--;
		}
		if (tokNdxStart > tokNdxEnd) {
			for (int i = patNdxStart; i <= patNdxEnd; i++) {	// string is finished
				if (!patterns[i].equals(PATH_MATCH)) {
					return false;
				}
			}
			return true;
		}

		while ((patNdxStart != patNdxEnd) && (tokNdxStart <= tokNdxEnd)) {
			int patIdxTmp = -1;
			for (int i = patNdxStart + 1; i <= patNdxEnd; i++) {
				if (patterns[i].equals(PATH_MATCH)) {
					patIdxTmp = i;
					break;
				}
			}
			if (patIdxTmp == patNdxStart + 1) {
				patNdxStart++;	// skip **/** situation
				continue;
			}
			// find the pattern between padIdxStart & padIdxTmp in str between strIdxStart & strIdxEnd
			int patLength = (patIdxTmp - patNdxStart - 1);
			int strLength = (tokNdxEnd - tokNdxStart + 1);
			int ndx = -1;
			strLoop:
			for (int i = 0; i <= strLength - patLength; i++) {
				for (int j = 0; j < patLength; j++) {
					String subPat = patterns[patNdxStart + j + 1];
					String subStr = tokens[tokNdxStart + i + j];
					if (!match(subStr, subPat)) {
						continue strLoop;
					}
				}

				ndx = tokNdxStart + i;
				break;
			}

			if (ndx == -1) {
				return false;
			}

			patNdxStart = patIdxTmp;
			tokNdxStart = ndx + patLength;
		}

		for (int i = patNdxStart; i <= patNdxEnd; i++) {
			if (!patterns[i].equals(PATH_MATCH)) {
				return false;
			}
		}

		return true;
	}
}
