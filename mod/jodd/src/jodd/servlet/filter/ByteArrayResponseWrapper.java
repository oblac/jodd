// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Response wrapper that takes everything the client would normally output
 * and saves it in byte array. It works for both output stream and writers.
 */
public class ByteArrayResponseWrapper extends HttpServletResponseWrapper {

	private final PrintWriter writer;
	private final FastByteArrayServletOutputStream out;

	public ByteArrayResponseWrapper(HttpServletResponse response) {
		super(response);
		out = new FastByteArrayServletOutputStream();
		writer = new PrintWriter(out);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return writer;
	}

	/**
	 * Get a string representation of the entire buffer.
	 */
	@Override
	public String toString() {
		return out.getByteArrayStream().toString();
	}

	@Override
	public void reset() {
		out.reset();
	}

	// ---------------------------------------------------------------- add-on

	/**
	 * Get the underlying byte array.
	 */
	public byte[] toByteArray() {
		return out.getByteArrayStream().toByteArray();
	}
}

