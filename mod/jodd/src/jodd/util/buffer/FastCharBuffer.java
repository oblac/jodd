// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.buffer;

/**
 * Fast, fast <code>char</code> buffer with some additional features.
 */
public class FastCharBuffer implements CharSequence, Appendable {

	// @@generated

	private char[][] buffers = new char[16][];
	private int buffersCount;
	private int currentBufferIndex = -1;
	private char[] currentBuffer;
	private int offset;
	private int count;

	/**
	 * Creates a new <code>char</code> buffer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastCharBuffer() {
		this(1024);
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
		needNewBuffer(size);
	}

	private void needNewBuffer(int newCount) {
		if (currentBufferIndex < buffersCount - 1) {	// recycling old buffer
			offset = 0;
			currentBufferIndex++;
			currentBuffer = buffers[currentBufferIndex];
		} else {										// creating new buffer
			int newBufferSize;
			if (currentBuffer == null) {
				newBufferSize = newCount;
			} else {
				newBufferSize = Math.max(
						currentBuffer.length << 1,
						newCount - count);		// this will give no free additional space

			}

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
	}

	/**
	 * Appends <code>char</code> array to buffer.
	 */
	public FastCharBuffer append(char[] b, int off, int len) {
		int end = off + len;
		if ((off < 0)
				|| (off > b.length)
				|| (len < 0)
				|| (end > b.length)
				|| (end < 0)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return this;
		}
		int newCount = count + len;
		int remaining = len;
		while (remaining > 0) {
			int part = Math.min(remaining, currentBuffer.length - offset);
			System.arraycopy(b, end - remaining, currentBuffer, offset, part);
			remaining -= part;
			offset += part;
			count += part;
			if (remaining > 0) {
				needNewBuffer(newCount);
			}
		}
		return this;
	}

	/**
	 * Appends <code>char</code> array to buffer.
	 */
	public FastCharBuffer append(char[] b) {
		return append(b, 0, b.length);
	}

	/**
	 * Appends single <code>char</code> to buffer.
	 */
	public FastCharBuffer append(char value) {
		if (offset == currentBuffer.length) {
			needNewBuffer(count + 1);
		}

		currentBuffer[offset] = value;
		offset++;
		count++;

		return this;
	}

	/**
	 * Appends another fast buffer to this one.
	 */
	FastCharBuffer append(FastCharBuffer buff) {
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
		return count;
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
	public void reset() {
		count = 0;
		offset = 0;
		currentBufferIndex = 0;
		currentBuffer = buffers[currentBufferIndex];
		buffersCount = 1;
	}

	/**
	 * Creates <code>char</code> array from buffered content.
	 */
	public char[] toArray() {
		int remaining = count;
		int pos = 0;
		char[] array = new char[count];
		for (char[] buf : buffers) {
			int c = Math.min(buf.length, remaining);
			System.arraycopy(buf, 0, array, pos, c);
			pos += c;
			remaining -= c;
			if (remaining == 0) {
				break;
			}
		}
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
		if (index >= count) {
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
		return count;
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

	// ---------------------------------------------------------------- additional appenders

	/**
	 * Appends string content to buffer.
	 */
	public FastCharBuffer append(String string) {
		return append(string.toCharArray());
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
