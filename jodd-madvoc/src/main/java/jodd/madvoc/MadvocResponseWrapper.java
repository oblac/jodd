// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

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
 *     <ol><code>text/html;charset="UTF-8"</code></ol>
 *     <ol><code>image/png"</code></ol>
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
	public MadvocResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	// ---------------------------------------------------------------- char encoding

	protected String mimeType;
	protected String characterEncoding;

	@Override
	public void setHeader(String name, String value) {
		if (name.toLowerCase().equals(CONTENT_TYPE)) {
			setContentType(value);
		} else {
			super.setHeader(name, value);
		}
	}

	@Override
	public void addHeader(String name, String value) {
		if (name.toLowerCase().equals(CONTENT_TYPE)) {
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
	public void setContentType(String type) {
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
	public void setCharacterEncoding(String charset) {
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