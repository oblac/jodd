// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Lagarto parser util.
 */
public class LagartoParserUtil {

	/**
	 * Returns <code>true</code> if character sequence region starts with provided string.
	 */
	public static boolean regionStartWith(CharSequence charSequence, int start, int end, String matchingString) {
		int len = end - start;
		int targetLen = matchingString.length();

		if (targetLen > len) {
			return false;
		}

		for (int i = 0; i < targetLen; i++) {
			if (charSequence.charAt(start) != matchingString.charAt(i)) {
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
	public static int regionIndexOf(CharSequence charSequence, int start, int end, char matchingChar) {
		for (int i = start; i < end; i++) {
			if (charSequence.charAt(i) == matchingChar) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of matching string in character sequence region.
	 * Returns <code>-1</code> if matching string is not found.
	 */
	public static int regionIndexOf(CharSequence charSequence, int start, int end, String match) {
		int matchLength = match.length();

		if (start + matchLength > end) {
			return -1;
		}

		int upToIndex = end - matchLength + 1;

		outloop:
		for (int i = start; i < upToIndex; i++) {

			for (int j = 0; j < matchLength; j++) {
				if (charSequence.charAt(i + j) != match.charAt(j)) {
					continue outloop;
				}
			}
			return i;
		}
		return -1;
	}

	/**
	 * Calculates line and row of current position in character sequence.
	 * Returns <code>int[2]</code> where first number is line and second
	 * number is row.
	 */
	public static int[] calculateLineAndRow(CharSequence charSequence, int position) {
		int line = 1;

		int offset = 0;
		int lastNewLineOffset = 0;

		while (offset < position) {
			char c = charSequence.charAt(offset);

			if (c == '\n') {
				line++;
				lastNewLineOffset++;
			}

			offset++;
		}

		return new int[] {line, position - lastNewLineOffset};
	}

}