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

package jodd.servlet.filter;

import jodd.io.IOUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Implementation of <b>HttpServletResponseWrapper</b> that works with
 * the {@link GzipResponseStream} implementation.
 */
public class GzipResponseWrapper extends HttpServletResponseWrapper {

	/**
	 * Calls the parent constructor which creates a ServletResponse adaptor
	 * wrapping the given response object.
	 */
	public GzipResponseWrapper(final HttpServletResponse response) {
		super(response);
		origResponse = response;

		// explicitly reset content length, as the size of zipped stream is unknown
		response.setContentLength(-1);
	}

	/**
	 * Original response.
	 */
	protected HttpServletResponse origResponse;

	/**
	 * The ServletOutputStream that has been returned by
	 * <code>getOutputStream()</code>, if any.
	 */
	protected ServletOutputStream stream;

	/**
	 * The PrintWriter that has been returned by <code>getWriter()</code>, if any.
	 */
	protected PrintWriter writer;

	/**
	 * The threshold number to compress.
	 */
	protected int threshold;

	/**
	 * Content type.
	 */
	protected String contentType;

	// ---------------------------------------------------------------- public
	
	/**
	 * Set content type
	 */
	@Override
	public void setContentType(final String contentType) {
		this.contentType = contentType;
		origResponse.setContentType(contentType);
	}

	/**
	 * Set threshold number
	 */
	public void setCompressionThreshold(final int threshold) {
		this.threshold = threshold;
	}

	/**
	 * Creates and returns a ServletOutputStream to write the content associated
	 * with this Response.
	 */
	public ServletOutputStream createOutputStream() throws IOException {
		final GzipResponseStream gzstream = new GzipResponseStream(origResponse);
		gzstream.setBuffer(threshold);
		return gzstream;
	}

	/**
	 * Finishes a response.
	 */
	public void finishResponse() {
		IOUtil.close(writer);
		IOUtil.close(stream);
	}

	// ---------------------------------------------------------------- ServletResponse

	/**
	 * Flushes the buffer and commit this response.
	 */
	@Override
	public void flushBuffer() throws IOException {
		if (stream != null) {
			stream.flush();
		}
	}

	/**
	 * Returns the servlet output stream associated with this Response.
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {

		if (writer != null) {
			throw new IllegalStateException("getWriter() has already been called for this response");
		}
		if (stream == null) {
			stream = createOutputStream();
		}
		return(stream);
	}

	/**
	 * Returns the writer associated with this Response.
	 */
	@Override
	public PrintWriter getWriter() throws IOException {

		if (writer != null) {
			return writer;
		}

		if (stream != null) {
			throw new IllegalStateException("getOutputStream() has already been called for this response");
		}

		stream = createOutputStream();
		
		final String charEnc = origResponse.getCharacterEncoding();
		if (charEnc != null) {
			writer = new PrintWriter(new OutputStreamWriter(stream, charEnc));
		} else {
			writer = new PrintWriter(stream);
		}
		return(writer);
	}

	/**
	 * Ignores set content length on zipped stream.
	 */
	@Override
	public void setContentLength(final int length) {
	}

	/**
	 * Servlets v3.1 introduce this method, so we need to have it here
	 * in case they are used.
	 * See: https://github.com/oblac/jodd/issues/189
	 */
	@Override
	public void setContentLengthLong(final long length) {
	}

}
