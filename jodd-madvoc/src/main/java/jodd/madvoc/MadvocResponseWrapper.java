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

package jodd.madvoc;

import jodd.servlet.wrapper.ContentTypeHeaderResolver;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Madvoc HTTP Response wrapper. It does the following:
 * <ul>
 * <li>Delays setting of response encoding until response is being used,
 * allowing resetting the charset.</li>
 * </ul>
 * This unifies the behavior of servlet containers. Method <code>setContentType</code>
 * may be called more times, and its on servlet container how to deal with character encodings.
 * For example, if user set content type with following values:
 * <ul>
 *     <li>text/html;charset="UTF-8"</li>
 *     <li>image/png</li>
 * </ul>
 * the question is would the second call reset the character or not. Tomcat 6-7, for example,
 * does not reset the charset on content type change. In our opinion this is an error
 * since new content type means new charset, as well.
 */
public class MadvocResponseWrapper extends HttpServletResponseWrapper {

	protected static final String CONTENT_TYPE = "content-type";

	/**
	 * Constructs a response adaptor wrapping the given response.
	 */
	public MadvocResponseWrapper(final HttpServletResponse response) {
		super(response);
	}

	// ---------------------------------------------------------------- char encoding

	protected String mimeType;
	protected String characterEncoding;

	@Override
	public void setHeader(final String name, final String value) {
		if (name.equalsIgnoreCase(CONTENT_TYPE)) {
			setContentType(value);
		} else {
			super.setHeader(name, value);
		}
	}

	@Override
	public void addHeader(final String name, final String value) {
		if (name.equalsIgnoreCase(CONTENT_TYPE)) {
			setContentType(value);
		} else {
			super.addHeader(name, value);
		}
	}

	/**
	 * Sets content type. If charset is missing, current value is reset.
	 * If passed value is <code>null</code>, content type will be reset
	 * as never set.
	 */
	@Override
	public void setContentType(final String type) {
		if (type == null) {
			mimeType = null;
			characterEncoding = null;
			return;
		}

		ContentTypeHeaderResolver contentTypeResolver = new ContentTypeHeaderResolver(type);

		mimeType = contentTypeResolver.getMimeType();
		characterEncoding = contentTypeResolver.getEncoding();
	}

	/**
	 * Sets just character encoding. Setting to <code>null</code> resets it.
	 */
	@Override
	public void setCharacterEncoding(final String charset) {
		characterEncoding = charset;
	}

	/**
	 * Applies content type to the response.
	 * Called before output stream or a writer are used.
	 */
	private void applyContentType() {
		if (mimeType != null) {
			super.setContentType(mimeType);

			if (characterEncoding != null) {
				super.setCharacterEncoding(characterEncoding);
			}
		}
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns content type and, optionally, charset. Returns <code>null</code>
	 * when mime type is not set.
	 */
	@Override
	public String getContentType() {
		String contentType = mimeType;

		if (mimeType != null && characterEncoding != null) {
			contentType += ";charset=" + characterEncoding;
		}
		return contentType;
	}

	/**
	 * Returns character encoding or <code>null</code> if not set.
	 */
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	// ---------------------------------------------------------------- out

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		applyContentType();
		return super.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		applyContentType();
		return super.getWriter();
	}

}