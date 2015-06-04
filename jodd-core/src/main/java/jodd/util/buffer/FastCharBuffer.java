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

package jodd.util.buffer;

/**
 * Fast, fast <code>char</code> buffer with some additional features.
 * This buffer implementation does not store all data
 * in single array, but in array of chunks.
 */
public class FastCharBuffer implements CharSequence, Appendable {

	// @@generated

	private char[][] buffers = new char[16][];
	private int buffersCount;
	private int currentBufferIndex = -1;
	private char[] currentBuffer;
	private int offset;
	private int size;
	private final int minChunkLen;

	/**
	 * Creates a new <code>char</code> buffer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastCharBuffer() {
		this.minChunkLen = 1024;
	}

	/**
	 * Creates a new <code>char</code> buffer, with a buffer capacity of
	 * the specified size, in bytes.
	 *
	 * @param size the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public FastCharBuffer(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Invalid size: " + size);
		}
		this.minChunkLen = size;
	}

	/**
	 * Prepares next chunk to match new size.
	 * The minimal length of new chunk is <code>minChunkLen</code>.
	 */
	private void needNewBuffer(int newSize) {
		int delta = newSize - size;
		int newBufferSize = Math.max(minChunkLen, delta);

		currentBufferIndex++;
		currentBuffer = new char[newBufferSize];
		offset = 0;

		// add buffer
		if (currentBufferIndex >= buffers.length) {
			int newLen = buffers.length << 1;
			char[][] newBuffers = new char[newLen][];
			System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
			buffers = newBuffers;
		}
		buffers[currentBufferIndex] = currentBuffer;
		buffersCount++;
	}

	/**
	 * Appends <code>char</code> array to buffer.
	 */
	public FastCharBuffer append(char[] array, int off, int len) {
		int end = off + len;
		if ((off < 0)
				|| (len < 0)
				|| (end > array.length)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return this;
		}
		int newSize = size + len;
		int remaining = len;

		if (currentBuffer != null) {
			// first try to fill current buffer
			int part = Math.min(remaining, currentBuffer.length - offset);
			System.arraycopy(array, end - remaining, currentBuffer, offset, part);
			remaining -= part;
			offset += part;
			size += part;
		}

		if (remaining > 0) {
			// still some data left
			// ask for new buffer
			needNewBuffer(newSize);

			// then copy remaining
			// but this time we are sure that it will fit
			int part = Math.min(remaining, currentBuffer.length - offset);
			System.arraycopy(array, end - remaining, currentBuffer, offset, part);
			offset += part;
			size += part;
		}

		return this;
	}

	/**
	 * Appends <code>char</code> array to buffer.
	 */
	public FastCharBuffer append(char[] array) {
		return append(array, 0, array.length);
	}

	/**
	 * Appends single <code>char</code> to buffer.
	 */
	public FastCharBuffer append(char element) {
		if ((currentBuffer == null) || (offset == currentBuffer.length)) {
			needNewBuffer(size + 1);
		}

		currentBuffer[offset] = element;
		offset++;
		size++;

		return this;
	}

	/**
	 * Appends another fast buffer to this one.
	 */
	public FastCharBuffer append(FastCharBuffer buff) {
		if (buff.size == 0) {
			return this;
		}
		for (int i = 0; i < buff.currentBufferIndex; i++) {
			append(buff.buffers[i]);
		}
		append(buff.currentBuffer, 0, buff.offset);
		return this;
	}

	/**
	 * Returns buffer size.
	 */
	public int size() {
		return size;
	}

	/**
	 * Tests if this buffer has no elements.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns current index of inner <code>char</code> array chunk.
	 * Represents the index of last used inner array chunk.
	 */
	public int index() {
		return currentBufferIndex;
	}

	/**
	 * Returns the offset of last used element in current inner array chunk.
	 */
	public int offset() {
		return offset;
	}

	/**
	 * Returns <code>char</code> inner array chunk at given index.
	 * May be used for iterating inner chunks in fast manner.
	 */
	public char[] array(int index) {
		return buffers[index];
	}

	/**
	 * Resets the buffer content.
	 */
	public void clear() {
		size = 0;
		offset = 0;
		currentBufferIndex = -1;
		currentBuffer = null;
		buffersCount = 0;
	}

	/**
	 * Creates <code>char</code> array from buffered content.
	 */
	public char[] toArray() {
		int pos = 0;
		char[] array = new char[size];

		if (currentBufferIndex == -1) {
			return array;
		}

		for (int i = 0; i < currentBufferIndex; i++) {
			int len = buffers[i].length;
			System.arraycopy(buffers[i], 0, array, pos, len);
			pos += len;
		}

		System.arraycopy(buffers[currentBufferIndex], 0, array, pos, offset);

		return array;
	}

    /**
     * Creates <code>char</code> subarray from buffered content.
     */
	public char[] toArray(int start, int len) {
		int remaining = len;
		int pos = 0;
		char[] array = new char[len];

		if (len == 0) {
			return array;
		}

		int i = 0;
		while (start >= buffers[i].length) {
			start -= buffers[i].length;
			i++;
		}

		while (i < buffersCount) {
			char[] buf = buffers[i];
			int c = Math.min(buf.length - start, remaining);
			System.arraycopy(buf, start, array, pos, c);
			pos += c;
			remaining -= c;
			if (remaining == 0) {
				break;
			}
			start = 0;
			i++;
		}
		return array;
	}

	/**
	 * Returns <code>char</code> element at given index.
	 */
	public char get(int index) {
		if ((index >= size) || (index < 0)) {
			throw new IndexOutOfBoundsException();
		}
		int ndx = 0;
        while (true) {
			char[] b = buffers[ndx];
			if (index < b.length) {
				return b[index];
			}
			ndx++;
			index -= b.length;
		}
	}

	// @@generated

	/**
	 * Returns buffer length, same as {@link #size()}.
	 */
	public int length() {
		return size;
	}

	/**
	 * Returns buffer content as a string.
	 */
	public String toString() {
		return new String(toArray());
	}

	/**
	 * Returns char at given index.
	 */
	public char charAt(int index) {
		return get(index);
	}

	/**
	 * Returns sub sequence.
	 */
	public CharSequence subSequence(int start, int end) {
		int len = end - start;
		return new StringBuilder(len).append(toArray(start, len));
	}

	// ---------------------------------------------------------------- additional

	/**
	 * Appends string content to buffer.
	 */
	public FastCharBuffer append(String string) {
		int len = string.length();
		if (len == 0) {
			return this;
		}

		int end = offset + len;
		int newSize = size + len;
		int remaining = len;
		int start = 0;

		if (currentBuffer != null) {
			// first try to fill current buffer
			int part = Math.min(remaining, currentBuffer.length - offset);
			string.getChars(0, part, currentBuffer, offset);
			remaining -= part;
			offset += part;
			size += part;
			start += part;
		}

		if (remaining > 0) {
			// still some data left
			// ask for new buffer
			needNewBuffer(newSize);

			// then copy remaining
			// but this time we are sure that it will fit
			int part = Math.min(remaining, currentBuffer.length - offset);
			string.getChars(start, start + part, currentBuffer, offset);
			offset += part;
			size += part;
		}

		return this;
	}

	/**
	 * Appends character sequence to buffer.
	 */
	public FastCharBuffer append(CharSequence csq) {
		append(csq, 0, csq.length());
		return this;
	}

	/**
	 * Appends character sequence to buffer.
	 */
	public FastCharBuffer append(CharSequence csq, int start, int end) {
		for (int i = start; i < end; i++) {
			append(csq.charAt(i));
		}
		return this;
	}

}