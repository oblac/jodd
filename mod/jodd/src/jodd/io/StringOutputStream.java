// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.util.CharUtil;
import jodd.JoddDefault;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.IOException;

/**
 * Provides an OutputStream to an internal String. Internally converts bytes
 * to a Strings and stores them in an internal StringBuffer.
 */
public class StringOutputStream extends OutputStream implements Serializable {
	
	/**
	 * The internal destination StringBuffer.
	 */
	protected final StringBuffer buf;
	protected final String encoding;

	/**
	 * Creates new StringOutputStream, makes a new internal StringBuffer.
	 */
	public StringOutputStream() {
		this(JoddDefault.encoding);
	}
	public StringOutputStream(String encoding) {
		super();
		buf = new StringBuffer();
		this.encoding = encoding;
	}

	/**
	 * Returns the content of the internal StringBuffer as a String, the result
	 * of all writing to this OutputStream.
	 *
	 * @return returns the content of the internal StringBuffer
	 */
	@Override
	public String toString() {
		return buf.toString();
	}

	/**
	* Sets the internal StringBuffer to null.
	*/
	@Override
	public void close() {
		buf.setLength(0);

	}

	/**
	 * Writes and appends byte array to StringOutputStream.
	 *
	 * @param b      byte array
	 */
	@Override
	public void write(byte[] b) throws IOException {
		buf.append(CharUtil.toCharArray(b, encoding));
	}

	/**
	 * Writes and appends a byte array to StringOutputStream.
	 *
	 * @param b      the byte array
	 * @param off    the byte array starting index
	 * @param len    the number of bytes from byte array to write to the stream
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if ((off < 0) || (len < 0) || (off + len) > b.length) {
			throw new IndexOutOfBoundsException("Parameters out of bounds.");
		}
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++) {
			bytes[i] = b[off];
			off++;
		}
		buf.append(CharUtil.toCharArray(bytes, encoding));
	}

	/**
	 * Writes and appends a single byte to StringOutputStream.
	 *
	 * @param b      the byte as an int to add
	 */
	@Override
	public void write(int b) {
		buf.append((char)b);
	}
}
