// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.typeconverter.Convert;

import static jodd.util.StringPool.EMPTY;

import java.io.UnsupportedEncodingException;


/**
 * Various String utilities.
 * @see jodd.util.TextUtil
 */
public class StringUtil {

	// ---------------------------------------------------------------- replace

	/**
	 * Replaces all occurrences of a certain pattern in a string with a
	 * replacement string. This is the fastest replace function known to author.
	 *
	 * @param s      string to be inspected
	 * @param sub    string pattern to be replaced
	 * @param with   string that should go where the pattern was
	 */
	public static String replace(String s, String sub, String with) {
		int c = 0;
		int i = s.indexOf(sub, c);
		if (i == -1) {
			return s;
		}
		int sLen = s.length();
		StringBuilder buf = new StringBuilder(sLen + with.length());
		do {
			 buf.append(s.substring(c,i));
			 buf.append(with);
			 c = i + sub.length();
		 } while ((i = s.indexOf(sub, c)) != -1);
		 if (c < sLen) {
			 buf.append(s.substring(c, sLen));
		 }
		 return buf.toString();
	}

	/**
	 * Replaces all occurrences of a character in a string.
	 *
	 * @param s      input string
	 * @param sub    character to replace
	 * @param with   character to replace with
	 */
	public static String replaceChar(String s, char sub, char with) {
		char[] str = s.toCharArray();
		for (int i = 0; i < str.length; i++) {
			if (str[i] == sub) {
				str[i] = with;
			}
		}
		return new String(str);
	}

	/**
	 * Replaces all occurrences of a characters in a string.
	 *
	 * @param s      input string
	 * @param sub    characters to replace
	 * @param with   characters to replace with
	 */
	public static String replaceChars(String s, char[] sub, char[] with) {
		char[] str = s.toCharArray();
		for (int i = 0; i < str.length; i++) {
			char c = str[i];
			for (int j = 0; j < sub.length; j++) {
			    if (c == sub[j]) {
					str[i] = with[j];
					break;
				}
			}
		}
		return new String(str);
	}

	/**
	 * Replaces the very first occurrence of a substring with supplied string.
	 *
	 * @param s      source string
	 * @param sub    substring to replace
	 * @param with   substring to replace with
	 */
	public static String replaceFirst(String s, String sub, String with) {
		int i = s.indexOf(sub);
		if (i == -1) {
			return s;
		}
		return s.substring(0, i) + with + s.substring(i + sub.length());
	}

	/**
	 * Replaces the very first occurrence of a character in a string.
	 *
	 * @param s      string
	 * @param sub    char to replace
	 * @param with   char to replace with
	 */
	public static String replaceFirst(String s, char sub, char with) {
		char[] str = s.toCharArray();
		for (int i = 0; i < str.length; i++) {
			if (str[i] == sub) {
				str[i] = with;
				break;
			}
		}
		return new String(str);
	}

	/**
	 * Replaces the very last occurrence of a substring with supplied string.
	 *
	 * @param s      source string
	 * @param sub    substring to replace
	 * @param with   substring to replace with
	 */
	public static String replaceLast(String s, String sub, String with) {
		int i = s.lastIndexOf(sub);
		if (i == -1) {
			return s;
		}
		return s.substring(0, i) + with + s.substring(i + sub.length());
	}

	/**
	 * Replaces the very last occurrence of a character in a string.
	 *
	 * @param s      string
	 * @param sub    char to replace
	 * @param with   char to replace with
	 */
	public static String replaceLast(String s, char sub, char with) {
		char[] str = s.toCharArray();
		for (int i = str.length - 1; i >= 0; i--) {
			if (str[i] == sub) {
				str[i] = with;
				break;
			}
		}
		return new String(str);
	}

	// ---------------------------------------------------------------- remove

	/**
	 * Removes all substring occurrences from the string.
	 *
	 * @param s      source string
	 * @param sub    substring to remove
	 */
	public static String remove(String s, String sub) {
		int c = 0;
		int sublen = sub.length();
		if (sublen == 0) {
			return s;
		}
		int i = s.indexOf(sub, c);
		if (i == -1) {
			return s;
		}
		StringBuilder buf = new StringBuilder(s.length());
		do {
			 buf.append(s.substring(c, i));
			 c = i + sublen;
		 } while ((i = s.indexOf(sub, c)) != -1);
		 if (c < s.length()) {
			 buf.append(s.substring(c, s.length()));
		 }
		 return buf.toString();
	}

	/**
	 * Removes all characters contained in provided string.
	 *
	 * @param src    source string
	 * @param chars  string containing characters to remove
	 */
	public static String removeChars(String src, String chars) {
		int i = src.length();
		StringBuilder stringbuffer = new StringBuilder(i);
		for (int j = 0; j < i; j++) {
			char c = src.charAt(j);
			if (chars.indexOf(c) == -1) {
				stringbuffer.append(c);
			}
		}
		return stringbuffer.toString();
	}


	/**
	 * Removes set of characters from string.
	 *
	 * @param src    string
	 * @param chars  characters to remove
	 */
	public static String removeChars(String src, char... chars) {
		int i = src.length();
		StringBuilder stringbuffer = new StringBuilder(i);
		mainloop:
		for (int j = 0; j < i; j++) {
			char c = src.charAt(j);
			for (char aChar : chars) {
				if (c == aChar) {
					continue mainloop;
				}
			}
			stringbuffer.append(c);
		}
		return stringbuffer.toString();
	}

	/**
	 * Removes a single character from string.
	 *
	 * @param src    source string
	 * @param chars  character to remove
	 */
	public static String remove(String src, char chars) {
		int i = src.length();
		StringBuilder stringbuffer = new StringBuilder(i);
		for (int j = 0; j < i; j++) {
			char c = src.charAt(j);
			if (c == chars) {
				continue;
			}
			stringbuffer.append(c);
		}
		return stringbuffer.toString();
	}

	// ---------------------------------------------------------------- miscellaneous

	/**
	 * Compares 2 strings. If one of the strings is <code>null</code>, <code>false</code> is returned. if
	 * both string are <code>null</code>, <code>true</code> is returned.
	 *
	 * @param s1     first string to compare
	 * @param s2     second string
	 *
	 * @return <code>true</code> if strings are equal, otherwise <code>false</code>
	 */
	public static boolean equals(String s1, String s2) {
		return ObjectUtil.equals(s1, s2);
	}

