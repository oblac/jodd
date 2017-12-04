// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.lagarto;

import jodd.util.CharArraySequence;
import jodd.util.CharUtil;

/**
 * Utility scanner over a char buffer.
 */
class Scanner {

	protected CharSequence input;
	protected int ndx = 0;
	protected int total;

	Scanner() { }

	/**
	 * Initializes scanner.
	 */
	protected void initialize(CharSequence input) {
		this.input = input;
		this.ndx = -1;
		this.total = input.length();
	}

	// ---------------------------------------------------------------- find

	/**
	 * Finds a character in some range and returns its index.
	 * Returns <code>-1</code> if character is not found.
	 */
	protected final int find(char target, int from, int end) {
		while (from < end) {
			if (input.charAt(from) == target) {
				break;
			}
			from++;
		}

		return (from == end) ? -1 : from;
	}

	/**
	 * Finds character buffer in some range and returns its index.
	 * Returns <code>-1</code> if character is not found.
	 */
	protected final int find(CharSequence target, int from, int end) {
		while (from < end) {
			if (match(target, from)) {
				break;
			}
			from++;
		}

		return (from == end) ? -1 : from;
	}

	// ---------------------------------------------------------------- match

	/**
	 * Matches char buffer with content on given location.
	 */
	protected final boolean match(CharSequence target, int ndx) {
		if (ndx + target.length() >= total) {
			return false;
		}

		int j = ndx;

		for (int i = 0; i < target.length(); i++, j++) {
			if (input.charAt(j) != target.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Matches char buffer with content at current location case-sensitive.
	 */
	public final boolean match(CharSequence target) {
		return match(target, ndx);
	}

	/**
	 * Matches char buffer given in uppercase with content at current location, that will
	 * be converted to upper case to make case-insensitive matching.
	 */
	public final boolean matchUpperCase(CharSequence uppercaseTarget) {
		if (ndx + uppercaseTarget.length() > total) {
			return false;
		}

		int j = ndx;

		for (int i = 0; i < uppercaseTarget.length(); i++, j++) {
			char c = CharUtil.toUpperAscii(input.charAt(j));

			if (c != uppercaseTarget.charAt(i)) {
				return false;
			}
		}

		return true;
	}

	// ---------------------------------------------------------------- char sequences

	/**
	 * Creates char sub-sequence from the input.
	 */
	protected final CharSequence charSequence(int from, int to) {
		if (from == to) {
			return CharArraySequence.EMPTY;
		}
		return input.subSequence(from, to);
	}

	// ---------------------------------------------------------------- position

	private int lastOffset = -1;
	private int lastLine;
	private int lastLastNewLineOffset;

	/**
	 * Returns <code>true</code> if EOF.
	 */
	protected final boolean isEOF() {
		return ndx >= total;
	}

	/**
	 * Calculates {@link Position current position}: offset, line and column.
	 */
	protected Position position(int position) {
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
			char c = input.charAt(offset);

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