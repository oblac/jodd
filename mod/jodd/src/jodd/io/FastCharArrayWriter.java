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

	private char[] getBuffer(int index) {
		return buffers.get(index);
	}

	private void needNewBuffer(int newcount) {
		if (currentBufferIndex < buffers.size() - 1) {
			//Recycling old buffer
			filledBufferSum += currentBuffer.length;

			currentBufferIndex++;
			currentBuffer = getBuffer(currentBufferIndex);
		} else {
			//Creating new buffer
			int newBufferSize;
			if (currentBuffer == null) {
				newBufferSize = newcount;
				filledBufferSum = 0;
			} else {
				newBufferSize = Math.max(
						currentBuffer.length << 1,
						newcount - filledBufferSum);
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
	public synchronized void write(char[] b, int off, int len) {
		if ((off < 0)
				|| (off > b.length)
				|| (len < 0)
				|| ((off + len) > b.length)
				|| ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		int newcount = count + len;
		int remaining = len;
		int inBufferPos = count - filledBufferSum;
		while (remaining > 0) {
			int part = Math.min(remaining, currentBuffer.length - inBufferPos);
			System.arraycopy(b, off + len - remaining, currentBuffer, inBufferPos, part);
			remaining -= part;
			if (remaining > 0) {
				needNewBuffer(newcount);
				inBufferPos = 0;
			}
		}
		count = newcount;
	}

	/**
	 * Calls the write(char[]) method.
	 *
	 * @see java.io.Writer#write(int)
	 */
	@Override
	public synchronized void write(int b) {
		write(new char[]{(char) b}, 0, 1);
	}

	@Override
	public synchronized void write(String s, int off, int len) {
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
	public synchronized void reset() {
		count = 0;
		filledBufferSum = 0;
		currentBufferIndex = 0;
		currentBuffer = getBuffer(currentBufferIndex);
	}

	/**
	 * @see java.io.CharArrayWriter#writeTo(java.io.Writer)
	 */
	public synchronized void writeTo(Writer out) throws IOException {
		int remaining = count;
		for (int i = 0; i < buffers.size(); i++) {
			char[] buf = getBuffer(i);
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
	public synchronized char[] toCharArray() {
		int remaining = count;
		int pos = 0;
		char newbuf[] = new char[count];
		for (int i = 0; i < buffers.size(); i++) {
			char[] buf = getBuffer(i);
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