	/**
	 * Determines if a string is empty (<code>null</code> or zero-length).
	 */
	public static boolean isEmpty(String string) {
		return ((string == null) || (string.length() == 0));
	}

	/**
	 * Determines if string array contains empty strings.
	 * @see #isEmpty(String) 
	 */
	public static boolean isAllEmpty(String... strings) {
		for (String string : strings) {
			if (isEmpty(string) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines if a string is blank (<code>null</code> or {@link #containsOnlyWhitespaces(String)}).
	 */
	public static boolean isBlank(String string) {
		return ((string == null) || containsOnlyWhitespaces(string));
	}

	/**
	 * Determines if string is not blank.
	 */
	public static boolean isNotBlank(String string) {
		return ((string != null) && !containsOnlyWhitespaces(string));
	}

	/**
	 * Determines if string array contains just blank strings.
	 */
	public static boolean isAllBlank(String... strings) {
		for (String string : strings) {
			if (isBlank(string) == false) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Returns <code>true</code> if string contains only white spaces.
	 */
	public static boolean containsOnlyWhitespaces(String string) {
		int size = string.length();
		for (int i= 0; i < size; i++) {
			char c = string.charAt(i);
			if (CharUtil.isWhitespace(c) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines if a string is not empty.
	 */
	public static boolean isNotEmpty(String string) {
		return string != null && string.length() > 0;
	}


	/**
	 * Converts safely an object to a string. If object is <code>null</code> it will be
	 * not converted.
	 */
	public static String toString(Object obj) {
		return Convert.toString(obj, null);
	}

	/**
	 * Converts safely an object to a string. If object is <code>null</code> an empty
	 * string is returned.
	 */
	public static String toSafeString(Object obj) {
		return Convert.toString(obj, EMPTY);
	}

	/**
	 * Converts an object to a String Array.
	 */
	public static String[] toStringArray(Object obj) {
		return Convert.toStringArray(obj);
	}

	// ---------------------------------------------------------------- captialize

	/**
	 * Capitalizes a string, changing the first letter to
	 * upper case. No other letters are changed.
	 *
	 * @param str   string to capitalize, may be null
	 * @see #uncapitalize(String)
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(true, str);
	}

	/**
	 * Uncapitalizes a <code>String</code>, changing the first letter to
	 * lower case. No other letters are changed.
	 *
	 * @param str the String to uncapitalize, may be null
	 * @return the uncapitalized String, <code>null</code> if null
	 * @see #capitalize(String) 
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(false, str);
	}

	/**
	 * Internal method for changing the first character case. It is significantly
	 * faster using StringBuffers then just simply Strings.
	 */
	private static String changeFirstCharacterCase(boolean capitalize, String str) {
		int strLen = str.length();
		if (strLen == 0) {
			return str;
		}
		StringBuilder buf = new StringBuilder(strLen);
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		} else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}


	// ---------------------------------------------------------------- truncate


	/**
	 * Sets the maximum length of the string. Longer strings will be simply truncated.
	 */
	public static String truncate(String string, int length) {
		if (string.length() > length) {
			string = string.substring(0, length);
		}
		return string;
	}

	// ---------------------------------------------------------------- split

	/**
	 * Splits a string in several parts (tokens) that are separated by delimiter.
	 * Delimiter is <b>always</b> surrounded by two strings! If there is no
	 * content between two delimiters, empty string will be returned for that
	 * token. Therefore, the length of the returned array will always be:
	 * #delimiters + 1.
	 * <p>
	 * Method is much, much faster then regexp <code>String.split()</code>,
	 * and a bit faster then <code>StringTokenizer</code>.
	 *
	 * @param src       string to split
	 * @param delimeter split delimiter
	 *
	 * @return array of split strings
	 */
	public static String[] split(String src, String delimeter) {
		int maxparts = (src.length() / delimeter.length()) + 2;		// one more for the last
		int[] positions = new int[maxparts];
		int dellen = delimeter.length();

		int i, j = 0;
		int count = 0;
		positions[0] = - dellen;
		while ((i = src.indexOf(delimeter, j)) != -1) {
			count++;
			positions[count] = i;
			j = i + dellen;
		}
		count++;
		positions[count] = src.length();

		String[] result = new String[count];

		for (i = 0; i < count; i++) {
			result[i] = src.substring(positions[i] + dellen, positions[i + 1]);
		}
		return result;
	}

	/**
	 * Splits a string in several parts (tokens) that are separated by delimiter
	 * characters. Delimiter may contains any number of character and it is
	 * always surrounded by two strings.
	 *
	 * @param src    source to examine
	 * @param d      string with delimiter characters
	 *
	 * @return array of tokens
	 */
	public static String[] splitc(String src, String d) {
		if ((d.length() == 0) || (src.length() == 0) ) {
			return new String[] {src};
		}
		char[] delimiters = d.toCharArray();
		char[] srcc = src.toCharArray();

		int maxparts = srcc.length + 1;
		int[] start = new int[maxparts];
		int[] end = new int[maxparts];

		int count = 0;

		start[0] = 0;
		int s = 0, e;
		if (CharUtil.equalsOne(srcc[0], delimiters) == true) {	// string starts with delimiter
			end[0] = 0;
			count++;
			s = CharUtil.findFirstDiff(srcc, 1, delimiters);
			if (s == -1) {							// nothing after delimiters
				return new String[] {EMPTY, EMPTY};
			}
			start[1] = s;							// new start
		}
		while (true) {
			// find new end
			e = CharUtil.findFirstEqual(srcc, s, delimiters);
			if (e == -1) {
				end[count] = srcc.length;
				break;
			}
			end[count] = e;

			// find new start
			count++;
			s = CharUtil.findFirstDiff(srcc, e, delimiters);
			if (s == -1) {
				start[count] = end[count] = srcc.length;
				break;
			}
			start[count] = s;
		}
		count++;
		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = src.substring(start[i], end[i]);
		}
		return result;
	}

	/**
	 * Splits a string in several parts (tokens) that are separated by single delimiter
	 * characters. Delimiter is always surrounded by two strings.
	 *
	 * @param src           source to examine
	 * @param delimiter     delimiter character
	 *
	 * @return array of tokens
	 */
	public static String[] splitc(String src, char delimiter) {
		if (src.length() == 0) {
			return new String[] {EMPTY};
		}
		char[] srcc = src.toCharArray();

		int maxparts = srcc.length + 1;
		int[] start = new int[maxparts];
		int[] end = new int[maxparts];

		int count = 0;

		start[0] = 0;
		int s = 0, e;
		if (srcc[0] == delimiter) {	// string starts with delimiter
			end[0] = 0;
			count++;
			s = CharUtil.findFirstDiff(srcc, 1, delimiter);
			if (s == -1) {							// nothing after delimiters
				return new String[] {EMPTY, EMPTY};
			}
			start[1] = s;							// new start
		}
		while (true) {
			// find new end
			e = CharUtil.findFirstEqual(srcc, s, delimiter);
			if (e == -1) {
				end[count] = srcc.length;
				break;
			}
			end[count] = e;

			// find new start
			count++;
			s = CharUtil.findFirstDiff(srcc, e, delimiter);
			if (s == -1) {
				start[count] = end[count] = srcc.length;
				break;
			}
			start[count] = s;
		}
		count++;
		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = src.substring(start[i], end[i]);
		}
		return result;
	}


	// ---------------------------------------------------------------- indexof and ignore cases

	/**
	 * Finds first occurrence of a substring in the given source but within limited range [start, end).
	 * It is fastest possible code, but still original <code>String.indexOf(String, int)</code>
	 * is much faster (since it uses char[] value directly) and should be used when no range is needed.
	 *
	 * @param src		source string for examination
	 * @param sub		substring to find
	 * @param startIndex	starting index
	 * @param endIndex		ending index
	 * @return index of founded substring or -1 if substring not found
	 */
	public static int indexOf(String src, String sub, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}
		int sublen = sub.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : startIndex;
		}

		int total = endIndex - sublen + 1;
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (src.charAt(i) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub.charAt(j) != src.charAt(k)) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of a character in the given source but within limited range (start, end].
	 */
	public static int indexOf(String src, char c, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}
		for (int i = startIndex; i < endIndex; i++) {
			if (src.charAt(i) == c) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds the first occurrence of a character in the given source but within limited range (start, end].
	 */
	public static int indexOfIgnoreCase(String src, char c, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}
		c = Character.toLowerCase(c);
		for (int i = startIndex; i < endIndex; i++) {
			if (Character.toLowerCase(src.charAt(i)) == c) {
				return i;
			}
		}
		return -1;
	}



	/**
	 * Finds first index of a substring in the given source string with ignored case.
	 *
	 * @param src    source string for examination
	 * @param subS   substring to find
	 *
	 * @return index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 */
	public static int indexOfIgnoreCase(String src, String subS) {
		return indexOfIgnoreCase(src, subS, 0, src.length());
	}

	/**
	 * Finds first index of a substring in the given source string with ignored
	 * case. This seems to be the fastest way doing this, with common string
	 * length and content (of course, with no use of Boyer-Mayer type of
	 * algorithms). Other implementations are slower: getting char array first,
	 * lower casing the source string, using String.regionMatch etc.
	 *
	 * @param src        source string for examination
	 * @param subS       substring to find
	 * @param startIndex starting index from where search begins
	 *
	 * @return index of founded substring or -1 if substring is not found
	 */
	public static int indexOfIgnoreCase(String src, String subS, int startIndex) {
		return indexOfIgnoreCase(src, subS, startIndex, src.length());
	}
	/**
	 * Finds first index of a substring in the given source string and range with
	 * ignored case.
	 *
	 * @param src		source string for examination
	 * @param sub		substring to find
	 * @param startIndex	starting index from where search begins
	 * @param endIndex		endint index
	 * @return index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 */
	public static int indexOfIgnoreCase(String src, String sub, int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		if (endIndex > srclen) {
			endIndex = srclen;
		}

		int sublen = sub.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : startIndex;
		}
		sub = sub.toLowerCase();
		int total = endIndex - sublen + 1;
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (Character.toLowerCase(src.charAt(i)) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				char source = Character.toLowerCase(src.charAt(k));
				if (sub.charAt(j) != source) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}


	/**
	 * Finds last index of a substring in the given source string with ignored
	 * case.
	 *
	 * @param s      source string
	 * @param subS   substring to find
	 *
	 * @return last index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 * @see #lastIndexOfIgnoreCase(String, String, int)
	 */
	public static int lastIndexOfIgnoreCase(String s, String subS) {
		return lastIndexOfIgnoreCase(s, subS, s.length(), 0);
	}

	/**
	 * Finds last index of a substring in the given source string with ignored
	 * case.
	 *
	 * @param src        source string for examination
	 * @param subS       substring to find
	 * @param startIndex starting index from where search begins
	 *
	 * @return last index of founded substring or -1 if substring is not found
	 * @see #indexOfIgnoreCase(String, String, int)
	 */
	public static int lastIndexOfIgnoreCase(String src, String subS, int startIndex) {
		return lastIndexOfIgnoreCase(src, subS, startIndex, 0);
	}
	/**
	 * Finds last index of a substring in the given source string with ignored
	 * case in specified range.
	 *
	 * @param src		source to examine
	 * @param sub		substring to find
	 * @param startIndex	starting index
	 * @param endIndex		end index
	 * @return last index of founded substring or -1 if substring is not found
	 */
	public static int lastIndexOfIgnoreCase(String src, String sub, int startIndex, int endIndex) {
		int sublen = sub.length();
		int srclen = src.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : (startIndex < -1 ? -1 : startIndex);
		}
		sub = sub.toLowerCase();
		int total = srclen - sublen;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.toLowerCase(src.charAt(i)) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				char source = Character.toLowerCase(src.charAt(k));
				if (sub.charAt(j) != source) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds last index of a substring in the given source string in specified range [end, start]
	 * See {@link #indexOf(String, String, int, int)}  for details about the speed.
	 *
	 * @param src		source to examine
	 * @param sub		substring to find
	 * @param startIndex	starting index
	 * @param endIndex		end index
	 * @return last index of founded substring or -1 if substring is not found
	 */
	public static int lastIndexOf(String src, String sub, int startIndex, int endIndex) {
		int sublen = sub.length();
		int srclen = src.length();
		if (sublen == 0) {
			return startIndex > srclen ? srclen : (startIndex < -1 ? -1 : startIndex);
		}
		int total = srclen - sublen;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		char c = sub.charAt(0);
	mainloop:
		for (int i = startIndex; i >= endIndex; i--) {
			if (src.charAt(i) != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub.charAt(j) != src.charAt(k)) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}

	/**
	 * Finds last index of a character in the given source string in specified range [end, start]
	 */
	public static int lastIndexOf(String src, char c, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		for (int i = startIndex; i >= endIndex; i--) {
			if (src.charAt(i) == c) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds last index of a character in the given source string in specified range [end, start]
	 */
	public static int lastIndexOfIgnoreCase(String src, char c, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		c = Character.toLowerCase(c);
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.toLowerCase(src.charAt(i)) == c) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOfWhitespace(String src) {
		return lastIndexOfWhitespace(src, src.length(), 0);
	}

	/**
	 * Returns last index of a whitespace.
	 */
	public static int lastIndexOfWhitespace(String src, int startIndex) {
		return lastIndexOfWhitespace(src, startIndex, 0);
	}

	/**
	 * Returns last index of a whitespace.
	 */
	public static int lastIndexOfWhitespace(String src, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.isWhitespace(src.charAt(i))) {
				return i;
			}
		}
		return -1;
	}


	public static int lastIndexOfNonWhitespace(String src) {
		return lastIndexOfNonWhitespace(src, src.length(), 0);
	}
	public static int lastIndexOfNonWhitespace(String src, int startIndex) {
		return lastIndexOfNonWhitespace(src, startIndex, 0);
	}
	public static int lastIndexOfNonWhitespace(String src, int startIndex, int endIndex) {
		int total = src.length() - 1;
		if (total < 0) {
			return -1;
		}
		if (startIndex >= total) {
			startIndex = total;
		}
		if (endIndex < 0) {
			endIndex = 0;
		}
		for (int i = startIndex; i >= endIndex; i--) {
			if (Character.isWhitespace(src.charAt(i)) == false) {
				return i;
			}
		}
		return -1;
	}

	// ---------------------------------------------------------------- starts and ends

	/**
	 * Tests if this string starts with the specified prefix with ignored case.
	 *
	 * @param src    source string to test
	 * @param subS   starting substring
	 *
	 * @return <code>true</code> if the character sequence represented by the argument is
	 *         a prefix of the character sequence represented by this string;
	 *         <code>false</code> otherwise.
	 */
	public static boolean startsWithIgnoreCase(String src, String subS) {
		return startsWithIgnoreCase(src, subS, 0);
	}

	/**
	 * Tests if this string starts with the specified prefix with ignored case
	 * and with the specified prefix beginning a specified index.
	 *
	 * @param src        source string to test
	 * @param subS       starting substring
	 * @param startIndex index from where to test
	 *
	 * @return <code>true</code> if the character sequence represented by the argument is
	 *         a prefix of the character sequence represented by this string;
	 *         <code>false</code> otherwise.
	 */
	public static boolean startsWithIgnoreCase(String src, String subS, int startIndex) {
		String sub = subS.toLowerCase();
		int sublen = sub.length();
		if (startIndex + sublen > src.length()) {
			return false;
		}
		int j = 0;
		int i = startIndex;
		while (j < sublen) {
			char source = Character.toLowerCase(src.charAt(i));
			if (sub.charAt(j) != source) {
				return false;
			}
			j++; i++;
		}
		return true;
	}

	/**
	 * Tests if this string ends with the specified suffix.
	 *
	 * @param src    String to test
	 * @param subS   suffix
	 *
	 * @return <code>true</code> if the character sequence represented by the argument is
	 *         a suffix of the character sequence represented by this object;
	 *         <code>false</code> otherwise.
	 */
	public static boolean endsWithIgnoreCase(String src, String subS) {
		String sub = subS.toLowerCase();
		int sublen = sub.length();
		int j = 0;
		int i = src.length() - sublen;
		if (i < 0) {
			return false;
		}
		while (j < sublen) {
			char source = Character.toLowerCase(src.charAt(i));
			if (sub.charAt(j) != source) {
				return false;
			}
			j++; i++;
		}
		return true;
	}

	/**
	 * Returns if string starts with given character.
	 */
	public static boolean startsWithChar(String s, char c) {
		if (s.length() == 0) {
			return false;
		}
		return s.charAt(0) == c;
	}

	/**
	 * Returns if string ends with provided character.
	 */
	public static boolean endsWithChar(String s, char c) {
		if (s.length() == 0) {
			return false;
		}
		return s.charAt(s.length() - 1) == c;
	}


	// ---------------------------------------------------------------- count substrings

	/**
	 * Counts substring occurrences in a source string.
	 *
	 * @param source	source string
	 * @param sub		substring to count
	 * @return			number of substring occurrences
	 */
	public static int count(String source, String sub) {
		return count(source, sub, 0);
	}
	public static int count(String source, String sub, int start) {
		int count = 0;
		int j = start;
		int sublen = sub.length();
		if (sublen == 0) {
			return 0;
		}
		while (true) {
			int i = source.indexOf(sub, j);
			if (i == -1) {
				break;
			}
			count++;
			j = i + sublen;
		}
		return count;
	}

	public static int count(String source, char c) {
		return count(source, c, 0);
	}
	public static int count(String source, char c, int start) {
		int count = 0;
		int j = start;
		while (true) {
			int i = source.indexOf(c, j);
			if (i == -1) {
				break;
			}
			count++;
			j = i + 1;
		}
		return count;
	}



	/**
	 * Count substring occurrences in a source string, ignoring case.
	 *
	 * @param source	source string
	 * @param sub		substring to count
	 * @return			number of substring occurrences
	 */
	public static int countIgnoreCase(String source, String sub) {
		int count = 0;
		int j = 0;
		int sublen = sub.length();
		if (sublen == 0) {
			return 0;
		}
		while (true) {
			int i = indexOfIgnoreCase(source, sub, j);
			if (i == -1) {
				break;
			}
			count++;
			j = i + sublen;
		}
		return count;
	}

	// ---------------------------------------------------------------- string arrays

	/**
	 * Finds the very first index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code> if
	 * noting found.
	 *
	 * @param s      source string
	 * @param arr    string array
	 */
	public static int[] indexOf(String s, String arr[]) {
		return indexOf(s, arr, 0);
	}
	/**
	 * Finds the very first index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code>
	 * if noting found.
	 *
	 * @param s      source string
	 * @param arr    string array
	 * @param start  starting position
	 */
	public static int[] indexOf(String s, String arr[], int start) {
		int arrLen = arr.length;
		int index = Integer.MAX_VALUE;
		int last = -1;
		for (int j = 0; j < arrLen; j++) {
			int i = s.indexOf(arr[j], start);
			if (i != -1) {
				if (i < index) {
					index = i;
					last = j;
				}
			}
		}
		return last == -1 ? null : new int[] {last, index};
	}

	/**
	 * Finds the very first index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code>
	 * if noting found.
	 *
	 * @param s      source string
	 * @param arr    string array
	 */
	public static int[] indexOfIgnoreCase(String s, String arr[]) {
		return indexOfIgnoreCase(s, arr, 0);
	}
	/**
	 * Finds the very first index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code>
	 * if noting found.
	 *
	 * @param s      source string
	 * @param arr    string array
	 * @param start  starting position
	 */
	public static int[] indexOfIgnoreCase(String s, String arr[], int start) {
		int arrLen = arr.length;
		int index = Integer.MAX_VALUE;
		int last = -1;
		for (int j = 0; j < arrLen; j++) {
			int i = indexOfIgnoreCase(s, arr[j], start);
			if (i != -1) {
				if (i < index) {
					index = i;
					last = j;
				}
			}
		}
		return last == -1 ? null : new int[] {last, index};
	}

	/**
	 * Finds the very last index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code>
	 * if noting found.
	 *
	 * @param s      source string
	 * @param arr    string array
	 */
	public static int[] lastIndexOf(String s, String arr[]) {
		return lastIndexOf(s, arr, s.length());
	}
	/**
	 * Finds the very last index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code>
	 * if noting found.
	 *
	 * @param s         source string
	 * @param arr       string array
	 * @param fromIndex starting position
	 */
	public static int[] lastIndexOf(String s, String arr[], int fromIndex) {
		int arrLen = arr.length;
		int index = -1;
		int last = -1;
		for (int j = 0; j < arrLen; j++) {
			int i = s.lastIndexOf(arr[j], fromIndex);
			if (i != -1) {
				if (i > index) {
					index = i;
					last = j;
				}
			}
		}
		return last == -1 ? null : new int[] {last, index};
	}

	/**
	 * Finds the very last index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code>
	 * if noting found.
	 *
	 * @param s      source string
	 * @param arr    string array
	 *
	 * @return int[2]
	 */
	public static int[] lastIndexOfIgnoreCase(String s, String arr[]) {
		return lastIndexOfIgnoreCase(s, arr, s.length());
	}
	/**
	 * Finds the very last index of a substring from the specified array. It
	 * returns an int[2] where int[0] represents the substring index and int[1]
	 * represents position where substring was found. Returns <code>null</code>
	 * if noting found.
	 *
	 * @param s         source string
	 * @param arr       string array
	 * @param fromIndex starting position
	 */
	public static int[] lastIndexOfIgnoreCase(String s, String arr[], int fromIndex) {
		int arrLen = arr.length;
		int index = -1;
		int last = -1;
		for (int j = 0; j < arrLen; j++) {
			int i = lastIndexOfIgnoreCase(s, arr[j], fromIndex);
			if (i != -1) {
				if (i > index) {
					index = i;
					last = j;
				}
			}
		}
		return last == -1 ? null : new int[] {last, index};
	}

	/**
	 * Compares two string arrays.
	 *
	 * @param as     first string array
	 * @param as1    second string array
	 *
	 * @return <code>true</code> if all array elements matches
	 */
	public static boolean equals(String as[], String as1[]) {
	    if (as.length != as1.length) {
	        return false;
	    }
	    for (int i = 0; i < as.length; i++) {
	        if (as[i].equals(as1[i]) == false) {
	            return false;
	        }
	    }
	    return true;
	}
	/**
	 * Compares two string arrays.
	 *
	 * @param as     first string array
	 * @param as1    second string array
	 *
	 * @return true if all array elements matches
	 */
	public static boolean equalsIgnoreCase(String as[], String as1[]) {
		if (as.length != as1.length) {
			return false;
		}
		for (int i = 0; i < as.length; i++) {
			if (as[i].equalsIgnoreCase(as1[i]) == false) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Replaces many substring at once. Order of string array is important.
	 *
	 * @param s      source string
	 * @param sub    substrings array
	 * @param with   replace with array
	 *
	 * @return string with all occurrences of substrings replaced
	 */
	public static String replace(String s, String[] sub, String[] with) {
		if ((sub.length != with.length) || (sub.length == 0)) {
			return s;
		}
		int start = 0;
		StringBuilder buf = new StringBuilder(s.length());
		while (true) {
			int[] res = indexOf(s, sub, start);
			if (res == null) {
				break;
			}
			int end = res[1];
			buf.append(s.substring(start, end));
			buf.append(with[res[0]]);
			start = end + sub[res[0]].length();
		}
		buf.append(s.substring(start));
		return buf.toString();
	}

	/**
	 * Replaces many substring at once. Order of string array is important.
	 *
	 * @param s      source string
	 * @param sub    substrings array
	 * @param with   replace with array
	 *
	 * @return string with all occurrences of substrings replaced
	 */
	public static String replaceIgnoreCase(String s, String[] sub, String[] with) {
		if ((sub.length != with.length) || (sub.length == 0)) {
			return s;
		}
		int start = 0;
		StringBuilder buf = new StringBuilder(s.length());
		while (true) {
			int[] res = indexOfIgnoreCase(s, sub, start);
			if (res == null) {
				break;
			}
			int end = res[1];
			buf.append(s.substring(start, end));
			buf.append(with[res[0]]);
			start = end + sub[0].length();
		}
		buf.append(s.substring(start));
		return buf.toString();
	}


	// ---------------------------------------------------------------- the one

	/**
	 * Compares string with at least one from the provided array.
	 * If at least one equal string is found, returns its index.
	 * Otherwise, <code>-1</code> is returned.
	 */
	public static int equalsOne(String src, String[] dest) {
		for (int i = 0; i < dest.length; i++) {
			if (src.equals(dest[i])) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Compares string with at least one from the provided array, ignoring case.
	 * If at least one equal string is found, it returns its index.
	 * Otherwise, <code>-1</code> is returned.
	 */
	public static int equalsOneIgnoreCase(String src, String[] dest) {
		for (int i = 0; i < dest.length; i++) {
			if (src.equalsIgnoreCase(dest[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if string starts with at least one string from the provided array.
	 * If at least one string is matched, it returns its index.
	 * Otherwise, <code>-1</code> is returned.
	 */
	public static int startsWithOne(String src, String[] dest) {
		for (int i = 0; i < dest.length; i++) {
			String m = dest[i];
			if (m == null) {
				continue;
			}
			if (src.startsWith(m)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if string starts with at least one string from the provided array.
	 * If at least one string is matched, it returns its index.
	 * Otherwise, <code>-1</code> is returned.
	 */
	public static int startsWithOneIgnoreCase(String src, String[] dest) {
		for (int i = 0; i < dest.length; i++) {
			String m = dest[i];
			if (m == null) {
				continue;
			}
			if (startsWithIgnoreCase(src, m)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Checks if string ends with at least one string from the provided array.
	 * If at least one string is matched, it returns its index.
	 * Otherwise, <code>-1</code> is returned.
	 */
	public static int endsWithOne(String src, String[] dest) {
		for (int i = 0; i < dest.length; i++) {
			String m = dest[i];
			if (m == null) {
				continue;
			}
			if (src.endsWith(m)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if string ends with at least one string from the provided array.
	 * If at least one string is matched, it returns its index.
	 * Otherwise, <code>-1</code> is returned.
	 */
	public static int endsWithOneIgnoreCase(String src, String[] dest) {
		for (int i = 0; i < dest.length; i++) {
			String m = dest[i];
			if (m == null) {
				continue;
			}
			if (endsWithIgnoreCase(src, m)) {
				return i;
			}
		}
		return -1;
	}


	// ---------------------------------------------------------------- char based


	/**
	 * @see #indexOfChars(String, String, int)
	 */
	public static int indexOfChars(String string, String chars) {
		return indexOfChars(string, chars, 0);
	}

	/**
	 * Returns the very first index of any char from provided string, starting from specified index offset.
	 * Returns index of founded char, or <code>-1</code> if nothing found.
	 */
	public static int indexOfChars(String string, String chars, int startindex) {
		int stringLen = string.length();
		int charsLen = chars.length();
		if (startindex < 0) {
			startindex = 0;
		}
		for (int i = startindex; i < stringLen; i++) {
			char c = string.charAt(i);
			for (int j = 0; j < charsLen; j++) {
				if (c == chars.charAt(j)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int indexOfChars(String string, char[] chars) {
		return indexOfChars(string, chars, 0);
	}

	/**
	 * Returns the very first index of any char from provided string, starting from specified index offset.
	 * Returns index of founded char, or <code>-1</code> if nothing found.
	 */
	public static int indexOfChars(String string, char[] chars, int startindex) {
		int stringLen = string.length();
		int charsLen = chars.length;
		for (int i = startindex; i < stringLen; i++) {
			char c = string.charAt(i);
			for (int j = 0; j < charsLen; j++) {
				if (c == chars[j]) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns first index of a whitespace character.
	 */
	public static int indexOfWhitespace(String string) {
		return indexOfWhitespace(string, 0, string.length());
	}

	public static int indexOfWhitespace(String string, int startindex) {
		return indexOfWhitespace(string, startindex, string.length());
	}

	/**
	 * Returns first index of a whitespace character, starting from specified index offset.
	 */
	public static int indexOfWhitespace(String string, int startindex, int endindex) {
		for (int i = startindex; i < endindex; i++) {
			if (CharUtil.isWhitespace(string.charAt(i))) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfNonWhitespace(String string) {
		return indexOfNonWhitespace(string, 0, string.length());
	}
	public static int indexOfNonWhitespace(String string, int startindex) {
		return indexOfNonWhitespace(string, startindex, string.length());
	}
	public static int indexOfNonWhitespace(String string, int startindex, int endindex) {
		for (int i = startindex; i < endindex; i++) {
			if (CharUtil.isWhitespace(string.charAt(i)) == false) {
				return i;
			}
		}
		return -1;
	}


	// ---------------------------------------------------------------- strip, trim

	/**
	 * Strips leading char if string starts with one.
	 */
	public static String stripLeadingChar(String string, char c) {
		if (string.length() > 0) {
			if (string.charAt(0) == c) {
				return string.substring(1);
			}
		}
		return string;
	}

	/**
	 * Strips trailing char if string ends with one.
	 */
	public static String stripTrailingChar(String string, char c) {
		if (string.length() > 0) {
			if (string.charAt(string.length() - 1) == c) {
				return string.substring(0, string.length() - 1);
			}
		}
		return string;
	}


	/**
	 * Trims array of strings. <code>null</code> array elements are ignored.
	 */
	public static void trimAll(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			if (string != null) {
				strings[i] = string.trim();
			}
		}
	}

	/**
	 * Trims array of strings where empty strings are set to <code>null</code>.
	 * <code>null</code> elements of the array are ignored.
	 * @see #trimDown(String)
	 */
	public static void trimDownAll(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			if (string != null) {
				strings[i] = trimDown(string);
			}
		}
	}


	/**
	 * Trims string and sets to <code>null</code> if trimmed string is empty.
	 */
	public static String trimDown(String string) {
		string = string.trim();
		if (string.length() == 0) {
			string = null;
		}
		return string;
	}

	/**
	 * Crops string by setting empty strings to <code>null</code>.
	 */
	public static String crop(String string) {
		if (string.length() == 0) {
			return null;
		}
		return string;
	}

	/**
	 * Crops all elements of string array.
	 */
	public static void cropAll(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			if (string != null) {
				string = crop(strings[i]);
			}
			strings[i] = string;
		}
	}

	/**
	 * Trim whitespaces from the left.
	 */
	public static String trimLeft(String src) {
		int len = src.length();
		int st = 0;
		while ((st < len) && (CharUtil.isWhitespace(src.charAt(st)))) {
			st++;
		}
		return st > 0 ? src.substring(st) : src;
	}

	/**
	 * Trim whitespaces from the right.
	 */
	public static String trimRight(String src) {
		int len = src.length();
		int count = len;
		while ((len > 0) && (CharUtil.isWhitespace(src.charAt(len - 1)))) {
			len--;
		}
		return (len < count) ? src.substring(0, len) : src;
	}


	// ---------------------------------------------------------------- regions

	/**
	 * @see #indexOfRegion(String, String, String, int)
	 */
	public static int[] indexOfRegion(String string, String leftBoundary, String rightBoundary) {
		return indexOfRegion(string, leftBoundary, rightBoundary, 0);
	}


	/**
	 * Returns indexes of the first region without escaping character.
	 * @see #indexOfRegion(String, String, String, char, int)
	 */
	public static int[] indexOfRegion(String string, String leftBoundary, String rightBoundary, int offset) {
		int ndx = offset;
		int[] res = new int[4];
		ndx = string.indexOf(leftBoundary, ndx);
		if (ndx == -1) {
			return null;
		}
		res[0] = ndx;
		ndx += leftBoundary.length();
		res[1] = ndx;

		ndx = string.indexOf(rightBoundary, ndx);
		if (ndx == -1) {
			return null;
		}
		res[2] = ndx;
		res[3] = ndx + rightBoundary.length();
		return res;
	}


	/**
	 * @see #indexOfRegion(String, String, String, char, int)
	 */
	public static int[] indexOfRegion(String string, String leftBoundary, String rightBoundary, char escape) {
		return indexOfRegion(string, leftBoundary, rightBoundary, escape, 0);
	}

	/**
	 * Returns indexes of the first string region. Region is defined by its left and right boundary.
	 * Return value is an array of the following indexes:
	 * <ul>
	 * <li>start of left boundary index</li>
	 * <li>region start index, i.e. end of left boundary</li>
	 * <li>region end index, i.e. start of right boundary</li>
	 * <li>end of right boundary index</li> 
	 * </ul>
	 * <p>
	 * Escape character may be used to prefix boundaries so they can be ignored.
	 * Double escaped region will be found, and first index of the result will be
	 * decreased to include one escape character. 
	 * If region is not founded, <code>null</code> is returned. 
	 */
	public static int[] indexOfRegion(String string, String leftBoundary, String rightBoundary, char escape, int offset) {
		int ndx = offset;
		int[] res = new int[4];
		while (true) {
			ndx = string.indexOf(leftBoundary, ndx);
			if (ndx == -1) {
				return null;
			}
			int leftBoundaryLen = leftBoundary.length();
			if (ndx > 0) {
				if (string.charAt(ndx - 1) == escape) {				// check previous char
					boolean cont = true;
					if (ndx > 1) {
						if (string.charAt(ndx - 2) == escape) {		// check double escapes
							ndx--;
							leftBoundaryLen++;
							cont = false;
						}
					}
					if (cont) {
						ndx += leftBoundaryLen;
						continue;
					}
				}
			}
			res[0] = ndx;
			ndx += leftBoundaryLen;
			res[1] = ndx;

			while (true) {		// find right boundary
				ndx = string.indexOf(rightBoundary, ndx);
				if (ndx == -1) {
					return null;
				}
				if (ndx > 0) {
					if (string.charAt(ndx - 1) == escape) {
						ndx += rightBoundary.length();
						continue;
					}
				}
				res[2] = ndx;
				res[3] = ndx + rightBoundary.length();
				return res;
			}
		}
	}


	// ---------------------------------------------------------------- join

	/**
	 * Joins an array of strings into one string.
	 */
	public static String join(String... parts) {
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			sb.append(part);
		}
		return sb.toString();
	}

	/**
	 * Joins list of iterable elements.
	 */
	public static String join(Iterable<?> elements, String separator) {
		if (elements == null) {
			return EMPTY;
		}
		StringBuilder buf = new StringBuilder();
		if (separator == null) {
			separator = EMPTY;
		}
		for (Object o : elements) {
			if (buf.length() > 0) {
				buf.append(separator);
			}
			buf.append(o);
		}
		return buf.toString();
	}

	// ---------------------------------------------------------------- charset

	/**
	 * Converts string charset.
	 */
	public static String convertCharset(String source, String srcCharsetName, String newCharsetName) throws UnsupportedEncodingException {
		return new String(source.getBytes(srcCharsetName), newCharsetName);
	}

	/**
	 * Escapes a string using java rules.
	 */
	public static String escapeJava(String str) {
		char[] chars = str.toCharArray();

		StringBuilder sb = new StringBuilder(str.length());
		for (char c : chars) {
			switch (c) {
				case '\b' : sb.append("\\b"); break;
				case '\t' : sb.append("\\t"); break;
				case '\n' : sb.append("\\n"); break;
				case '\f' : sb.append("\\f"); break;
				case '\r' : sb.append("\\r"); break;
				case '\"' : sb.append("\\\""); break;
				case '\\' : sb.append("\\\\"); break;
				default:
					if ((c < 32) || (c > 127)) {
						String hex = Integer.toHexString(c);
						sb.append("\\u");
						for (int k = hex.length(); k < 4; k++) {
							sb.append('0');
						}
						sb.append(hex);
					} else {
						sb.append(c);
					}
			}
		}
		return sb.toString();
	}

	/**
	 * Unescapes a string using java rules.
	 */
	public static String unescapeJava(String str) {
		char[] chars = str.toCharArray();

		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c != '\\') {
				sb.append(c);
				continue;
			}
			i++;
			c = chars[i];
			switch (c) {
				case 'b': sb.append('\b'); break;
				case 't': sb.append('\t'); break;
				case 'n': sb.append('\n'); break;
				case 'f': sb.append('\f'); break;
				case 'r': sb.append('\r'); break;
				case '"': sb.append('\"'); break;
				case '\\': sb.append('\\'); break;
				case 'u' :
					char hex = (char) Integer.parseInt(new String(chars, i + 1, 4), 16);
					sb.append(hex);
					i += 4;
					break;
				default:
					throw new IllegalArgumentException("Invalid escaping character: " + c);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(escapeJava("\\u\tđavo"));
	}


	// ---------------------------------------------------------------- chars

	/**
	 * Safely compares provided char with char on given location.
	 */
	public static boolean isCharAtEqual(String string, int index, char charToCompare) {
		if ((index < 0) || (index >= string.length())) {
			return false;
		}
		return string.charAt(index) == charToCompare;
	}


	// ---------------------------------------------------------------- surround

	/**
	 * @see #surround(String, String, String)
	 */
	public static String surround(String string, String fix) {
		return surround(string, fix, fix);
	}

	/**
	 * Surrounds the string with provided prefix and suffix if such missing from string.
	 */
	public static String surround(String string, String prefix, String suffix) {
		if (string.startsWith(prefix) == false) {
			string = prefix + string;
		}
		if (string.endsWith(suffix) == false) {
			string += suffix;
		}
		return string;
	}

	/**
	 * Inserts prefix if doesn't exist.
	 */
	public static String prefix(String string, String prefix) {
		if (string.startsWith(prefix) == false) {
			string = prefix + string;
		}
		return string;
	}

	/**
	 * Appends suffix if doesn't exist.
	 */
	public static String suffix(String string, String suffix) {
		if (string.endsWith(suffix) == false) {
			string += suffix;
		}
		return string;
	}

	// ---------------------------------------------------------------- cut

	/**
	 * Cuts the string from beginning to the first index of provided substring.
	 */
	public static String cutToIndexOf(String string, String substring) {
		int i = string.indexOf(substring);
		if (i != -1) {
			string = string.substring(0, i);
		}
		return string;
	}
	/**
	 * Cuts the string from beginning to the first index of provided char.
	 */
	public static String cutToIndexOf(String string, char c) {
		int i = string.indexOf(c);
		if (i != -1) {
			string = string.substring(0, i);
		}
		return string;
	}

	/**
	 * Cuts the string from the first index of provided substring to the end.
	 */
	public static String cutFromIndexOf(String string, String substring) {
		int i = string.indexOf(substring);
		if (i != -1) {
			string = string.substring(i);
		}
		return string;
	}
	/**
	 * Cuts the string from the first index of provided char to the end.
	 */
	public static String cutFromIndexOf(String string, char c) {
		int i = string.indexOf(c);
		if (i != -1) {
			string = string.substring(i);
		}
		return string;
	}

	/**
	 * Cuts prefix if exists.
	 */
	public static String cutPrefix(String string, String prefix) {
		if (string.startsWith(prefix)) {
			string = string.substring(prefix.length());
		}
		return string;
	}

	/**
	 * Cuts sufix if exists.
	 */
	public static String cutSuffix(String string, String suffix) {
		if (string.endsWith(suffix)) {
			string = string.substring(0, string.length() - suffix.length());
		}
		return string;
	}

	/**
	 * @see #cutSurrounding(String, String, String)
	 */
	public static String cutSurrounding(String string, String fix) {
		return cutSurrounding(string, fix, fix);
	}

	/**
	 * Removes surrounding prefix and suffixes.
	 */
	public static String cutSurrounding(String string, String prefix, String suffix) {
		int start = 0;
		int end = string.length();
		if (string.startsWith(prefix)) {
			start = prefix.length();
		}
		if (string.endsWith(suffix)) {
			end -= suffix.length();
		}

		return string.substring(start, end);
	}


	// ---------------------------------------------------------------- escaped

	/**
	 * Returns <code>true</code> if character at provided index position is escaped
	 * by escape character.
	 */
	public static boolean isCharAtEscaped(String src, int ndx, char escapeChar) {
		if (ndx == 0) {
			return false;
		}
		ndx--;
		return src.charAt(ndx) == escapeChar;
	}

	public static int indexOfUnescapedChar(String src, char sub, char escapeChar) {
		return indexOfUnescapedChar(src, sub, escapeChar, 0);
	}

	public static int indexOfUnescapedChar(String src, char sub, char escapeChar, int startIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		int srclen = src.length();
		char previous;
		char c = 0;
		for (int i = startIndex; i < srclen; i++) {
			previous = c;
			c = src.charAt(i);
			if (c == sub) {
				if (i > startIndex) {
					if (previous == escapeChar) {
						continue;
					}
				}
				return i;
			}
		}
		return -1;

	}

	// ---------------------------------------------------------------- insert

	public static String insert(String src, String insert) {
		return insert(src, insert, 0);
	}

	/**
	 * Inserts a string on provided offset.
	 */
	public static String insert(String src, String insert, int offset) {
		if (offset < 0) {
			offset = 0;
		}
		if (offset > src.length()) {
			offset = src.length();
		}
		StringBuilder sb = new StringBuilder(src);
		sb.insert(offset, insert);
		return sb.toString();
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Creates a new string that contains the provided string a number of times.
	 */
	public static String repeat(String source, int count) {
		StringBuilder result = new StringBuilder(count * source.length());
		while (count > 0) {
			result.append(source);
			count --;
		}
		return result.toString();
	}

	public static String repeat(char c, int count) {
		StringBuilder result = new StringBuilder(count);
		while (count > 0) {
			result.append(c);
			count --;
		}
		return result.toString();
	}

	/**
	 * Reverse a string.
	 */
	public static String reverse(String s) {
		StringBuilder result = new StringBuilder(s.length());
		for (int i = s.length() -1; i >= 0; i--) {
			result.append(s.charAt(i));
		}
		return result.toString();
	}

	/**
	 * Returns max common prefix of two strings.
	 */
	public static String maxCommonPrefix(String one, String two) {
        final int minLength = Math.min(one.length(), two.length());

        final StringBuilder sb = new StringBuilder(minLength);
        for (int pos = 0; pos < minLength; pos++) {
            final char currentChar = one.charAt(pos);
            if (currentChar != two.charAt(pos)) {
                break;
            }
            sb.append(currentChar);
        }

		return sb.toString();
	}

	/**
	 * Changes a camelCase string value to space separated
	 */
	public static String camelCaseToWords(String input) {
		StringBuilder s = new StringBuilder();
		int length = input.length();
		for (int i = 0; i < length; i++) {
			char ch = input.charAt(i);
			if (Character.isUpperCase(ch) && (i > 0)) {
				s.append(' ');
				ch = Character.toLowerCase(ch);
			} else if ((i == 0) && !Character.isUpperCase(ch)) {
				ch = Character.toUpperCase(ch);
			}
			if (ch == '.') {
				s.append(' ');
			}
			s.append(ch);
		}
		return s.toString();
	}

	/**
	 * Changes a space separated string value to camelCase
	 */
	public static String wordsToCamelCase(String input) {
		StringBuilder s = new StringBuilder();
		int length = input.length();
		boolean upperCase = false;

		for (int i = 0; i < length; i++) {
			char ch = input.charAt(i);
			if (ch == ' ') {
				upperCase = true;
			} else if (upperCase) {
				s.append(Character.toUpperCase(ch));
				upperCase = false;
			} else {
				s.append(ch);
			}
		}
		return s.toString();
	}

	// ---------------------------------------------------------------- prefixes

	/**
	 * Finds common prefix for several strings. Returns an empty string if
	 * arguments do not have a common prefix.
	 */
	public static String findCommonPrefix(String... strings) {

		StringBuilder prefx = new StringBuilder();
		int index = 0;
		char c = 0;

		loop:
		while (true) {
			for (int i = 0; i < strings.length; i++) {

				String s = strings[i];
				if (index == s.length()) {
					break loop;
				}

				if (i == 0) {
					c = s.charAt(index);
				} else {
					if (s.charAt(index) != c) {
						break loop;
					}
				}
			}

			index++;
			prefx.append(c);
		}
		return prefx.length() == 0 ? StringPool.EMPTY : prefx.toString();
	}


}
