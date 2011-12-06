// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Simple <code>InputStream</code> subclass to fetch <b>all</b> bytes from a String.
 * @see AsciiInputStream
 */
public class StringInputStream extends InputStream implements Serializable {

	protected int strOffset;
	protected int charOffset;
	protected int available;
	protected String str;

	public StringInputStream(String s) {
		str = s;
		available = s.length() << 1;
	}

	@Override
	public int read() throws IOException {
		if (available == 0) {
			return -1;
		}
		available--;
		char c = str.charAt(strOffset);
		if (charOffset == 0) {
			charOffset = 1;
			return (c & 0x0000ff00) >> 8;
		} else {
			charOffset = 0;
			strOffset++;
			return c & 0x000000ff;
		}
	}

	@Override
	public int available() throws IOException {
		return available;
	}

}
