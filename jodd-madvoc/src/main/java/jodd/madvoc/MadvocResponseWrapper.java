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
	 * Caches content type for later. If passed value is <code>null</code>
	 * content type will be reset as never set.
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
	 * Caches character encoding charset for later. If passed value
	 * is <code>null</code> encoding will be reset as never set.
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