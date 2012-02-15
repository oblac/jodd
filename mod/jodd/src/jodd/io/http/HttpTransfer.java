// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.http;

import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.buffer.FastByteBuffer;

import java.io.UnsupportedEncodingException;
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
	
	// ---------------------------------------------------------------- misc

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
	 * Sets request host name. Not used internally, just holds the value
	 * for completeness.
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
	 * Sets request port number. Not used internally, just holds the value
	 * for completeness.
	 */
	public void setPort(int port) {
		this.port = port;
	}

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
	 * Sets request path.
	 */
	public void setPath(String path) {
		this.path = path;
	}

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
	 * Adds parameter to header. The Order of header
	 * parameters is preserved.
	 */
	public void addHeader(String name, String value) {
		String key = name.trim().toLowerCase();
		headers.put(key, new String[]{name, value.trim()});
	}

	public void addHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}
	
	// ---------------------------------------------------------------- body

	/**
	 * Returns body, if exist.
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * Specifies body, if exist.
	 */
	public void setBody(byte[] body) {
		this.body = body;
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
			StringBand headLine = new StringBand();
			headLine.append(values[0]);
			headLine.append(": ");
			headLine.append(values[1]);

			append(buff, headLine.toString());
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
	
}
