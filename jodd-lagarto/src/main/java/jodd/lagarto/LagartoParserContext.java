// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import java.nio.CharBuffer;

/**
 * Lagarto parser context, holder of various
 * data during the parsing.
 */
public class LagartoParserContext {

	protected final CharBuffer input;
	protected long startTime;
	protected long endTime;
	protected long elapsedTime;
	protected int offset;

	public LagartoParserContext(CharBuffer input) {
		this.input = input;
	}

	/**
	 * Returns parsing source.
	 */
	public CharBuffer getInput() {
		return input;
	}

	/**
	 * Returns time when parsing started.
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Returns time when parsing stopped or was interrupted.
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * Returns elapsed time after the parsing is done.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Returns offset of current processing.
	 */
	public int getOffset() {
		return offset;
	}
}