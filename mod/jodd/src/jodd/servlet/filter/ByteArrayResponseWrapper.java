// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/*
 * ByteArray implementation of HttpServletResponseWrapper.
 */
public class ByteArrayResponseWrapper extends HttpServletResponseWrapper {

	private PrintWriter writer;
	private ByteArrayOutputStreamWrapper out;

	public ByteArrayResponseWrapper(ServletResponse response) throws IOException {
		super((HttpServletResponse) response);
		out = new ByteArrayOutputStreamWrapper(response.getOutputStream());
		writer = new PrintWriter(out);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return writer;
	}

	/**
	 * Get a String representation of the entire buffer.
	 */
	@Override
	public String toString() {
		return out.getByteArrayStream().toString();
	}

	/**
	 * Get the underlying character array.
	 */
	public char[] toCharArray() {
		return out.getByteArrayStream().toString().toCharArray();
	}

	/**
	 * Get the underlying byte array.
	 */
	public byte[] toByteArray() {
		return out.getByteArrayStream().toByteArray();
	}

	@Override
	public void reset() {
		out.reset();
	}
}

