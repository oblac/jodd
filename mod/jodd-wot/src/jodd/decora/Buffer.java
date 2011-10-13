// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import jodd.io.FastCharArrayWriter;
import jodd.servlet.filter.ByteArrayOutputStreamWrapper;

import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;

/**
 * Facade for both response <code>PrintWriter</code> and <code>ServletOutputStream</code>.
 */
public class Buffer {

	private static final char[] EMPTY_CHAR_BUFFER = new char[] {};
	private static final byte[] EMPTY_BYTE_BUFFER = new byte[] {};

	protected FastCharArrayWriter bufferedWriter;
	protected ByteArrayOutputStreamWrapper bufferOutputStream;

	protected PrintWriter outWriter;
	protected ServletOutputStream outStream;

	/**
	 * Returns a writer.
	 */
	public PrintWriter getWriter() {
		if (outWriter == null) {
			if (outStream != null) {
				throw new DecoraException("Can't call response.getWriter() after response.getOutputStream()");
			}
			bufferedWriter = new FastCharArrayWriter();
			outWriter = new PrintWriter(bufferedWriter);
		}
		return outWriter;
	}

	/**
	 * Returns a servlet output stream.
	 */
	public ServletOutputStream getOutputStream() {
		if (outStream == null) {
			if (outWriter != null) {
				throw new IllegalStateException("Can't call response.getOutputStream() after response.getWriter()");
			}
			bufferOutputStream = new ByteArrayOutputStreamWrapper();
			outStream = bufferOutputStream;
		}
		return outStream;
	}

	/**
	 * Returns <code>true</code> if streams are using.
	 */
	public boolean isUsingStream() {
		return outStream != null;
	}

	/**
	 * Returns buffered writer content as char array.
	 */
	public char[] toCharArray() {
		if (bufferedWriter != null) {
			return bufferedWriter.toCharArray();
		}
		return EMPTY_CHAR_BUFFER;
	}

	/**
	 * Returns buffered servlet output content as byte array.
	 */
	public byte[] toByteArray() {
		if (bufferOutputStream != null) {
			return bufferOutputStream.getByteArrayStream().toByteArray();
		}
		return EMPTY_BYTE_BUFFER;
	}

}