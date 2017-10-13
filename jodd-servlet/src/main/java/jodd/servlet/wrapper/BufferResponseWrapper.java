// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.servlet.wrapper;

import jodd.util.CharUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Buffered servlet response wrapper.
 */
public class BufferResponseWrapper extends HttpServletResponseWrapper {

	protected static final String CONTENT_TYPE = "content-type";
	protected static final String CONTENT_LENGTH = "content-length";
	protected static final String LAST_MODIFIED = "last-modified";

	protected final LastModifiedData lastModifiedData;

	protected Buffer buffer;

	public BufferResponseWrapper(HttpServletResponse originalResponse) {
		this(originalResponse, new LastModifiedData());
	}

	public BufferResponseWrapper(HttpServletResponse originalResponse, LastModifiedData lastModifiedData) {
		super(originalResponse);
		this.lastModifiedData = lastModifiedData;
		lastModifiedData.startNewResponse();
		enableBuffering();
	}


	// ---------------------------------------------------------------- commit

	/**
	 * Called just before stream or writer is accessed.
	 * After that point response is considered as committed
	 * and should not be modified.
	 */
	protected void preResponseCommit() {
	}

	/**
	 * Commits a response if not already committed. Invokes
	 * {@link #preResponseCommit()} if response is going to
	 * be committed.
	 */
	public void commitResponse() {
		if (!getResponse().isCommitted()) {
			preResponseCommit();
		}
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
	 * Returns last modified data.
	 */
	public LastModifiedData getLastModifiedData() {
		return lastModifiedData;
	}

	// ---------------------------------------------------------------- content

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

	/**
	 * Returns buffered content as chars, no matter if stream or writer is used.
	 * Returns <code>null</code> if buffering was not enabled.
	 */
	public char[] getBufferContentAsChars() {
		if (buffer == null) {
			return null;
		}

		if (!buffer.isUsingStream()) {
			return buffer.toCharArray();
		}

		byte[] content = buffer.toByteArray();
		String encoding = getContentTypeEncoding();

		try {
			if (encoding == null) {
				// assume default encoding
				return CharUtil.toCharArray(content);
			} else {
				return CharUtil.toCharArray(content, encoding);
			}
		} catch (UnsupportedEncodingException ueex) {
			throw new IllegalArgumentException(ueex);
		}
	}

	/**
	 * Returns buffered content as bytes, no matter if stream or writer is used.
	 * Returns <code>null</code> if buffering was not enabled.
	 */
	public byte[] getBufferContentAsBytes() {
		if (buffer == null) {
			return null;
		}

		if (buffer.isUsingStream()) {
			return buffer.toByteArray();
		}

		char[] content = buffer.toCharArray();
		String encoding = getContentTypeEncoding();

		try {
			if (encoding == null) {
				// assume default encoding
				return CharUtil.toByteArray(content);
			} else {
				return CharUtil.toByteArray(content, encoding);
			}
		} catch (UnsupportedEncodingException ueex) {
			throw new IllegalArgumentException(ueex);
		}
	}

	/**
	 * Writes content to original output stream, using either output stream or writer, depending
	 * on how the content was buffered. It is assumed that provided content is a modified
	 * wrapped content.
	 */
	public void writeContentToResponse(char[] content) throws IOException {
		if (buffer == null) {
			return;
		}
		if (buffer.isUsingStream()) {
			ServletOutputStream outputStream = getResponse().getOutputStream();

			String encoding = getContentTypeEncoding();
			if (encoding == null) {
				outputStream.write(CharUtil.toByteArray(content));
			} else {
				outputStream.write(CharUtil.toByteArray(content, encoding));
			}

			outputStream.flush();
		} else {
			Writer out = getResponse().getWriter();
			out.write(content);
			out.flush();
		}
	}

	/**
	 * Writes (unmodified) buffered content, using either output stream or writer.
	 * May be used for writing the unmodified response. Of course, you may
	 * {@link #print(String) modify} buffered data by altering the buffer content.
	 */
	public void writeContentToResponse() throws IOException {
		if (buffer == null) {
			return;
		}
		if (buffer.isUsingStream()) {
			ServletOutputStream outputStream = getResponse().getOutputStream();
			outputStream.write(buffer.toByteArray());
			outputStream.flush();
		} else {
			Writer out = getResponse().getWriter();
			out.write(buffer.toCharArray());
			out.flush();
		}
	}


	// ---------------------------------------------------------------- content type

	protected ContentTypeHeaderResolver contentTypeResolver;

	/**
	 * Returns content encoding or <code>null</code>.
	 */
	public String getContentTypeEncoding() {
		if (contentTypeResolver == null) {
			return null;
		}
		return contentTypeResolver.getEncoding();
	}

	/**
	 * Returns content mime type or <code>null</code>.
	 */
	public String getContentMimeType() {
		if (contentTypeResolver == null) {
			return null;
		}
		return contentTypeResolver.getMimeType();
	}

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

		contentTypeResolver = new ContentTypeHeaderResolver(type);

		if (bufferContentType(type, contentTypeResolver.getMimeType(), contentTypeResolver.getEncoding())) {
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
	 * Prevents setting content-length if buffering enabled.
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
		String lowerName = name.toLowerCase();

		if (lowerName.equals(CONTENT_TYPE)) {
			setContentType(value);
		} else if (buffer == null || !lowerName.equals(CONTENT_LENGTH)) {
			super.addHeader(name, value);
		}
	}

	/**
	 * Prevents setting content-length if buffering enabled.
	 */
	@Override
	public void setIntHeader(String name, int value) {
		if (buffer == null || !name.equalsIgnoreCase(CONTENT_LENGTH)) {
			super.setIntHeader(name, value);
		}
	}

	/**
	 * Prevents setting content-length if buffering enabled.
	 */
	@Override
	public void addIntHeader(String name, int value) {
		if (buffer == null || !name.equalsIgnoreCase(CONTENT_LENGTH)) {
			super.addIntHeader(name, value);
		}
	}

	// ---------------------------------------------------------------- date headers

	@Override
	public void setDateHeader(String name, long value) {
		if (name.equalsIgnoreCase(LAST_MODIFIED)) {
			lastModifiedData.updateLastModified(value);
		} else {
			super.setDateHeader(name, value);
		}
	}

	@Override
	public void addDateHeader(String name, long value) {
		if (name.equalsIgnoreCase(LAST_MODIFIED)) {
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
		if (!bufferStatusCode(statusCode)) {
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

	// ---------------------------------------------------------------- alter buffer

	/**
	 * Appends string to the buffer.
	 */
	public void print(String string) throws IOException {
		if (isBufferStreamBased()) {
			String encoding = getContentTypeEncoding();
			byte[] bytes;

			if (encoding == null) {
				bytes = string.getBytes();
			} else {
				bytes = string.getBytes(encoding);
			}

			buffer.getOutputStream().write(bytes);
			return;
		}

		// make sure at least writer is initialized
		buffer.getWriter().write(string);
	}
}