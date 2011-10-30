// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Buffered servlet response wrapper.
 */
public class BufferResponseWrapper extends HttpServletResponseWrapper {

	protected static final String CONTENT_TYPE = "content-type";
	protected static final String CONTENT_LENGTH = "content-length";
	protected static final String LAST_MODIFIED = "last-modified";

	protected final LastModifiedData lastModifiedData;

	protected Buffer buffer;

	public BufferResponseWrapper(final HttpServletResponse originalResponse, LastModifiedData lastModifiedData) {
		super(originalResponse);
		this.lastModifiedData = lastModifiedData;
		lastModifiedData.startNewResponse();
		enableBuffering();
	}

	/**
	 * Called just before stream or writer is accessed.
	 * After that point response is considered as committed
	 * and should not be modified.
	 */
	protected void preResponseCommit() {
	}

	// ---------------------------------------------------------------- enable/disable

	/**
	 * Enables buffering by transferring the output to the buffer.
	 */
	protected void enableBuffering() {
		if (buffer != null) {
			return;
		}
		buffer = new Buffer();
	}

	/**
	 * Disables buffering by transferring the output to original destinations.
	 */
	protected void disableBuffering() {
		if (buffer == null) {
			return;
		}
		buffer = null;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns <code>true</code> if buffering is enabled.
	 */
	public boolean isBufferingEnabled() {
		return buffer != null;
	}

	/**
	 * Returns <code>true</code> if underlying buffer was written to
	 * using {@link #getOutputStream()} (as opposed to {@link #getWriter()}.
	 * If buffering was not enabled at all, <code>false</code> will be returned,
	 * therefore an additional {@link #isBufferingEnabled() check} is required.
	 */
	public boolean isBufferStreamBased() {
		return buffer != null && buffer.isUsingStream();
	}


	/**
	 * Returns buffered writer if buffering is enabled,
	 * otherwise returns the original writer.
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		preResponseCommit();
		if (buffer == null) {
			return getResponse().getWriter();
		}
		return buffer.getWriter();
	}

	/**
	 * Returns buffered output stream if buffering is enabled,
	 * otherwise returns the original stream.
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		preResponseCommit();
		if (buffer == null) {
			return getResponse().getOutputStream();
		}
		return buffer.getOutputStream();
	}

	/**
	 * Returns buffered content or <code>null</code> if buffering was not enabled.
	 */
	public char[] getBufferedChars() {
		if (buffer == null) {
			return null;
		}
		return buffer.toCharArray();
	}

	/**
	 * Returns buffered bytes or <code>null</code> if buffering was not enabled.
	 */
	public byte[] getBufferedBytes() {
		if (buffer == null) {
			return null;
		}
		return buffer.toByteArray();
	}


	// ---------------------------------------------------------------- content type

	/**
	 * Determines if some content type has to be buffered. By default returns <code>true</code>.
	 * @param contentType full content-type, e.g. "text/html; charset=utf-8"
	 * @param mimeType extracted mime-type, e.g. "text/html"
	 * @param encoding extracted encoding, e.g. "utf-8" (may be <code>null</code>)
	 */
	protected boolean bufferContentType(String contentType, String mimeType, String encoding) {
		return true;
	}

	/**
	 * Sets the content type and enables or disables buffering.
	 */
	@Override
	public void setContentType(String type) {
		super.setContentType(type);

		ContentTypeHeaderResolver resolver = new ContentTypeHeaderResolver(type);
		if (bufferContentType(type, resolver.getType(), resolver.getEncoding())) {
			enableBuffering();
		} else {
			disableBuffering();
		}
	}

	/**
	 * Prevents flushing buffer if buffering enabled.
	 */
	@Override
	public void flushBuffer() throws IOException {
		if (buffer == null) {
			super.flushBuffer();
		}
	}

	// ---------------------------------------------------------------- content headers

	/**
	 * Prevents content-length being set if buffering enabled.
	 */
	@Override
	public void setContentLength(int contentLength) {
		if (buffer == null) {
			super.setContentLength(contentLength);
		}
	}

	/**
	 * Prevents setting content-length fif buffering enabled.
	 */
	@Override
	public void setHeader(String name, String value) {
		String lowerName = name.toLowerCase();
		if (lowerName.equals(CONTENT_TYPE)) {
			setContentType(value);
		} else if (buffer == null || !lowerName.equals(CONTENT_LENGTH)) {
			super.setHeader(name, value);
		}
	}

	/**
	 * Prevents setting content-length if buffering enabled.
	 */
	@Override
	public void addHeader(String name, String value) {
		if (name.toLowerCase().equals(CONTENT_TYPE)) {
			setContentType(value);
		} else if (buffer == null || !name.toLowerCase().equals(CONTENT_LENGTH)) {
			super.addHeader(name, value);
		}
	}

	/**
	 * Prevents setting content-length if buffering enabled.
	 */
	@Override
	public void setIntHeader(String name, int value) {
		if (buffer == null || !name.toLowerCase().equals(CONTENT_LENGTH)) {
			super.setIntHeader(name, value);
		}
	}

	/**
	 * Prevents setting content-length if buffering enabled.
	 */
	@Override
	public void addIntHeader(String name, int value) {
		if (buffer == null || !name.toLowerCase().equals(CONTENT_LENGTH)) {
			super.addIntHeader(name, value);
		}
	}

	// ---------------------------------------------------------------- date headers

	@Override
	public void setDateHeader(String name, long value) {
		if (name.toLowerCase().equals(LAST_MODIFIED)) {
			lastModifiedData.updateLastModified(value);
		} else {
			super.setDateHeader(name, value);
		}
	}

	@Override
	public void addDateHeader(String name, long value) {
		if (name.toLowerCase().equals(LAST_MODIFIED)) {
			lastModifiedData.updateLastModified(value);
		} else {
			super.addDateHeader(name, value);
		}
	}

	// ---------------------------------------------------------------- status

	@Override
	public void setStatus(int statusCode) {
		stopBufferingForStatusCode(statusCode);
		super.setStatus(statusCode);
	}

	@Override
	public void setStatus(int statusCode, String reason) {
		stopBufferingForStatusCode(statusCode);
		super.setStatus(statusCode);
	}

	@Override
	public void sendError(int statusCode) throws IOException {
		stopBufferingForStatusCode(statusCode);
		super.sendError(statusCode);
	}

	@Override
	public void sendError(int statusCode, String reason) throws IOException {
		stopBufferingForStatusCode(statusCode);
		super.sendError(statusCode, reason);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		stopBufferingForStatusCode(HttpServletResponse.SC_TEMPORARY_REDIRECT);
		super.sendRedirect(location);
	}

	protected void stopBufferingForStatusCode(int statusCode) {
		if (bufferStatusCode(statusCode) == false) {
			disableBuffering();
		}
	}

	/**
	 * Determines if buffering should be used for some HTTP status code.
	 * By default returns <code>true</code> only for status code <b>200</b>.
	 */
	protected boolean bufferStatusCode(int statusCode) {
		return statusCode == 200;
	}

}