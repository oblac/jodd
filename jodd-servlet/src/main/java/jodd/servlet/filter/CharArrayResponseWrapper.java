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

import jodd.io.FastCharArrayWriter;
import jodd.util.StringPool;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Response wrapper that takes everything the client would normally output
 * and saves it in character array. It works only for writers, byte stream
 * is not buffered.
 */
public class CharArrayResponseWrapper extends HttpServletResponseWrapper {

	protected FastCharArrayWriter writer;
	protected FastByteArrayServletOutputStream out;
	protected PrintWriter printWriter;

	/**
	 * Initializes wrapper by creating {@link FastCharArrayWriter} that will
	 * be used to accumulate the response.
	 */
	public CharArrayResponseWrapper(final HttpServletResponse response) {
		super(response);
	}

	/**
	 * Returns buffered writer. Buffer will be created if not already used.
	 */
	@Override
	public PrintWriter getWriter() {
		if (writer == null) {
			writer = new FastCharArrayWriter();
			printWriter = new PrintWriter(writer);
		}
		return printWriter;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new IOException("Using output stream is not supported");
	}

	/**
	 * Get a String representation of the entire buffer.
	 * <p>
	 * <b>Do not</b> call this method multiple times on the same wrapper as
	 * new string is created every time.
	 */
	@Override
	public String toString() {
		if (writer == null) {
			return StringPool.EMPTY;
		}
		return writer.toString();
	}

	@Override
	public void reset() {
		if (writer != null) {
			writer.reset();
		}
	}

	// ---------------------------------------------------------------- add-on

	/**
	 * Explicitly closes the writer.
	 */
	public void close() {
		if (writer != null) {
			writer.close();
		}
	}

	/**
	 * Returns the size (number of characters) of written data.
	 */
	public int size() {
		if (writer == null) {
			return 0;
		}
		return writer.size();
	}

	/**
	 * Get the underlying character array or <code>null</code> if
	 * writer not used.
	 */
	public char[] toCharArray() {
		if (writer == null) {
			return null;
		}
		return writer.toCharArray();
	}

}
