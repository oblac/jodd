// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.nio.CharBuffer;

/**
 * Lagarto parser util.
 */
public class LagartoParserUtil {

	/**
	 * Returns <code>true</code> if character sequence region starts with provided string.
	 */
	public static boolean regionStartWith(char[] chars, int start, int end, char[] matchingString) {
		int len = end - start;
		int targetLen = matchingString.length;

		if (targetLen > len) {
			return false;
		}

		for (int i = 0; i < targetLen; i++) {
			if (chars[start] != matchingString[i]) {
				return false;
			}
			start++;
		}

		return true;
	}

	/**
	 * Returns the index of matching character in character sequence region.
	 * Returns <code>-1</code> if character is not found.
	 */
	public static int regionIndexOf(char[] chars, int start, int end, char matchingChar) {
		for (int i = start; i < end; i++) {
			if (chars[i] == matchingChar) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of matching string in character sequence region.
	 * Returns <code>-1</code> if matching string is not found.
	 */
	public static int regionIndexOf(char[] chars, int start, int end, char[] match) {
		int matchLength = match.length;

		if (start + matchLength > end) {
			return -1;
		}

		int upToIndex = end - matchLength + 1;

		outloop:
		for (int i = start; i < upToIndex; i++) {

			for (int j = 0; j < matchLength; j++) {
				if (chars[i + j] != match[j]) {
					continue outloop;
				}
			}
			return i;
		}
		return -1;
	}

	/**
	 * Creates a <code>CharSequence</code> on given char array region.
	 * Returned sequence is just a 'view' of the input char array.
	 */
	public static CharSequence subSequence(char[] chars, int from, int to) {
		int len = to - from;
		if (len == 0) {
			return EMPTY_CHAR_SEQUENCE;
		}
		return CharBuffer.wrap(chars, from, len);
	}

	private static final CharSequence EMPTY_CHAR_SEQUENCE = CharBuffer.wrap(new char[0]);

}