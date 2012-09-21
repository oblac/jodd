// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.CharBufferReader;

import java.nio.CharBuffer;

/**
 * Lagarto lexer.
 */
public final class LagartoLexer extends Lexer {

	private final CharSequence input;

	public LagartoLexer(CharBuffer input) {
		super(new CharBufferReader(input));
		this.input = input;
	}


	// ---------------------------------------------------------------- position

	private int lastOffset = -1;
	private int lastLine;
	private int lastLastNewLineOffset;

	/**
	 * Calculates {@link Position current position}: offset, line and column.
	 */
	public Position currentPosition() {

		int position = position();

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

		return new Position(position, line, position - lastNewLineOffset);
	}

	/**
	 * Current position.
	 */
	public static class Position {

		public int offset;
		public int line;
		public int column;

		public Position(int offset, int line, int column) {
			this.offset = offset;
			this.line = line;
			this.column = column;
		}
		public Position(int line, int column) {
			this.offset = -1;
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
