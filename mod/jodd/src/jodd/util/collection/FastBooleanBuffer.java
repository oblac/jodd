package jodd.util.collection;

import java.util.List;
import java.util.ArrayList;

/**
 * Fast boolean buffer.
 */
public class FastBooleanBuffer {

	private List<boolean[]> buffers = new ArrayList<boolean[]>();
	private int currentBufferIndex = -1;
	private boolean[] currentBuffer;
	private int offset;
	private int count;

	/**
	 * Creates a new boolean buffer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastBooleanBuffer() {
		this(1024);
	}

	/**
	 * Creates a new boolean buffer, with a buffer capacity of
	 * the specified size, in bytes.
	 *
	 * @param size the initial size.
	 * @throws IllegalArgumentException if size is negative.
	 */
	public FastBooleanBuffer(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Invalid size: " + size);
		}
		needNewBuffer(size);
	}

	private void needNewBuffer(int newCount) {
		if (currentBufferIndex < buffers.size() - 1) {	// recycling old buffer
			offset = 0;
			currentBufferIndex++;
			currentBuffer = buffers.get(currentBufferIndex);
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
			currentBuffer = new boolean[newBufferSize];
			offset = 0;
			buffers.add(currentBuffer);
		}
	}

	/**
	 * Appends boolean array.
	 */
	public FastBooleanBuffer append(boolean[] b, int off, int len) {
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
	 * Appends boolean array.
	 */
	public FastBooleanBuffer append(boolean[] b) {
		return append(b, 0, b.length);
	}

	/**
	 * Appends single boolean.
	 */
	public FastBooleanBuffer append(boolean value) {
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
	 * Returns current index of boolean array.
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
	 * Returns boolean chunk at given index.
	 */
	public boolean[] array(int index) {
		return buffers.get(index);
	}

	/**
	 * Resets the buffer content.
	 */
	public void reset() {
		count = 0;
		offset = 0;
		currentBufferIndex = 0;
		currentBuffer = buffers.get(currentBufferIndex);
	}

	/**
	 * Creates boolean array from the buffered content.
	 */
	public boolean[] toArray() {
		int remaining = count;
		int pos = 0;
		boolean[] array = new boolean[count];
		for (boolean[] buf : buffers) {
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