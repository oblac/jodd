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

package jodd.servlet.wrapper;

import jodd.io.FastCharArrayWriter;
import jodd.servlet.filter.FastByteArrayServletOutputStream;

import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;

/**
 * Facade for both <code>PrintWriter</code> and <code>ServletOutputStream</code> of servlet response.
 * Uses fast {@link jodd.io.FastCharArrayWriter char array writter} and
 * {@link jodd.servlet.filter.FastByteArrayServletOutputStream byte output stream}.
 */
public class Buffer {

	protected FastCharArrayWriter bufferedWriter;
	protected FastByteArrayServletOutputStream bufferOutputStream;

	protected PrintWriter outWriter;
	protected ServletOutputStream outStream;

	/**
	 * Returns a writer.
	 */
	public PrintWriter getWriter() {
		if (outWriter == null) {
			if (outStream != null) {
				throw new IllegalStateException("Can't call getWriter() after getOutputStream()");
			}
			bufferedWriter = new FastCharArrayWriter();
			outWriter = new PrintWriter(bufferedWriter) {
				@Override
				public void close() {
					// do not close the print writer after rendering
					// since it will remove reference to bufferedWriter
				}
			};
		}
		return outWriter;
	}

	/**
	 * Returns a servlet output stream.
	 */
	public ServletOutputStream getOutputStream() {
		if (outStream == null) {
			if (outWriter != null) {
				throw new IllegalStateException("Can't call getOutputStream() after getWriter()");
			}
			bufferOutputStream = new FastByteArrayServletOutputStream();
			outStream = bufferOutputStream;
		}
		return outStream;
	}

	/**
	 * Returns <code>true</code> if streams are using.
	 */
	public boolean isUsingStream() {
		return outStream != null;
	}

	/**
	 * Returns buffered writer content as char array.
	 * Returns <code>null</code> if writer is not used.
	 */
	public char[] toCharArray() {
		if (bufferedWriter != null) {
			return bufferedWriter.toCharArray();
		}
		return null;
	}

	/**
	 * Returns buffered servlet output content as byte array.
	 * Returns <code>null</code> if writer is not used.
	 */
	public byte[] toByteArray() {
		if (bufferOutputStream != null) {
			return bufferOutputStream.getByteArrayStream().toByteArray();
		}
		return null;
	}

}