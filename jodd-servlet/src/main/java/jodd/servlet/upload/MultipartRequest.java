// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload;

import jodd.JoddDefault;
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
	 * @see #MultipartRequest(javax.servlet.http.HttpServletRequest, FileUploadFactory, String)
	 */
	public MultipartRequest(HttpServletRequest request) {
		this(request, null, null);
	}

	/**
	 * @see #MultipartRequest(javax.servlet.http.HttpServletRequest, FileUploadFactory, String)
	 */
	public MultipartRequest(HttpServletRequest request, FileUploadFactory fileUploadFactory) {
		this(request, fileUploadFactory, null);
	}

	/**
	 * @see #MultipartRequest(javax.servlet.http.HttpServletRequest, FileUploadFactory, String)
	 */
	public MultipartRequest(HttpServletRequest request, String encoding) {
		this(request, null, encoding);
	}

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
	public MultipartRequest(HttpServletRequest request, FileUploadFactory fileUploadFactory, String encoding) {
		super(fileUploadFactory);
		this.request = request;
		if (encoding != null) {
			this.characterEncoding = encoding;
		} else {
			this.characterEncoding = request.getCharacterEncoding();
		}
		if (this.characterEncoding == null) {
			this.characterEncoding = JoddDefault.encoding;
		}
	}

	// ---------------------------------------------------------------- factories

	private static final String MREQ_ATTR_NAME = MultipartRequest.class.getName();

	/**
	 * Returns a new instance of MultipleRequest if it was not created before during current request.
	 */
	public static MultipartRequest getInstance(HttpServletRequest request, FileUploadFactory fileUploadFactory, String encoding) {
		MultipartRequest mreq = (MultipartRequest) request.getAttribute(MREQ_ATTR_NAME);
		if (mreq == null) {
			mreq = new MultipartRequest(request, fileUploadFactory, encoding);
			request.setAttribute(MREQ_ATTR_NAME, mreq);
		}
		return mreq;
	}

	/**
	 * Returns parsed instance of MultipartRequest.
	 */
	public static MultipartRequest getParsedInstance(HttpServletRequest request, FileUploadFactory fileUploadFactory, String encoding) throws IOException {
		MultipartRequest mreq = getInstance(request, fileUploadFactory, encoding);
		if (mreq.isLoaded() == false) {
			mreq.parseRequest();
		}
		return mreq;
	}

	public static MultipartRequest getInstance(HttpServletRequest request, String encoding) {
		return getInstance(request, null, encoding);
	}
	public static MultipartRequest getParsedInstance(HttpServletRequest request, String encoding) throws IOException {
		MultipartRequest mreq = getInstance(request, null, encoding);
		if (mreq.isLoaded() == false) {
			mreq.parseRequest();
		}
		return mreq;
	}


	public static MultipartRequest getInstance(HttpServletRequest request, FileUploadFactory fileUploadFactory) {
		return getInstance(request, fileUploadFactory, null);
	}
	public static MultipartRequest getParsedInstance(HttpServletRequest request, FileUploadFactory fileUploadFactory) throws IOException {
		MultipartRequest mreq = getInstance(request, fileUploadFactory, null);
		if (mreq.isLoaded() == false) {
			mreq.parseRequest();
		}
		return mreq;
	}

	public static MultipartRequest getInstance(HttpServletRequest request) {
		return getInstance(request, null, null);
	}
	public static MultipartRequest getParsedInstance(HttpServletRequest request) throws IOException {
		MultipartRequest mreq = getInstance(request, null, null);
		if (mreq.isLoaded() == false) {
			mreq.parseRequest();
		}
		return mreq;
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
		if (ServletUtil.isMultipartRequest(request) == true) {
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