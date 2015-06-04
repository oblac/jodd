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

package jodd.io;

import jodd.util.buffer.FastCharBuffer;

import java.io.IOException;
import java.io.Writer;

/**
 * Similar as {@link jodd.io.FastByteArrayOutputStream} but for {@link Writer}.
 */
public class FastCharArrayWriter extends Writer {
	
	private final FastCharBuffer buffer;

	/**
	 * Creates a new writer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastCharArrayWriter() {
		this(1024);
	}

	/**
	 * Creates a new char array writer, with a buffer capacity of
	 * the specified size, in bytes.
	 *
	 * @param size the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public FastCharArrayWriter(int size) {
		buffer = new FastCharBuffer(size);
	}

	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] b, int off, int len) {
		buffer.append(b, off, len);
	}

	/**
	 * Writes single byte.
	 */
	@Override
	public void write(int b) {
		buffer.append((char) b);
	}

	@Override
	public void write(String s, int off, int len) {
		write(s.toCharArray(), off, len);
	}

	/**
	 * @see java.io.CharArrayWriter#size()
	 */
	public int size() {
		return buffer.size();
	}

	/**
	 * Closing a <code>FastCharArrayWriter</code> has no effect. The methods in
	 * this class can be called after the stream has been closed without
	 * generating an <code>IOException</code>.
	 */
	@Override
	public void close() {
		//nop
	}

	/**
	 * Flushing a <code>FastCharArrayWriter</code> has no effects.
	 */
	@Override
	public void flush() {
		//nop
	}

	/**
	 * @see java.io.CharArrayWriter#reset()
	 */
	public void reset() {
		buffer.clear();
	}

	/**
	 * @see java.io.CharArrayWriter#writeTo(java.io.Writer)
	 */
	public void writeTo(Writer out) throws IOException {
		int index = buffer.index();
		for (int i = 0; i < index; i++) {
			char[] buf = buffer.array(i);
			out.write(buf);
		}
		out.write(buffer.array(index), 0, buffer.offset());
	}

	/**
	 * @see java.io.CharArrayWriter#toCharArray()
	 */
	public char[] toCharArray() {
		return buffer.toArray();
	}

	/**
	 * @see java.io.CharArrayWriter#toString()
	 */
	@Override
	public String toString() {
		return new String(toCharArray());
	}
}