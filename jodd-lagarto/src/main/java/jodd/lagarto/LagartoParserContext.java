// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

/**
 * Lagarto parser context, holder of various
 * data needed for whole parsing process.
 * // todo do we need this?
 */
public class LagartoParserContext {

	protected long startTime;
	protected long endTime;
	protected long elapsedTime;
	protected int offset;

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