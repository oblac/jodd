// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.util.buffer.FastByteBuffer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

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

	private final FastByteBuffer buffer;

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
		buffer = new FastByteBuffer(size);
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) {
		buffer.append(b, off, len);
	}

	/**
	 * Writes single byte.
	 */
	@Override
	public void write(int b) {
		buffer.append((byte) b);
	}

	/**
	 * @see java.io.ByteArrayOutputStream#size()
	 */
	public int size() {
		return buffer.size();
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
	public void reset() {
		buffer.clear();
	}

	/**
	 * @see java.io.ByteArrayOutputStream#writeTo(OutputStream)
	 */
	public void writeTo(OutputStream out) throws IOException {
		int index = buffer.index();
		for (int i = 0; i < index; i++) {
			byte[] buf = buffer.array(i);
			out.write(buf);
		}
		out.write(buffer.array(index), 0, buffer.offset());
	}

	/**
	 * @see java.io.ByteArrayOutputStream#toByteArray()
	 */
	public byte[] toByteArray() {
		return buffer.toArray();
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