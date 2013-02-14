// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.upload.FileUpload;
import jodd.upload.MultipartStreamParser;
import jodd.util.Base64;
import jodd.util.KeyValue;
import jodd.util.MimeTypes;
import jodd.util.RandomStringUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.buffer.FastByteBuffer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Raw content of HTTP transfer for both requests and responses.
 */
public class HttpTransfer {

	protected String httpVersion = "HTTP/1.1";
	
	protected String host;

	protected int port = 80;

	protected String method;
	
	private String _path;	// don't use it directly!
	
	protected int statusCode;
	
	protected String statusPhrase;

	protected Map<String, String[]> headers = new LinkedHashMap<String, String[]>();

	protected byte[] body;
	
	// ---------------------------------------------------------------- common

	/**
	 * Creates socket using host and port.
	 */
	public Socket createSocket() throws IOException {
		return new Socket(getHost(), getPort());
	}

	/**
	 * Creates HttpURLConnection using host, port and path.
	 * @see #buildURL()
	 */
	public HttpURLConnection createURLConnection() throws IOException {
		URL url = buildURL();

		return (HttpURLConnection) url.openConnection();
	}

	/**
	 * Builds URL from connection data: host, port and path.
	 */
	public URL buildURL() {
		try {
			return new URL("http", host, port, getPath());
		} catch (MalformedURLException murlex) {
			return null;
		}
	}

	/**
	 * Returns HTTP version string. By default it's "HTTP/1.1".
	 */
	public String getHttpVersion() {
		return httpVersion;
	}

	/**
	 * Sets the HTTP version string. Must be formed like "HTTP/1.1".
	 */
	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	/**
	 * Returns request host name.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets request host name.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns request port number.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets request port number.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	// ---------------------------------------------------------------- request

	/**
	 * Returns request method.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Specifies request method.
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Returns request path (including query part).
	 */
	public String getPath() {
		applyQueryParameters();		// make sure that path is set correctly

		return _path;
	}

	/**
	 * Sets request path, including the query. Adds a slash if path doesn't start with one.
	 * Setting the path invalidates any previously created {@link #getQueryParameters() query parameters}
	 * object!
	 */
	public void setPath(String path) {
		if (path.startsWith(StringPool.SLASH) == false) {
			path = StringPool.SLASH + path;
		}

		this._path = path;

		queryParameters = null;
	}

	// ---------------------------------------------------------------- response

	/**
	 * Returns response status code.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets response status code.
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Returns response status phrase.
	 */
	public String getStatusPhrase() {
		return statusPhrase;
	}

	/**
	 * Sets response status phrase.
	 */
	public void setStatusPhrase(String statusPhrase) {
		this.statusPhrase = statusPhrase;
	}

	// ---------------------------------------------------------------- headers

	/**
	 * Returns value of header parameter.
	 */
	public String getHeader(String name) {
		String key = name.trim().toLowerCase();
		String[] values = headers.get(key);
		if (values == null) {
			return null;
		}
		return headers.get(key)[1];
	}

	/**
	 * Removes some parameter from header.
	 */
	public void removeHeader(String name) {
		String key = name.trim().toLowerCase();
		headers.remove(key);
	}

	/**
	 * Adds parameter to header. Existing parameter is overwritten.
	 * The order of header parameters is preserved.
	 */
	public void addHeader(String name, String value) {
		String key = name.trim().toLowerCase();
		headers.put(key, new String[]{name, value.trim()});
	}

	/**
	 * @see #addHeader(String, String)
	 */
	public void addHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

	// ---------------------------------------------------------------- auth

	/**
	 * Enables basic authentication by adding required header.
	 */
	public void useBasicAuthentication(String username, String password) {
		String data = username.concat(StringPool.COLON).concat(password);

		String base64 = Base64.encodeToString(data);

		addHeader("Authorization", "Basic " + base64);
	}
	
	// ---------------------------------------------------------------- body

