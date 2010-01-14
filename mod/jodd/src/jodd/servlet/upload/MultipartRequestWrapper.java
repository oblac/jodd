// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload;

import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Collections;
import java.io.IOException;

/**
 * Multi-part HTTP servlet request wrapper.
 * 
 * @see MultipartRequest
 */
public class MultipartRequestWrapper extends HttpServletRequestWrapper {

	// ---------------------------------------------------------------- construction

	MultipartRequest mreq;
	HttpServletRequest req;

	public MultipartRequestWrapper(HttpServletRequest request, FileUploadFactory fileUploadFactory, String encoding) throws IOException {
		super(request);
		req = request;
		if (ServletUtil.isMultipartRequest(request)) {
			mreq = MultipartRequest.getParsedInstance(request, fileUploadFactory, encoding);
		}
	}

	public MultipartRequestWrapper(HttpServletRequest request, FileUploadFactory fileUploadFactory) throws IOException {
		super(request);
		if (ServletUtil.isMultipartRequest(request)) {
			mreq = MultipartRequest.getParsedInstance(request, fileUploadFactory);
		}
	}

	public MultipartRequestWrapper(MultipartRequest mpreq) {
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
	public FileUpload[] getFiles(String fieldName) {
		if (mreq == null) {
			return null;
		}
		return mreq.getFiles(fieldName);
	}

	public FileUpload getFile(String fieldName) {
		if (mreq == null) {
			return null;
		}
		return mreq.getFile(fieldName);
	}


	/**
	 * @see javax.servlet.http.HttpServletRequest#getParameter(String)
	 */
	@Override
	public String getParameter(String name) {
		if (mreq == null) {
			return super.getParameter(name);
		}
		return mreq.getParameter(name);
	}

	/**
	 * @see javax.servlet.http.HttpServletRequest#getParameterMap()
	 */
	@Override
	public Map getParameterMap() {
		if (mreq == null) {
			return super.getParameterMap();
		}
		Map<String, String[]> map = new HashMap<String, String[]>();
		Enumeration enumeration = getParameterNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
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
	public String[] getParameterValues(String name) {
		if (mreq == null) {
			return super.getParameterValues(name);
		}
		return mreq.getParameterValues(name);
	}

}
