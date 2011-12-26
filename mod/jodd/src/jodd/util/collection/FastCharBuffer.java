package jodd.util.collection;

import java.util.List;
import java.util.ArrayList;

/**
 * Fast char buffer.
 */
public class FastCharBuffer {

	private List<char[]> buffers = new ArrayList<char[]>();
	private int currentBufferIndex;
	private int filledBufferSum;
	private char[] currentBuffer;
	private int count;

	/**
	 * Creates a new char buffer. The buffer capacity is
	 * initially 1024 bytes, though its size increases if necessary.
	 */
	public FastCharBuffer() {
		this(1024);
	}

	/**
	 * Creates a new char buffer, with a buffer capacity of
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
		if (currentBufferIndex < buffers.size() - 1) {
			// recycling old buffer
			filledBufferSum += currentBuffer.length;
			currentBufferIndex++;
			currentBuffer = buffers.get(currentBufferIndex);
		} else {
			// creating new buffer
			int newBufferSize;
			if (currentBuffer == null) {
				newBufferSize = newCount;
				filledBufferSum = 0;
			} else {
				newBufferSize = Math.max(
						currentBuffer.length << 1,
						newCount - filledBufferSum);
				filledBufferSum += currentBuffer.length;
			}

			currentBufferIndex++;
			currentBuffer = new char[newBufferSize];
			buffers.add(currentBuffer);
		}
	}

	/**
	 * Appends char array.
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
		int inBufferPos = count - filledBufferSum;
		while (remaining > 0) {
			int part = Math.min(remaining, currentBuffer.length - inBufferPos);
			System.arraycopy(b, end - remaining, currentBuffer, inBufferPos, part);
			remaining -= part;
			if (remaining > 0) {
				needNewBuffer(newCount);
				inBufferPos = 0;
			}
		}
		count = newCount;
		return this;
	}

	/**
	 * Appends char array.
	 */
	public FastCharBuffer append(char[] b) {
		return append(b, 0, b.length);
	}

	/**
	 * Appends single char.
	 */
	public FastCharBuffer append(char value) {
		int inBufferPos = count - filledBufferSum;

		if (inBufferPos == currentBuffer.length) {
			needNewBuffer(count + 1);
			inBufferPos = 0;
		}

		currentBuffer[inBufferPos] = value;
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
	 * Resets the buffer content.
	 */
	public void reset() {
		count = 0;
		filledBufferSum = 0;
		currentBufferIndex = 0;
		currentBuffer = buffers.get(currentBufferIndex);
	}

	/**
	 * Creates char array from the buffered content.
	 */
	public char[] toArray() {
		int remaining = count;
		int pos = 0;
		char[] newbuf = new char[count];
		for (char[] buf : buffers) {
			int c = Math.min(buf.length, remaining);
			System.arraycopy(buf, 0, newbuf, pos, c);
			pos += c;
			remaining -= c;
			if (remaining == 0) {
				break;
			}
		}
		return newbuf;
	}

}