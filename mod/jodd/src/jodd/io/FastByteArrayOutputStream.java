// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;

/**
 * This class implements an output stream in which the data is
 * written into a byte array. The buffer automatically grows as data
 * is written to it.
 * <p>
 * The data can be retrieved using <code>toByteArray()</code> and
 * <code>toString()</code>.
 * <p>
 * Closing a <code>FastByteArrayOutputStream</code> has no effect. The methods in
 * this class can be called after the stream has been closed without
 * generating an <code>IOException</code>.
 * <p>
 * This is an alternative implementation of the java.io.FastByteArrayOutputStream
 * class. The original implementation only allocates 32 bytes at the beginning.
 * As this class is designed for heavy duty it starts at 1024 bytes. In contrast
 * to the original it doesn't reallocate the whole memory block but allocates
 * additional buffers. This way no buffers need to be garbage collected and
 * the contents don't have to be copied to the new buffer. This class is
 * designed to behave exactly like the original. The only exception is the
 * depreciated toString(int) method that has been ignored.
 *
 */
public class FastByteArrayOutputStream extends OutputStream {

	private List<byte[]> buffers = new ArrayList<byte[]>();
	private int currentBufferIndex;
	private int filledBufferSum;
	private byte[] currentBuffer;
	private int count;

	/**
	 * Creates a new byte array output stream. The buffer capacity is
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
	public FastByteArrayOutputStream(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Negative initial size: " + size);
		}
		needNewBuffer(size);
	}

	private byte[] getBuffer(int index) {
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
			currentBuffer = new byte[newBufferSize];
			buffers.add(currentBuffer);
		}
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public synchronized void write(byte[] b, int off, int len) {
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
	 * Calls the write(byte[]) method.
	 *
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public synchronized void write(int b) {
		write(new byte[]{(byte) b}, 0, 1);
	}

	/**
	 * @see java.io.ByteArrayOutputStream#size()
	 */
	public int size() {
		return count;
	}

	/**
	 * Closing a <code>FastByteArrayOutputStream</code> has no effect. The methods in
	 * this class can be called after the stream has been closed without
	 * generating an <code>IOException</code>.
	 */
	@Override
	public void close() {
		//nop
	}

	/**
	 * @see java.io.ByteArrayOutputStream#reset()
	 */
	public synchronized void reset() {
		count = 0;
		filledBufferSum = 0;
		currentBufferIndex = 0;
		currentBuffer = getBuffer(currentBufferIndex);
	}

	/**
	 * @see java.io.ByteArrayOutputStream#writeTo(OutputStream)
	 */
	public synchronized void writeTo(OutputStream out) throws IOException {
		int remaining = count;
		for (int i = 0; i < buffers.size(); i++) {
			byte[] buf = getBuffer(i);
			int c = Math.min(buf.length, remaining);
			out.write(buf, 0, c);
			remaining -= c;
			if (remaining == 0) {
				break;
			}
		}
	}

	/**
	 * @see java.io.ByteArrayOutputStream#toByteArray()
	 */
	public synchronized byte toByteArray()[] {
		int remaining = count;
		int pos = 0;
		byte newbuf[] = new byte[count];
		for (int i = 0; i < buffers.size(); i++) {
			byte[] buf = getBuffer(i);
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
	 * @see java.io.ByteArrayOutputStream#toString()
	 */
	@Override
	public String toString() {
		return new String(toByteArray());
	}

	/**
	 * @see java.io.ByteArrayOutputStream#toString(String)
	 */
	public String toString(String enc) throws UnsupportedEncodingException {
		return new String(toByteArray(), enc);
	}

}
