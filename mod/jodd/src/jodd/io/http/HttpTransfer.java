// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.http;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.util.KeyValue;
import jodd.util.MimeTypes;
import jodd.util.RandomStringUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import jodd.util.buffer.FastByteBuffer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
	
	protected String path;
	
	protected int statusCode;
	
	protected String statusPhrase;

	protected Map<String, String[]> headers = new LinkedHashMap<String, String[]>();

	protected byte[] body;
	
	// ---------------------------------------------------------------- common

	/**
	 * Builds URL from connection data: host, port and path.
	 */
	public URL buildURL() {
		try {
			return new URL("http", host, port, path);
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
	 * Returns request path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets request path. Adds a slash if path doesn't start with one.
	 */
	public void setPath(String path) {
		if (path.startsWith(StringPool.SLASH) == false) {
			path = StringPool.SLASH + path;
		}
		this.path = path;
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
	 * Sets query parameters.
	 */
	public void setQueryParameters(HttpParams httpParams) {
		String path = getPath();

		int ndx = path.indexOf('?');
		if (ndx != -1) {
			path = path.substring(0, ndx);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(path);

		String query = httpParams.toString();

		if (query.length() != 0) {
			sb.append('?');
			sb.append(query);
		}

		setPath(sb.toString());
	}

	/**
	 * Reads query parameters from the {@link HttpTransfer} path.
	 * Path remains unmodified.
	 */
	public HttpParams getQueryParameters() {
		String path = getPath();

		HttpParams httpParams = null;

		int ndx = path.indexOf('?');
		if (ndx != -1) {
			String query = path.substring(ndx + 1);

			httpParams = new HttpParams();
			httpParams.addParameters(query, true);
		}

	    return httpParams;
	}

	// ---------------------------------------------------------------- request parameters

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
				sb.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
				sb.append(value);
			} else if (type == String[].class) {
				String[] array = (String[]) value;
				for (String v : array) {
					sb.append("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n");
					sb.append(v);
				}
			} else if (value instanceof File) {
				File file = (File) value;
				String fileName = FileNameUtil.getName(file.getName());

				sb.append("Content-Disposition: form-data; name=\"" + name + "\";filename=\"" + fileName + "\"\r\n");
				sb.append("Content-Type: ").append(MimeTypes.getMimeType(FileNameUtil.getExtension(fileName))).append("\r\n");
				sb.append("Content-Transfer-Encoding: binary\r\n\r\n");

				char[] chars = FileUtil.readChars(file, StringPool.ISO_8859_1);
				sb.append(chars);
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
			append(buff, path);
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
