// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import jodd.io.FastByteArrayOutputStream;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * Implementation of <code>ServletOutputStream</code> that buffers
 * inserted content.
 */
public class FastByteArrayServletOutputStream extends ServletOutputStream {

	protected final FastByteArrayOutputStream wrapped;

	public FastByteArrayServletOutputStream() {
		wrapped = new FastByteArrayOutputStream();
	}

	/**
	 * Returns wrapped output stream.
	 */
	public FastByteArrayOutputStream getByteArrayStream() {
		return wrapped;
	}

	/**
	 * Writes to wrapped buffer.
	 */
	@Override
	public void write(int i) throws IOException {
		wrapped.write(i);
	}

	public void reset() {
		wrapped.reset();
	}

}
