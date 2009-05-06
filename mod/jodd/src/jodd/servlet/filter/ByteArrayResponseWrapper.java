// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.filter;

import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/*
 * ByteArray implementation of HttpServletResponseWrapper.
 */
public class ByteArrayResponseWrapper extends HttpServletResponseWrapper {

	private PrintWriter tpWriter;
	private ByteArrayOutputStreamWrapper tpStream;

	public ByteArrayResponseWrapper(ServletResponse inResp) throws java.io.IOException {
		super((HttpServletResponse) inResp);
		tpStream = new ByteArrayOutputStreamWrapper(inResp.getOutputStream());
		tpWriter = new PrintWriter(tpStream);
	}

	@Override
	public ServletOutputStream getOutputStream() throws java.io.IOException {
		return tpStream;
	}

	@Override
	public PrintWriter getWriter() throws java.io.IOException {
		return tpWriter;
	}

	/**
	 * Get a String representation of the entire buffer.
	 */
	@Override
	public String toString() {
		return tpStream.getByteArrayStream().toString();
	}

	/**
	 * Get the underlying character array.
	 */
	public char[] toCharArray() {
		return tpStream.getByteArrayStream().toString().toCharArray();
	}

	/**
	 * Get the underlying byte array.
	 */
	public byte[] toByteArray() {
		return tpStream.getByteArrayStream().toByteArray();
	}

	@Override
	public void reset() {
		tpStream.reset();
	}
}

