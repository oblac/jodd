// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.io.FastCharArrayWriter;
import jodd.JoddDefault;

/**
 * A response wrapper that takes everything the client would normally output
 * and saves it in one big character array.
 */
public class CharArrayResponseWrapper extends HttpServletResponseWrapper {

	protected final FastCharArrayWriter writer;
	protected final String encoding;

	/**
	 * Initializes wrapper.

	 * First, this constructor calls the parent constructor. That call is crucial
	 * so that the response is stored and thus setHeader, setStatus, addCookie,
	 * and so forth work normally.

	 * Second, this constructor creates a CharArrayWriter that will be used to
	 * accumulate the response.
	 */
	public CharArrayResponseWrapper(ServletResponse response) {
		this(response, JoddDefault.encoding);
	}

	public CharArrayResponseWrapper(ServletResponse response, String encoding) {
		super((HttpServletResponse) response);
		writer = new FastCharArrayWriter();
		this.encoding = encoding;
	}

	/**
	 * When servlets or JSP pages ask for the Writer, don't give them the real
	 * one. Instead, give them a version that writes into the character array.
	 * The filter needs to send the contents of the array to the client (perhaps
	 * after modifying it).
	 */
	@Override
	public PrintWriter getWriter() {
		return new PrintWriter(writer);
	}

	/**
	 * Get a String representation of the entire buffer.
	 *
	 * Be sure <b>not</b> to call this method multiple times on the same wrapper.
	 * The API for CharArrayWriter does not guarantee that it "remembers" the
	 * previous value, so the call is likely to make a new String every time.
	 */
	@Override
	public String toString() {
		return writer.toString();
	}

	/**
	 * Get the underlying character array.
	 */
	public char[] toCharArray() {
		return writer.toCharArray();
	}

	/**
	 * Get the underlying byte array.
	 */
	public byte[] toByteArray() throws IOException {
		return CharUtil.toByteArray(writer.toCharArray(), encoding);
	}


	/**
	 * This empty method <b>must</b> exist.
	 */
	@Override
	public void setContentLength(int len) {
		super.setContentLength(len);
	}

	/**
	 * Returns the size (number of characters) of written data.
	 */
	public int getSize() {
		return writer.size();
	}

	private String contentType = StringPool.EMPTY;

	/**
	 * Sets the content type.
	 */
	@Override
	public void setContentType(String type) {
		super.setContentType(type);
		contentType = type;
	}

	/**
	 * Returns content type.
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	public void close() {
		writer.close();
	}

	@Override
	public void reset() {
		writer.reset();
	}

	/**
	 * Returns output stream.
	 */
	@Override
	public ServletOutputStream getOutputStream() {

		return new ServletOutputStream() {
			@Override
			public void write(int b) {
				writer.write(b);
			}

			@Override
			public void write(byte b[]) throws IOException {
				writer.write(CharUtil.toCharArray(b, encoding), 0, b.length);
			}

			@Override
			public void write(byte b[], int off, int len) throws IOException {
				writer.write(CharUtil.toCharArray(b, encoding), off, len);
			}
		};
	}

}