	/**
	 * Returns body or <code>null</code>.
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * Specifies body.
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

	// ---------------------------------------------------------------- query

	/**
	 * Contains query parameters.
	 */
	protected HttpParams queryParameters;

	/**
	 * Sets new query parameters. Existing query parameters are thrown away.
	 */
	public void setQueryParameters(HttpParams httpParams) {
		queryParameters = httpParams;

		queryParameters.modified = true;
	}

	/**
	 * Returns query parameters from the {@link HttpTransfer} path.
	 * First time the parameters will be created from the query.
	 */
	public HttpParams getQueryParameters() {
		if (queryParameters != null) {
			return queryParameters;
		}

		final String path = this._path;
		int ndx = path.indexOf('?');

		if (ndx != -1) {
			String query = path.substring(ndx + 1);

			queryParameters = new HttpParams(query, true);
		} else {
			queryParameters = new HttpParams();
		}

		return queryParameters;
	}

	/**
	 * Applies query parameters, if modified.
	 */
	protected void applyQueryParameters() {
		if (queryParameters == null) {
			return;
		}
		if (queryParameters.modified == false) {
			return;
		}

		String path = this._path;

		int ndx = path.indexOf('?');
		if (ndx != -1) {
			path = path.substring(0, ndx);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(path);

		String query = queryParameters.toString();

		if (query.length() != 0) {
			sb.append('?');
			sb.append(query);
		}

		queryParameters.modified = false;

		this._path = sb.toString();
	}

	// ---------------------------------------------------------------- request parameters

	/**
	 * Returns request parameters. For uploaded file, {@link FileUpload} is returned.
	 */
	public HttpParams getRequestParameters() {
		String contentType = getHeader("Content-Type");
		if (contentType.equals("application/x-www-form-urlencoded")) {
			try {
				return new HttpParams(new String(body, StringPool.ISO_8859_1), true);
			} catch (UnsupportedEncodingException ignore) {
			}
		}

		
		HttpParams httpParams = new HttpParams();
		
		MultipartStreamParser multipartParser = new MultipartStreamParser();

		ByteArrayInputStream bin = new ByteArrayInputStream(body);
		try {
			multipartParser.parseRequestStream(bin, StringPool.ISO_8859_1);
		} catch (IOException ioex) {
			return null;
		}
		
		for (String paramName : multipartParser.getParameterNames()) {
			String[] values = multipartParser.getParameterValues(paramName);
			if (values.length == 1) {
				httpParams.addParameter(paramName, values[0]);
			} else {
				httpParams.addParameter(paramName, values);
			}
		}
		for (String paramName : multipartParser.getFileParameterNames()) {
			FileUpload[] values = multipartParser.getFiles(paramName);
			if (values.length == 1) {
				httpParams.addParameter(paramName, values[0]);
			} else {
				httpParams.addParameter(paramName, values);
			}
		}
		return httpParams;
	}

	/**
	 * Sets the request parameters. This can be done in two ways: by setting the simple form
	 * encoded parameters, or by setting multipart request parameters.
	 */
	public void setRequestParameters(HttpParams httpParams) {
		if (httpParams.hasFiles()) {
			try {
				setMultipartRequestParameters(httpParams);
			} catch (IOException ignore) {
			}
			return;
		}
		String body = httpParams.toString();
		addHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			byte[] bytes = body.getBytes(StringPool.ISO_8859_1);
			setBody(bytes);
			addHeader("Content-Length", bytes.length);

		} catch (UnsupportedEncodingException ignore) {
		}
	}

