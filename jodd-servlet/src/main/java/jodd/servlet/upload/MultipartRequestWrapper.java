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

import jodd.http.upload.FileUpload;
import jodd.http.upload.FileUploadFactory;
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Multi-part HTTP servlet request wrapper.
 * 
 * @see MultipartRequest
 */
public class MultipartRequestWrapper extends HttpServletRequestWrapper {

	// ---------------------------------------------------------------- construction

	MultipartRequest mreq;
	HttpServletRequest req;

	public MultipartRequestWrapper(final HttpServletRequest request, final FileUploadFactory fileUploadFactory, final String encoding) throws IOException {
		super(request);
		req = request;
		if (ServletUtil.isMultipartRequest(request)) {
			mreq = MultipartRequest.getInstance(request, fileUploadFactory, encoding);
		}
	}

	public MultipartRequestWrapper(final HttpServletRequest request, final FileUploadFactory fileUploadFactory) throws IOException {
		super(request);
		if (ServletUtil.isMultipartRequest(request)) {
			mreq = MultipartRequest.getInstance(request, fileUploadFactory, null);
		}
	}

	public MultipartRequestWrapper(final MultipartRequest mpreq) {
		super(mpreq.getServletRequest());
		mreq = mpreq;
	}

	/**
	 * Returns {@link MultipartRequest} instance or <code>null</code> if request is not multi-part.
	 */
	public MultipartRequest getMultipartRequest() {
		return mreq;
	}

	/**
	 * Returns <code>true</code> if request is multi-part.
	 */
	public boolean isMultipart() {
		return mreq != null;
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Get an enumeration of the parameter names for uploaded files
	 */
	public Enumeration<String> getFileParameterNames() {
		if (mreq == null) {
			return null;
		}
		return Collections.enumeration(mreq.getFileParameterNames());
	}

	/**
	 * Get a {@link FileUpload} array for the given input field name.
	 *
	 * @param fieldName input field name
	 * @return a File[] object for files associated with the specified input field name
	 */
	public FileUpload[] getFiles(final String fieldName) {
		if (mreq == null) {
			return null;
		}
		return mreq.getFiles(fieldName);
	}

	public FileUpload getFile(final String fieldName) {
		if (mreq == null) {
			return null;
		}
		return mreq.getFile(fieldName);
	}


	/**
	 * @see javax.servlet.http.HttpServletRequest#getParameter(String)
	 */
	@Override
	public String getParameter(final String name) {
		if (mreq == null) {
			return super.getParameter(name);
		}
		return mreq.getParameter(name);
	}

	/**
	 * @see javax.servlet.http.HttpServletRequest#getParameterMap()
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		if (mreq == null) {
			return super.getParameterMap();
		}
		final Map<String, String[]> map = new HashMap<>();
		final Enumeration enumeration = getParameterNames();
		while (enumeration.hasMoreElements()) {
			final String name = (String) enumeration.nextElement();
			map.put(name, this.getParameterValues(name));
		}
		return map;
	}

	/**
	 * @see javax.servlet.http.HttpServletRequest#getParameterNames()
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public Enumeration<String> getParameterNames() {
		if (mreq == null) {
			return super.getParameterNames();
		}
		return Collections.enumeration(mreq.getParameterNames());
	}

	/**
	 * @see javax.servlet.http.HttpServletRequest#getParameterValues(String)
	 */
	@Override
	public String[] getParameterValues(final String name) {
		if (mreq == null) {
			return super.getParameterValues(name);
		}
		return mreq.getParameterValues(name);
	}

}
