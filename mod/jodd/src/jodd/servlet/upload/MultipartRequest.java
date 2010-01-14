// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet.upload;

import jodd.servlet.ServletUtil;
import jodd.servlet.upload.impl.MemoryFileUploadFactory;
import jodd.io.FastByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class MultipartRequest {

	// ---------------------------------------------------------------- properties

	private Map<String, String[]> requestParameters;
	private Map<String, FileUpload[]> requestFiles;
	private HttpServletRequest request;
	private String characterEncoding;
	private FileUploadFactory fileUploadFactory;

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
	 * If not specified, character encoding is read from the request.
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
		this.request = request;
		if (encoding != null) {
			this.characterEncoding = encoding;
		} else {
			this.characterEncoding = request.getCharacterEncoding();
		}
		this.fileUploadFactory = (fileUploadFactory == null ? new MemoryFileUploadFactory() : fileUploadFactory);
	}

	// ---------------------------------------------------------------- factories

	private static final String MREQ_ATTR_NAME = "jodd.servlet.upload.MultipartRequest";

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

	private boolean loaded;

	/**
	 * Returns <code>true</code> if multi-part request is already loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Loads and parse multi-part request. It <b>doesn't</b> check if request is multi-part.
	 * Must be called on same request at most <b>once</b>.
	 */
	public void parseMultipartRequest() throws IOException {
		if (loaded == true) {
			throw new IOException("Multi-part request already loaded and parsed.");
		}
		loaded = true;
		requestParameters = new HashMap<String, String[]>();
		requestFiles = new HashMap<String, FileUpload[]>();
		parseRequestStream(request, characterEncoding);
	}

	/**
	 * Checks if request if multi-part and parse it. If request is not multi-part it
	 * copies all parameters, to make usage the same in both cases.
	 *
	 * @see MultipartRequestWrapper
	 */
	public void parseRequest() throws IOException {
		if (loaded == true) {
			throw new IOException("Multi-part request already loaded and parsed.");
		}
		loaded = true;
		requestParameters = new HashMap<String, String[]>();
		requestFiles = new HashMap<String, FileUpload[]>();
		if (ServletUtil.isMultipartRequest(request) == true) {
			parseRequestStream(request, characterEncoding);
		} else {
			Enumeration names = request.getParameterNames();
			while (names.hasMoreElements()) {
				String paramName = (String) names.nextElement();
				String[] values = request.getParameterValues(paramName);
				requestParameters.put(paramName, values);
			}
		}
	}

	// ---------------------------------------------------------------- parameters


	/**
	 * Returns single value of a parameter. If parameter name is used for
	 * more then one parameter, only the first one will be returned.
	 *
	 * @return parameter value, or <code>null</code> if not found
	 */
	public String getParameter(String paramName) {
		String[] values = requestParameters.get(paramName);
		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		return null;
	}

	/**
	 * Returns the names of the parameters contained in this request.
	 */
	public Set<String> getParameterNames() {
		return requestParameters.keySet();
	}

	/**
	 * Returns all values all of the values the given request parameter has.
	 */
	public String[] getParameterValues(String paramName) {
		return requestParameters.get(paramName);
	}


	/**
	 * Returns uploaded file.
	 * @param paramName parameter name of the uploaded file
	 * @return uploaded file or <code>null</code> if parameter name not found
	 */
	public FileUpload getFile(String paramName) {
		FileUpload[] values = requestFiles.get(paramName);
		if ((values != null) && (values.length > 0)) {
			return values[0];
		}
		return null;
	}


	/**
	 * Returns all uploaded files the given request parameter has.
	 */
	public FileUpload[] getFiles(String paramName) {
		return requestFiles.get(paramName);
	}

	/**
	 * Returns parameter names of all uploaded files.
	 */
	public Set<String> getFileParameterNames() {
		return requestFiles.keySet();
	}


	// ---------------------------------------------------------------- load and extract

	private void putFile(Map<String, List<FileUpload>> destination, String name, FileUpload value) {
		List<FileUpload> valuesList = destination.get(name);
		if (valuesList == null) {
			valuesList = new ArrayList<FileUpload>();
			destination.put(name, valuesList);
		}
		valuesList.add(value);
	}

	private void putString(Map<String, List<String>> destination, String name, String value) {
		List<String> valuesList = destination.get(name);
		if (valuesList == null) {
			valuesList = new ArrayList<String>();
			destination.put(name, valuesList);
		}
		valuesList.add(value);
	}

	/**
	 * Extracts uploaded files and parameters from the request data.
	 */
	protected void parseRequestStream(HttpServletRequest request, String encoding) throws IOException {
		HashMap<String, List<String>> reqParam = new HashMap<String, List<String>>();
		HashMap<String, List<FileUpload>> reqFiles = new HashMap<String, List<FileUpload>>();

		MultipartRequestInputStream input = new MultipartRequestInputStream(request.getInputStream());
		input.readBoundary();
		while (true) {
			FileUploadHeader header = input.readDataHeader(encoding);
			if (header == null) {
				break;
			}

			if (header.isFile == true) {
				String fileName = header.fileName;
				if (fileName.length() > 0) {
					if (header.contentType.indexOf("application/x-macbinary") > 0) {
						input.skipBytes(128);
					}
				}
				FileUpload newFile = fileUploadFactory.create(input);
				newFile.processStream();
				if (fileName.length() == 0) {
					// file was specified, but no name was provided, therefore it was not uploaded. toask when this happens?
				}
				putFile(reqFiles, header.formFieldName, newFile);
			} else {
				// no file, therefore it is regular form parameter.
				FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
				input.copyAll(fbos);
				String value = new String(fbos.toByteArray(), encoding);
				putString(reqParam, header.formFieldName, value);
			}

			input.skipBytes(1);
			input.mark(1);
			if (input.readByte() == '-') {
				input.reset();
				break;
			}
			input.reset();
		}

		// convert lists into arrays
		for (String paramName : reqParam.keySet()) {
			List valuesList = reqParam.get(paramName);
			if (valuesList != null) {
				String[] result = new String[valuesList.size()];
				for (int i = 0; i < result.length; i++) {
					result[i] = (String) valuesList.get(i);
				}
				requestParameters.put(paramName, result);
			}
		}
		for (String paramName : reqFiles.keySet()) {
			List<FileUpload> valuesList = reqFiles.get(paramName);
			if (valuesList != null) {
				FileUpload[] result = new FileUpload[valuesList.size()];
				for (int i = 0; i < result.length; i++) {
					result[i] = valuesList.get(i);
				}
				requestFiles.put(paramName, result);
			}
		}
	}
}