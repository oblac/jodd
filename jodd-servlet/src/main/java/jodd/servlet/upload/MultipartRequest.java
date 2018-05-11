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

package jodd.servlet.upload;

import jodd.core.JoddCore;
import jodd.io.upload.FileUploadFactory;
import jodd.io.upload.MultipartStreamParser;
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Handles multi-part requests and extract uploaded files and parameters from
 * it. Multi-part forms should be defined as:
 * <p>
 *
 * <code>
 * &lt;form method="post" enctype="multipart/form-data" accept-charset="<i>charset</i>"...
 * </code>
 *
 * <p>
 * "accept-charset" may be used in case when jsp page uses specific
 * encoding. If default encoding is used, this attribute is not required.
 *
 * <p>
 * MultipleRequest class may be created in two ways:<br>
 * 1) with the constructors, when user must prevent instantiating more than once;<br>
 * 2) using static factory methods, which always return valid MultipleRequest instance.
 *
 * <p>
 * This class loads complete request. To prevent big uploads (and potential
 * DoS attacks) check content length <b>before</b> loading.
 */
public class MultipartRequest extends MultipartStreamParser {

	// ---------------------------------------------------------------- properties

	private HttpServletRequest request;
	private String characterEncoding;

	/**
	 * Returns actual http servlet request instance.
	 */
	public HttpServletRequest getServletRequest() {
		return request;
	}

	/**
	 * Returns request content length. Usually used before loading, to check the upload size.
	 */
	public int getContentLength() {
		return request.getContentLength();
	}

	/**
	 * Returns current encoding.
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	// ---------------------------------------------------------------- constructors

	/**
	 * Creates new multi-part request with form encoding and file upload factory.
	 * After construction stream is <b>not</b> yet parsed! Use {@link #parseMultipartRequest()} or
	 * {@link #parseRequest()} to parse before further usage.
	 *
	 * <p>
	 * If not specified, character encoding is read from the request. If not specified there,
	 * default Jodd encoding is used.
	 *
	 * <p>
	 * Multiple instantiation doesn't work, since input stream can be parsed just once.
	 * Still, if it is needed, use {@link #getInstance(javax.servlet.http.HttpServletRequest, FileUploadFactory, String)}
	 * instead.
	 *
	 * @param request	http request
	 * @param encoding	form encoding or <code>null</code>
	 * @param fileUploadFactory	file factory, or <code>null</code> for default factory
	 */
	public MultipartRequest(final HttpServletRequest request, final FileUploadFactory fileUploadFactory, final String encoding) {
		super(fileUploadFactory);
		this.request = request;
		if (encoding != null) {
			this.characterEncoding = encoding;
		} else {
			this.characterEncoding = request.getCharacterEncoding();
		}
		if (this.characterEncoding == null) {
			this.characterEncoding = JoddCore.encoding;
		}
	}

	// ---------------------------------------------------------------- factories

	private static final String MREQ_ATTR_NAME = MultipartRequest.class.getName();

	/**
	 * Returns new or existing instance of <code>MultipartRequest</code>.
	 */
	public static MultipartRequest getInstance(final HttpServletRequest request, final FileUploadFactory fileUploadFactory, final String encoding) throws IOException {
		MultipartRequest mreq = (MultipartRequest) request.getAttribute(MREQ_ATTR_NAME);
		if (mreq == null) {
			mreq = new MultipartRequest(request, fileUploadFactory, encoding);
			request.setAttribute(MREQ_ATTR_NAME, mreq);
		}
		if (!mreq.isParsed()) {
			mreq.parseRequest();
		}
		return mreq;
	}

	/**
	 * Returns new or existing instance of <code>MultipartRequest</code>.
	 */
	public static MultipartRequest getInstance(final HttpServletRequest request) throws IOException {
		return getInstance(request, null, null);
	}

	// ---------------------------------------------------------------- load


	/**
	 * Loads and parse multi-part request. It <b>doesn't</b> check if request is multi-part.
	 * Must be called on same request at most <b>once</b>.
	 */
	public void parseMultipartRequest() throws IOException {
		parseRequestStream(request.getInputStream(), characterEncoding);
	}

	/**
	 * Checks if request if multi-part and parse it. If request is not multi-part it
	 * copies all parameters, to make usage the same in both cases.
	 *
	 * @see MultipartRequestWrapper
	 */
	public void parseRequest() throws IOException {
		if (ServletUtil.isMultipartRequest(request)) {
			parseRequestStream(request.getInputStream(), characterEncoding);
		} else {
			Enumeration names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String paramName = (String) names.nextElement();
				String[] values = request.getParameterValues(paramName);

				putParameters(paramName, values);
			}
		}
	}

}