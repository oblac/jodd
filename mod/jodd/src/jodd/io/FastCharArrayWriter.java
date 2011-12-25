// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;

/**
 * Similar as {@link jodd.io.FastByteArrayOutputStream} but for {@link Writer}.
 */
public class FastCharArrayWriter extends Writer {

	private List<char[]> buffers = new ArrayList<char[]>();
	private int currentBufferIndex;
	private int filledBufferSum;
	private char[] currentBuffer;
	private int count;

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
		if (size < 0) {
			throw new IllegalArgumentException("Negative initial size: " + size);
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
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] b, int off, int len) {
		int end = off + len;
		if ((off < 0)
				|| (off > b.length)
				|| (len < 0)
				|| (end > b.length)
				|| (end < 0)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return;
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
	}

	/**
	 * Writes single byte.
	 */
	@Override
	public void write(int b) {
		int inBufferPos = count - filledBufferSum;

		if (inBufferPos == currentBuffer.length) {
			needNewBuffer(count + 1);
			inBufferPos = 0;
		}

		currentBuffer[inBufferPos] = (char) b;
		count++;
	}

	@Override
	public void write(String s, int off, int len) {
		write(s.toCharArray(), off, len);
	}

	/**
	 * @see java.io.CharArrayWriter#size()
	 */
	public int size() {
		return count;
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
		count = 0;
		filledBufferSum = 0;
		currentBufferIndex = 0;
		currentBuffer = buffers.get(currentBufferIndex);
	}

	/**
	 * @see java.io.CharArrayWriter#writeTo(java.io.Writer)
	 */
	public void writeTo(Writer out) throws IOException {
		int remaining = count;
		for (int i = 0; i < buffers.size(); i++) {
			char[] buf = buffers.get(i);
			int c = Math.min(buf.length, remaining);
			out.write(buf, 0, c);
			remaining -= c;
			if (remaining == 0) {
				break;
			}
		}
	}

	/**
	 * @see java.io.CharArrayWriter#toCharArray()
	 */
	public char[] toCharArray() {
		int remaining = count;
		int pos = 0;
		char newbuf[] = new char[count];
		for (int i = 0; i < buffers.size(); i++) {
			char[] buf = buffers.get(i);
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

	/**
	 * @see java.io.CharArrayWriter#toString()
	 */
	@Override
	public String toString() {
		return new String(toCharArray());
	}
}
