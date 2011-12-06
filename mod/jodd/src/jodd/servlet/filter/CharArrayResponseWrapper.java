// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import jodd.io.FastCharArrayWriter;
import jodd.util.StringPool;

/**
 * Response wrapper that takes everything the client would normally output
 * and saves it in character array. It works only for writers, byte stream
 * is not buffered.
 */
public class CharArrayResponseWrapper extends HttpServletResponseWrapper {

	protected FastCharArrayWriter writer;
	protected PrintWriter printWriter;

	/**
	 * Initializes wrapper by creating {@link FastCharArrayWriter} that will
	 * be used to accumulate the response.
	 */
	public CharArrayResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	/**
	 * Returns buffered writer. Buffer will be created if not already used.
	 */
	@Override
	public PrintWriter getWriter() {
		if (writer == null) {
			writer = new FastCharArrayWriter();
			printWriter = new PrintWriter(writer);
		}
		return printWriter;
	}

	/**
	 * Get a String representation of the entire buffer.
	 * <p>
	 * <b>Do not</b> call this method multiple times on the same wrapper as
	 * new string is created every time.
	 */
	@Override
	public String toString() {
		if (writer == null) {
			return StringPool.EMPTY;
		}
		return writer.toString();
	}

	@Override
	public void reset() {
		if (writer != null) {
			writer.reset();
		}
	}

	// ---------------------------------------------------------------- add-on

	/**
	 * Explicitly closes the writer.
	 */
	public void close() {
		if (writer != null) {
			writer.close();
		}
	}

	/**
	 * Returns the size (number of characters) of written data.
	 */
	public int size() {
		if (writer == null) {
			return 0;
		}
		return writer.size();
	}

	/**
	 * Get the underlying character array or <code>null</code> if
	 * writer not used.
	 */
	public char[] toCharArray() {
		if (writer == null) {
			return null;
		}
		return writer.toCharArray();
	}

}
