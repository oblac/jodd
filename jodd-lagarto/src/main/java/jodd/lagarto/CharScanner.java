// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.nio.CharBuffer;

/**
 * Scanner over a char buffer.
 */
public class CharScanner {

	protected char[] input;
	protected int ndx = 0;
	protected int total;

	/**
	 * Initializes scanner.
	 */
	protected void initialize(char[] input) {
		this.input = input;
		this.ndx = -1;
		this.total = input.length;
	}

	// ---------------------------------------------------------------- find

	/**
	 * Finds index of target block from current position.
	 * Returns index of founded block (it is not consumed).
	 * Returns <code>-1</code> if block is not found.
	 */
	public final int find(char[] target) {
		return find(target, total);
	}

	public final int find(char target) {
		return find(target, total);
	}

	/**
	 * Finds index of target block from current position up to some limit.
	 * Returns index of founded block (it is not consumed).
	 * Returns <code>-1</code> if block is not found.
	 */
	public final int find(char[] target, int end) {
		int start = ndx;

		while (ndx < end) {
			if (match(target, false)) {
				break;
			}
			ndx++;
		}

		int foundNdx = ndx;
		ndx = start;

		if (foundNdx == end) {
			return -1;
		}

		return foundNdx;
	}

	/**
	 * Finds character from current position up to some limit.
	 * Returns index of founded char (it is not consumed).
	 * Returns <code>-1</code> if block is not found.
	 */
	public final int find(char target, int end) {
		int start = ndx;

		while (ndx < end) {
			char c = input[ndx];

			if (c == target) {
				break;
			}
			ndx++;
		}

		int foundNdx = ndx;
		ndx = start;

		if (foundNdx == end) {
			return -1;
		}

		return foundNdx;
	}

	// ---------------------------------------------------------------- read

	/**
	 * Reads and consumes only allowed characters from current position.
	 * Returns <code>null</code> if nothing is consumed.
	 */
	public final CharSequence read(char[] allowedChars) {
		int start = ndx;

		while (ndx < total) {
			char c = input[ndx];

			if (!_matchAny(c, allowedChars)) {
				break;
			}
			ndx++;
		}

		return charSequence(start, ndx);
	}

	// ---------------------------------------------------------------- match

	public final boolean match(char[] target) {
		return match(target, true);
	}

	/**
	 * Matches current location to a target.
	 * If match is positive, the target is consumed.
	 */
	public final boolean match(char[] target, boolean consume) {
		if (ndx + target.length > total) {
			return false;
		}

		int j = ndx;

		for (int i = 0; i < target.length; i++, j++) {
			if (input[j] != target[i]) {
				return false;
			}
		}

		if (consume) {
			ndx = j;
		}

		return true;
	}

	public final boolean matchIgnoreCase(char[] target) {
		return matchCaseInsensitiveWithUpper(target, true);
	}

	/**
	 * todo ovaj method pretrvara char u upper case i radi match sa targetom koji je vec u uppercase zbog performansi
	 * @param uppercaseTarget
	 * @param consume
	 * @return
	 */
	public final boolean matchCaseInsensitiveWithUpper(char[] uppercaseTarget, boolean consume) {
		if (ndx + uppercaseTarget.length > total) {
			return false;
		}

		int j = ndx;

		for (int i = 0; i < uppercaseTarget.length; i++, j++) {
			char c = _toUppercase(input[j]);

			if (c != uppercaseTarget[i]) {
				return false;
			}
		}

		if (consume) {
			ndx = j;
		}

		return true;
	}

	public final boolean match(char target) {
		return match(target, true);
	}

	/**
	 * Returns <code>true</code> if current char matches
	 * the target char. Current char gets consumed if
	 * it was matched.
	 */
	public final boolean match(char target, boolean consume) {
		if (ndx >= total) {
			return false;
		}
		if (input[ndx] == target) {
			if (consume) {
				ndx++;
			}
			return true;
		}

		return false;
	}

	// ---------------------------------------------------------------- utils

	/**
	 * Checks if char matches to any character in provided array.
	 */
	private static boolean _matchAny(char c, char[] target) {
		for (char t : target) {
			if (c == t) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts ASCII char to uppercase.
	 * Simple and fast.
	 * todo use CharUtil
	 */
	private static char _toUppercase(char c) {
		if ((c >= 'a') && (c <= 'z')) {
			c -= 32;
		}
		return c;
	}

	// ---------------------------------------------------------------- char sequences

	/**
	 * Returns <code>true</code> if two sequences are equal.
	 */
	public final boolean equals(CharSequence one, CharSequence two) {
		int len = one.length();
		if (len != two.length()) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			if (one.charAt(i) != two.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if two sequences are equal regarding the case.
	 */
	public final boolean equalsIgnoreCase(CharSequence one, CharSequence two) {
		int len = one.length();
		if (len != two.length()) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			if (_toUppercase(one.charAt(i)) != _toUppercase(two.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Creates char sub-sequence from the input.
	 */
	public final CharSequence charSequence(int from, int to) {
		int len = to - from;
		if (len == 0) {
			return EMPTY_CHAR_SEQUENCE;
		}
		return CharBuffer.wrap(input, from, len);
	}

	private static CharSequence EMPTY_CHAR_SEQUENCE = CharBuffer.wrap(new char[0]);

	/**
	 * Creates substring from the input.
	 */
	public final String substring(int from, int to) {
		return new String(input, from, to - from);
	}

	// ---------------------------------------------------------------- position

	private int lastOffset = -1;
	private int lastLine;
	private int lastLastNewLineOffset;

	/**
	 * Returns <code>true</code> if EOF.
	 */
	public final boolean isEOF() {
		return ndx >= total;
	}

	public Position position() {
		return position(ndx);
	}

	/**
	 * Calculates {@link Position current position}: offset, line and column.
	 */
	public Position position(int position) {
		int line;
		int offset;
		int lastNewLineOffset;

		if (position > lastOffset) {
			line = 1;
			offset = 0;
			lastNewLineOffset = 0;
		} else {
			line = lastLine;
			offset = lastOffset;
			lastNewLineOffset = lastLastNewLineOffset;
		}

		while (offset < position) {
			char c = input[offset];

			if (c == '\n') {
				line++;
				lastNewLineOffset = offset + 1;
			}

			offset++;
		}

		lastOffset = offset;
		lastLine = line;
		lastLastNewLineOffset = lastNewLineOffset;

		return new Position(position, line, position - lastNewLineOffset + 1);
	}

	/**
	 * Current position.
	 */
	public static class Position {

		private final int offset;
		private final int line;
		private final int column;

		public Position(int offset, int line, int column) {
			this.offset = offset;
			this.line = line;
			this.column = column;
		}
		public Position(int offset) {
			this.offset = offset;
			this.line = -1;
			this.column = -1;
		}

		public String toString() {
			if (offset == -1) {
				return "[" + line + ':' + column + ']';
			}
			if (line == -1) {
				return "[@" + offset + ']';
			}
			return "[" + line + ':' + column + " @" + offset + ']';
		}
	}

}