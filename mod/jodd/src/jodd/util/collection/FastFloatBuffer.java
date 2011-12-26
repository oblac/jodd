// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

/**
 * Fast, fast float buffer.
 */
public class FastFloatBuffer {

	private float[][] buffers = new float[16][];
	private int buffersCount = 0;
	private int currentBufferIndex = -1;
	private float[] currentBuffer;
	private int offset;
	private int count;

	/**
	 * Creates a new float buffer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastFloatBuffer() {
		this(1024);
	}

	/**
	 * Creates a new float buffer, with a buffer capacity of
	 * the specified size, in bytes.
	 *
	 * @param size the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public FastFloatBuffer(int size) {
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
			currentBuffer = new float[newBufferSize];
			offset = 0;

			// add buffer
			if (currentBufferIndex >= buffers.length) {
				int newLen = buffers.length << 1;
				float[][] newBuffers = new float[newLen][];
                System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
                buffers = newBuffers;
			}
			buffers[currentBufferIndex] = currentBuffer;
			buffersCount++;
		}
	}

	/**
	 * Appends float array.
	 */
	public FastFloatBuffer append(float[] b, int off, int len) {
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
	 * Appends float array.
	 */
	public FastFloatBuffer append(float[] b) {
		return append(b, 0, b.length);
	}

	/**
	 * Appends single float.
	 */
	public FastFloatBuffer append(float value) {
		if (offset == currentBuffer.length) {
			needNewBuffer(count + 1);
		}

		currentBuffer[offset] = value;
		offset++;
		count++;

		return this;
	}

	/**
	 * Returns buffer size.
	 */
	public int size() {
		return count;
	}

	/**
	 * Returns current index of float array.
	 */
	public int index() {
		return currentBufferIndex;
	}

	/**
	 * Returns offset in current array buffer.
	 */
	public int offset() {
		return offset;
	}

	/**
	 * Returns float chunk at given index.
	 */
	public float[] array(int index) {
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
	 * Creates float array from the buffered content.
	 */
	public float[] toArray() {
		int remaining = count;
		int pos = 0;
		float[] array = new float[count];
		for (float[] buf : buffers) {
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

}