// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.servlet.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Response wrapper that takes everything the client would normally output
 * and saves it in byte array. It works for both output stream and writers.
 */
public class ByteArrayResponseWrapper extends HttpServletResponseWrapper {

	private final PrintWriter writer;
	private final FastByteArrayServletOutputStream out;

	public ByteArrayResponseWrapper(final HttpServletResponse response) {
		super(response);
		out = new FastByteArrayServletOutputStream();

		// create a PrintWriter-wrapper over the output stream
		// that is not buffered and is immediately flush-able
		// so to reflect the changes on out immediately.

		writer = new PrintWriter(new OutputStreamWriter(out) {
			@Override
			public void write(final int c) throws IOException {
				super.write(c);
				super.flush();
			}

			@Override
			public void write(final char[] cbuf, final int off, final int len) throws IOException {
				super.write(cbuf, off, len);
				super.flush();
			}

			@Override
			public void write(final String str, final int off, final int len) throws IOException {
				super.write(str, off, len);
				super.flush();
			}
		});
	}

	/**
	 * Returns the wrapped output stream.
	 */
	@Override
	public ServletOutputStream getOutputStream() {
		return out;
	}

	/**
	 * Returns a writer-wrapper that is backed up by the
	 * wrapped output stream.
	 */
	@Override
	public PrintWriter getWriter() {
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

