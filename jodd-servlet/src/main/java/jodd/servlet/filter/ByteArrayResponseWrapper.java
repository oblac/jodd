// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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
	private boolean writerTaken = false;

	public ByteArrayResponseWrapper(HttpServletResponse response) {
		super(response);
		out = new FastByteArrayServletOutputStream();
		writer = new PrintWriter(out);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (writerTaken) {
			writerTaken = false;
			writer.flush();
		}
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		writerTaken = true;
		return writer;
	}

	/**
	 * Get a string representation of the entire buffer.
	 */
	@Override
	public String toString() {
		flushBuffer();
		return out.getByteArrayStream().toString();
	}

	@Override
	public void reset() {
		out.reset();
	}

	@Override
	public void flushBuffer() {
		if (writerTaken) {
			writerTaken = false;
			writer.flush();
		}
		try {
			super.flushBuffer();
		} catch (IOException ignore) {
		}
	}

	// ---------------------------------------------------------------- add-on

	/**
	 * Get the underlying byte array.
	 */
	public byte[] toByteArray() {
		this.flushBuffer();
		return out.getByteArrayStream().toByteArray();
	}
}

