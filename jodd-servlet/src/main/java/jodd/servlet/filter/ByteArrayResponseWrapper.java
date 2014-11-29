// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.OutputStreamWriter;
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

		// create a PrintWriter-wrapper over the output stream
		// that is not buffered and is immediately flush-able
		// so to reflect the changes on out immediately.

		writer = new PrintWriter(new OutputStreamWriter(out) {
			@Override
			public void write(int c) throws IOException {
				super.write(c);
				super.flush();
			}

			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				super.write(cbuf, off, len);
				super.flush();
			}

			@Override
			public void write(String str, int off, int len) throws IOException {
				super.write(str, off, len);
				super.flush();
			}
		});
	}

	/**
	 * Returns the wrapped output stream.
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return out;
	}

	/**
	 * Returns a writer-wrapper that is backed up by the
	 * wrapped output stream.
	 */
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

	/**
	 * Returns current buffer size.
	 */
	@Override
	public int getBufferSize() {
		return out.wrapped.size();
	}

	// ---------------------------------------------------------------- add-on

	/**
	 * Get the underlying byte array.
	 */
	public byte[] toByteArray() {
		return out.getByteArrayStream().toByteArray();
	}
}

