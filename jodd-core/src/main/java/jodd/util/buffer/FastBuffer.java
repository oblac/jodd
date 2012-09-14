// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.buffer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Fast, fast <code>E</code> buffer with additional features.
 */
@SuppressWarnings("unchecked")
public class FastBuffer<E> implements RandomAccess, Iterable<E> {

	// @@generated

	private E[][] buffers = (E[][]) new Object[16][];
	private int buffersCount;
	private int currentBufferIndex = -1;
	private E[] currentBuffer;
	private int offset;
	private int count;

	/**
	 * Creates a new <code>E</code> buffer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastBuffer() {
		this(1024);
	}

	/**
	 * Creates a new <code>E</code> buffer, with a buffer capacity of
	 * the specified size, in bytes.
	 *
	 * @param size the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public FastBuffer(int size) {
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
			currentBuffer = (E[]) new Object[newBufferSize];
			offset = 0;

			// add buffer
			if (currentBufferIndex >= buffers.length) {
				int newLen = buffers.length << 1;
				E[][] newBuffers = (E[][]) new Object[newLen][];
                System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
                buffers = newBuffers;
			}
			buffers[currentBufferIndex] = currentBuffer;
			buffersCount++;
		}
	}

	/**
	 * Appends <code>E</code> array to buffer.
	 */
	public FastBuffer<E> append(E[] array, int off, int len) {
		int end = off + len;
		if ((off < 0)
				|| (off > array.length)
				|| (len < 0)
				|| (end > array.length)
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
			System.arraycopy(array, end - remaining, currentBuffer, offset, part);
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
	 * Appends <code>E</code> array to buffer.
	 */
	public FastBuffer<E> append(E[] array) {
		return append(array, 0, array.length);
	}

	/**
	 * Appends single <code>E</code> to buffer.
	 */
	public FastBuffer<E> append(E element) {
		if (offset == currentBuffer.length) {
			needNewBuffer(count + 1);
		}

		currentBuffer[offset] = element;
		offset++;
		count++;

		return this;
	}

	/**
	 * Appends another fast buffer to this one.
	 */
	public FastBuffer<E> append(FastBuffer<E> buff) {
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
	 * Tests if this buffer has no elements.
	 */
	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * Returns current index of inner <code>E</code> array chunk.
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
	 * Returns <code>E</code> inner array chunk at given index.
	 * May be used for iterating inner chunks in fast manner.
	 */
	public E[] array(int index) {
		return buffers[index];
	}

	/**
	 * Resets the buffer content.
	 */
	public void clear() {
		count = 0;
		offset = 0;
		currentBufferIndex = 0;
		currentBuffer = buffers[currentBufferIndex];
		buffersCount = 1;
	}

	/**
	 * Creates <code>E</code> array from buffered content.
	 */
	public E[] toArray() {
		int remaining = count;
		int pos = 0;
		E[] array = (E[]) new Object[count];
		for (E[] buf : buffers) {
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
     * Creates <code>E</code> subarray from buffered content.
     */
	public E[] toArray(int start, int len) {
		int remaining = len;
		int pos = 0;
		E[] array = (E[]) new Object[len];

		if (len == 0) {
			return array;
		}

		int i = 0;
		while (start >= buffers[i].length) {
			start -= buffers[i].length;
			i++;
		}

		while (i < buffersCount) {
			E[] buf = buffers[i];
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
	 * Returns <code>E</code> element at given index.
	 */
	public E get(int index) {
		if (index >= count) {
			throw new IndexOutOfBoundsException();
		}
		int ndx = 0;
        while (true) {
			E[] b = buffers[ndx];
			if (index < b.length) {
				return b[index];
			}
			ndx++;
			index -= b.length;
		}
	}

	// @@generated

	/**
	 * Adds element to buffer.
	 */
	public void add(E element) {
		append(element);
	}

	/**
	 * Returns an iterator over buffer elements.
	 */
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			int iteratorIndex;
			int iteratorBufferIndex;
			int iteratorOffset;

			public boolean hasNext() {
				return iteratorIndex < count;
			}

			public E next() {
				if (iteratorIndex >= count) {
					throw new NoSuchElementException();
				}
				E[] buf = buffers[iteratorBufferIndex];
				E result = buf[iteratorOffset];

				// increment
				iteratorIndex++;
				iteratorOffset++;
				if (iteratorOffset >= buf.length) {
					iteratorOffset = 0;
					iteratorBufferIndex++;
				}

				return result;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
