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

package jodd.buffer;

import jodd.util.CharArraySequence;

import java.util.Arrays;

/**
 * Faster {@code char} buffer.
 */
public class FastCharBuffer implements CharSequence, Appendable {

	private char[] buffer;
	private int offset;

	/**
	 * Creates a new {@code char} buffer. The buffer capacity is
	 * initially 64 chars, though its size increases if necessary.
	 */
	public FastCharBuffer() {
		this.buffer = new char[64];
	}

	/**
	 * Creates a new {@code char} buffer, with a buffer capacity of
	 * the specified size, in chars.
	 *
	 * @param size the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public FastCharBuffer(final int size) {
		this.buffer = new char[size];
	}

	/**
	 * Grows the buffer.
	 */
	private void grow(final int minCapacity) {
		final int oldCapacity = buffer.length;
		int newCapacity = oldCapacity << 1;
		if (newCapacity - minCapacity < 0) {
			// special case, min capacity is larger then a grow
			newCapacity = minCapacity + 512;
		}
		buffer = Arrays.copyOf(buffer, newCapacity);
	}

	/**
	 * Appends single {@code char} to buffer.
	 */
	@Override
	public FastCharBuffer append(final char element) {
		if (offset - buffer.length >= 0) {
			grow(offset);
		}

		buffer[offset++] = element;
		return this;
	}

	/**
	 * Appends {@code char} array to buffer.
	 */
	public FastCharBuffer append(final char[] array, final int off, final int len) {
		if (offset + len - buffer.length > 0) {
			grow(offset + len);
		}

		System.arraycopy(array, off, buffer, offset, len);
		offset += len;
		return this;
	}

	/**
	 * Appends {@code char} array to buffer.
	 */
	public FastCharBuffer append(final char[] array) {
		return append(array, 0, array.length);
	}

	/**
	 * Appends another fast buffer to this one.
	 */
	public FastCharBuffer append(final FastCharBuffer buff) {
		if (buff.offset == 0) {
			return this;
		}
		append(buff.buffer, 0, buff.offset);
		return this;
	}

	/**
	 * Returns buffer size.
	 */
	public int size() {
		return offset;
	}

	@Override
	public int length() {
		return offset;
	}

	/**
	 * Tests if this buffer has no elements.
	 */
	public boolean isEmpty() {
		return offset == 0;
	}

	/**
	 * Resets the buffer content.
	 */
	public void clear() {
		offset = 0;
	}

	/**
	 * Creates {@code char} array from buffered content.
	 */
	public char[] toArray() {
		return Arrays.copyOf(buffer, offset);
	}

	/**
	 * Creates {@code char} subarray from buffered content.
	 */
	public char[] toArray(final int start, final int len) {
		final char[] array = new char[len];

		if (len == 0) {
			return array;
		}

		System.arraycopy(buffer, start, array, 0, len);

		return array;
	}

	/**
	 * Returns {@code char} element at given index.
	 */
	public char get(final int index) {
		if (index >= offset) {
			throw new IndexOutOfBoundsException();
		}
		return buffer[index];
	}

	/**
	 * Appends character sequence to buffer.
	 */
	@Override
	public FastCharBuffer append(final CharSequence csq) {
		append(csq, 0, csq.length());
		return this;
	}

	/**
	 * Appends character sequence to buffer.
	 */
	@Override
	public FastCharBuffer append(final CharSequence csq, final int start, final int end) {
		for (int i = start; i < end; i++) {
			append(csq.charAt(i));
		}
		return this;
	}
	@Override
	public char charAt(final int index) {
		return get(index);
	}

	@Override
	public CharSequence subSequence(final int start, final int end) {
		return new CharArraySequence(buffer, start, end - start);
	}

	/**
	 * Returns a String of the char buffer.
	 * Please use {@code StringBuilder} instead, it is faster.
	 */
	@Override
	public String toString() {
		return new String(toArray());
	}
}