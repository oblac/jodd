// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.wrapper;

import jodd.io.FastCharArrayWriter;
import jodd.servlet.filter.FastByteArrayServletOutputStream;

import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;

/**
 * Facade for both <code>PrintWriter</code> and <code>ServletOutputStream</code> of servlet response.
 */
public class Buffer {

	protected FastCharArrayWriter bufferedWriter;
	protected FastByteArrayServletOutputStream bufferOutputStream;

	protected PrintWriter outWriter;
	protected ServletOutputStream outStream;

	/**
	 * Returns a writer.
	 */
	public PrintWriter getWriter() {
		if (outWriter == null) {
			if (outStream != null) {
				throw new IllegalStateException("Can't call getWriter() after getOutputStream()");
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
				throw new IllegalStateException("Can't call getOutputStream() after getWriter()");
			}
			bufferOutputStream = new FastByteArrayServletOutputStream();
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
	 * Returns <code>null</code> if writer is not used.
	 */
	public char[] toCharArray() {
		if (bufferedWriter != null) {
			return bufferedWriter.toCharArray();
		}
		return null;
	}

	/**
	 * Returns buffered servlet output content as byte array.
	 * Returns <code>null</code> if writer is not used.
	 */
	public byte[] toByteArray() {
		if (bufferOutputStream != null) {
			return bufferOutputStream.getByteArrayStream().toByteArray();
		}
		return null;
	}

}