	protected void setMultipartRequestParameters(HttpParams httpParams) throws IOException {
		String boundary = StringUtil.repeat('-', 15) + RandomStringUtil.randomAlphaNumeric(25);

		addHeader("Content-Type", "multipart/form-data, boundary=" + boundary);

		Iterator<KeyValue<String, Object>> iter = httpParams.iterate();

		StringBuilder sb = new StringBuilder();

		while (iter.hasNext()) {
			KeyValue<String, Object> entry = iter.next();

			sb.append(boundary);
			sb.append("\r\n");

			String name = entry.getKey();
			Object value =  entry.getValue();
			Class type = value.getClass();

			if (type == String.class) {
				sb.append("Content-Disposition: form-data; name=\"").append(name).append("\"\r\n\r\n");
				sb.append(value);
			} else if (type == String[].class) {
				String[] array = (String[]) value;
				for (String v : array) {
					sb.append("Content-Disposition: form-data; name=\"").append(name).append("\"\r\n\r\n");
					sb.append(v);
				}
			} else if (type == File.class) {
				File file = (File) value;
				String fileName = FileNameUtil.getName(file.getName());

				sb.append("Content-Disposition: form-data; name=\"").append(name).append("\";filename=\"").append(fileName).append("\"\r\n");
				sb.append("Content-Type: ").append(MimeTypes.getMimeType(FileNameUtil.getExtension(fileName))).append("\r\n");
				sb.append("Content-Transfer-Encoding: binary\r\n\r\n");

				char[] chars = FileUtil.readChars(file, StringPool.ISO_8859_1);
				sb.append(chars);
			} else {
				throw new HttpException("Unsupported parameter type: " + type.getName());
			}
			sb.append("\r\n");
		}

		sb.append(boundary).append("--\r\n");

		try {
			byte[] bytes = sb.toString().getBytes(StringPool.ISO_8859_1);
			setBody(bytes);
			addHeader("Content-Length", bytes.length);
		} catch (UnsupportedEncodingException ignore) {
		}

	}

	// ---------------------------------------------------------------- array

	public static final byte[] SPACE = " ".getBytes();
	public static final byte[] CRLF = "\r\n".getBytes();

	protected void append(FastByteBuffer buff, String string) {
		try {
			buff.append(string.getBytes(StringPool.ISO_8859_1));
		} catch (UnsupportedEncodingException ignore) {
		}
	}

	/**
	 * Converts HTTP transfer to byte array ready for sending.
	 */
	public byte[] toArray() {
		FastByteBuffer buff = new FastByteBuffer();

		if (method != null) {
			append(buff, method);
			buff.append(SPACE);
			append(buff, getPath());
			buff.append(SPACE);
			append(buff, httpVersion);
			buff.append(CRLF);
		} else {
			append(buff, httpVersion);
			buff.append(SPACE);
			append(buff, String.valueOf(statusCode));
			buff.append(SPACE);
			append(buff, statusPhrase);
			buff.append(CRLF);
		}

		for (String[] values : headers.values()) {
			String headLine = values[0].concat(": ").concat(values[1]);

			append(buff, headLine);
			buff.append(CRLF);
		}

		buff.append(CRLF);

		if (body != null) {
			buff.append(body);
		}

		return buff.toArray();
	}

	/**
	 * String representation of the HTTP transfer bytes.
	 */
	public String toString() {
		try {
			return new String(toArray(), StringPool.ISO_8859_1);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	// ---------------------------------------------------------------- send

	/**
	 * Sends complete HTTP transfer to socket.
	 * @see #send(java.io.OutputStream)
	 */
	public void send(Socket socket) throws IOException {
		send(socket.getOutputStream());
	}

	/**
	 * Sends complete HTTP transfer to output stream.
	 */
	public void send(OutputStream out) throws IOException {
		out.write(toArray());
		out.flush();
	}

	/**
	 * Sends data to HttpURLConnection.
	 */
	public void send(HttpURLConnection huc) throws IOException {
		if (method != null) {
			huc.setRequestMethod(method);
		}

		for (String[] values : headers.values()) {
			huc.setRequestProperty(values[0], values[1]);
		}

		huc.setDoOutput(true);
		huc.connect();

		if (body != null) {
			OutputStream out = huc.getOutputStream();
			out.write(body);
			out.flush();
		}

	}

}