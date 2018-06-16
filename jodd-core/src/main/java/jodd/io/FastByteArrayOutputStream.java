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

import jodd.util.StringUtil;
import jodd.buffer.FastByteBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class implements an {@link OutputStream} in which the data is
 * written into a byte array. The buffer automatically grows as data
 * is written to it.
 * <p>
 * The data can be retrieved using {@link #toByteArray()} and {@link #toString}.
 * <p>
 * Closing a {@link FastByteArrayOutputStream} has no effect. The methods in
 * this class can be called after the stream has been closed without
 * generating an {@link IOException}.
 * <p>
 * This is an alternative implementation of the {@code java.io.FastByteArrayOutputStream}
 * class. The original implementation only allocates 32 bytes at the beginning.
 * As this class is designed for heavy duty it starts at 1024 bytes. In contrast
 * to the original it doesn't reallocate the whole memory block but allocates
 * additional buffers. This way no buffers need to be garbage collected and
 * the contents don't have to be copied to the new buffer. This class is
 * designed to behave exactly like the original. The only exception is the
 * deprecated {@code java.io.FastByteArrayOutputStream#toString(int)} method that has been ignored.
 */
public class FastByteArrayOutputStream extends OutputStream {

	private final FastByteBuffer buffer;

	/**
	 * Creates a new byte array {@link OutputStream}. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastByteArrayOutputStream() {
		this(1024);
	}

	/**
	 * Creates a new byte array output stream, with a buffer capacity of
	 * the specified size, in bytes.
	 *
	 * @param size the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public FastByteArrayOutputStream(final int size) {
		buffer = new FastByteBuffer(size);
	}

	/**
	 * @see OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(final byte[] b, final int off, final int len) {
		buffer.append(b, off, len);
	}

	/**
	 * Writes single byte.
	 */
	@Override
	public void write(final int b) {
		buffer.append((byte) b);
	}

	/**
	 * @see ByteArrayOutputStream#size()
	 */
	public int size() {
		return buffer.size();
	}

	/**
	 * Closing a {@link FastByteArrayOutputStream} has no effect. The methods in
	 * this class can be called after the stream has been closed without
	 * generating an {@link IOException}.
	 */
	@Override
	public void close() {
		//nop
	}

	/**
	 * @see ByteArrayOutputStream#reset()
	 */
	public void reset() {
		buffer.clear();
	}

	/**
	 * @see ByteArrayOutputStream#writeTo(OutputStream)
	 */
	public void writeTo(final OutputStream out) throws IOException {
		out.write(buffer.toArray());
	}

	/**
	 * @see ByteArrayOutputStream#toByteArray()
	 */
	public byte[] toByteArray() {
		return buffer.toArray();
	}

	/**
	 * @see ByteArrayOutputStream#toString()
	 */
	@Override
	public String toString() {
		return new String(toByteArray());
	}

	/**
	 * @see ByteArrayOutputStream#toString(String)
	 */
	public String toString(final String enc) {
		return StringUtil.newString(toByteArray(), enc);
	}
}