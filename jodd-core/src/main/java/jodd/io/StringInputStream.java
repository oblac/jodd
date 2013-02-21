// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.util.CharUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Simple <code>InputStream</code> to fetch bytes from a String.
 * There are 3 modes how bytes can be processed:
 * <ul>
 *     <li><code>ALL</code> - all bytes are returned: both high and low byte of each character</li>
 *     <li><code>STRIP</code> - only low byte of each character is returned</li>
 *     <li><code>ASCII</code> - only low byte of each character is returned, but overloaded chars are marked with 0x3F</li>
 * </ul>
 */
public class StringInputStream extends InputStream implements Serializable {

	protected final String string;
	protected final Mode mode;
	protected int index;
	protected int charOffset;
	protected int available;

	/**
	 * Mode that defines how input string is processed.
	 */
	public enum Mode {
		/**
		 * Both lower and higher byte of string characters are processed.
		 */
		ALL,
		/**
		 * High bytes are simply cut off.
		 */
		STRIP,
		/**
		 * Returns only low bytes, marking overloaded chars with 0x3F.
		 */
		ASCII
	}

	public StringInputStream(String string, Mode mode) {
		this.string = string;
		this.mode = mode;
		available = string.length();

		if (mode == Mode.ALL) {
			available <<= 1;
		}
	}

	@Override
	public int read() throws IOException {
		if (available == 0) {
			return -1;
		}
		available--;
		char c = string.charAt(index);

		switch (mode) {
			case ALL:
				if (charOffset == 0) {
					charOffset = 1;
					return (c & 0x0000ff00) >> 8;
				} else {
					charOffset = 0;
					index++;
					return c & 0x000000ff;
				}
			case STRIP:
				index++;
				return c & 0x000000ff;
			case ASCII:
				index++;
				return CharUtil.toAscii(c);
		}
		return -1;
	}

	@Override
	public int available() throws IOException {
		return available;
	}

}