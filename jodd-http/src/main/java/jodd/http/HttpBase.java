// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.JoddHttp;
import jodd.datetime.TimeUtil;
import jodd.http.up.ByteArrayUploadable;
import jodd.http.up.FileUploadable;
import jodd.http.up.Uploadable;
import jodd.io.FastCharArrayWriter;
import jodd.io.FileNameUtil;
import jodd.io.StreamUtil;
import jodd.upload.FileUpload;
import jodd.upload.MultipartStreamParser;
import jodd.util.CharUtil;
import jodd.util.MimeTypes;
import jodd.util.RandomStringUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static jodd.util.StringPool.CRLF;

/**
 * Base class for {@link HttpRequest} and {@link HttpResponse}.
 */
@SuppressWarnings("unchecked")
public abstract class HttpBase<T> {

	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_HOST = "Host";
	public static final String HEADER_ETAG = "ETag";
	public static final String HEADER_CONNECTION = "Connection";
	public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
	public static final String HEADER_CLOSE = "Close";
	public static final String HTTP_1_0 = "HTTP/1.0";
	public static final String HTTP_1_1 = "HTTP/1.1";

	protected String httpVersion = HTTP_1_1;
	protected HttpValuesMap headers = new HttpValuesMap();

	protected HttpValuesMap form;	// holds form data (when used)
	protected String body;			// holds raw body string (always)

	// ---------------------------------------------------------------- properties

	/**
	 * Returns HTTP version string. By default it's "HTTP/1.1".
	 */
	public String httpVersion() {
		return httpVersion;
	}

	/**
	 * Sets the HTTP version string. Must be formed like "HTTP/1.1".
	 */
	public T httpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
		return (T) this;
	}

	// ---------------------------------------------------------------- headers

	/**
	 * Returns value of header parameter.
	 * If multiple headers with the same names exist,
	 * the first value will be returned. Returns <code>null</code>
	 * if header doesn't exist.
	 */
	public String header(String name) {
		String key = name.trim().toLowerCase();

		Object value = headers.getFirst(key);

		if (value == null) {
			return null;
		}
		return value.toString();
	}

	/**
	 * Returns all values for given header name.
	 */
	public String[] headers(String name) {
		String key = name.trim().toLowerCase();

		return headers.getStrings(key);
	}

	/**
	 * Removes all header parameters for given name.
	 */
	public void removeHeader(String name) {
		String key = name.trim().toLowerCase();

		headers.remove(key);
	}

	/**
	 * Adds header parameter. If a header with the same name exist,
	 * it will not be overwritten, but the new header with the same
	 * name is going to be added.
	 * The order of header parameters is preserved.
	 * Also detects 'Content-Type' header and extracts
	 * {@link #mediaType() media type} and {@link #charset() charset}
	 * values.
	 */
	public T header(String name, String value) {
		return header(name, value, false);
	}

	/**
	 * Adds or sets header parameter.
	 * @see #header(String, String)
	 */
	public T header(String name, String value, boolean overwrite) {
		String key = name.trim().toLowerCase();

		value = value.trim();

		if (key.equalsIgnoreCase(HEADER_CONTENT_TYPE)) {
			mediaType = HttpUtil.extractMediaType(value);
			charset = HttpUtil.extractContentTypeCharset(value);
		}

		if (overwrite == true) {
			headers.set(key, value);
		} else {
			headers.add(key, value);
		}
		return (T) this;
	}

	/**
	 * Internal direct header setting.
	 */
	protected void _header(String name, String value, boolean overwrite) {
		String key = name.trim().toLowerCase();
		value = value.trim();
		if (overwrite) {
			headers.set(key, value);
		} else {
			headers.add(key, value);
		}
	}

	/**
	 * Adds <code>int</code> value as header parameter,
	 * @see #header(String, String)
	 */
	public T header(String name, int value) {
		_header(name, String.valueOf(value), false);
		return (T) this;
	}

	/**
	 * Adds date value as header parameter.
	 * @see #header(String, String)
	 */
	public T header(String name, long millis) {
		_header(name, TimeUtil.formatHttpDate(millis), false);
		return (T) this;
	}

	// ---------------------------------------------------------------- content type

	protected String charset;

	/**
	 * Returns charset, as defined by 'Content-Type' header.
	 * If not set, returns <code>null</code> - indicating
	 * the default charset (ISO-8859-1).
	 */
	public String charset() {
		return charset;
	}

	/**
	 * Defines just content type charset. Setting this value to
	 * <code>null</code> will remove the charset information from
	 * the header.
	 */
	public T charset(String charset) {
		this.charset = null;
		contentType(null, charset);
		return (T) this;
	}


	protected String mediaType;

	/**
	 * Returns media type, as defined by 'Content-Type' header.
	 * If not set, returns <code>null</code> - indicating
	 * the default media type, depending on request/response.
	 */
	public String mediaType() {
		return mediaType;
	}

	/**
	 * Defines just content media type.
	 * Setting this value to <code>null</code> will
	 * not have any effects.
	 */
	public T mediaType(String mediaType) {
		contentType(mediaType, null);
		return (T) this;
	}

	/**
	 * Returns full "Content-Type" header.
	 * It consists of {@link #mediaType() media type}
	 * and {@link #charset() charset}.
	 */
	public String contentType() {
		return header(HEADER_CONTENT_TYPE);
	}

	/**
	 * Sets full "Content-Type" header. Both {@link #mediaType() media type}
	 * and {@link #charset() charset} are overridden.
	 */
	public T contentType(String contentType) {
		header(HEADER_CONTENT_TYPE, contentType, true);
		return (T) this;
	}

	/**
	 * Sets "Content-Type" header by defining media-type and/or charset parameter.
	 * This method may be used to update media-type and/or charset by passing
	 * non-<code>null</code> value for changes.
	 * <p>
	 * Important: if Content-Type header has some other parameters, they will be removed!
	 */
	public T contentType(String mediaType, String charset) {
		if (mediaType == null) {
			mediaType = this.mediaType;
		} else {
			this.mediaType = mediaType;
		}

		if (charset == null) {
			charset = this.charset;
		} else {
			this.charset = charset;
		}

		String contentType = mediaType;
		if (charset != null) {
			contentType += ";charset=" + charset;
		}

		_header(HEADER_CONTENT_TYPE, contentType, true);
		return (T) this;
	}

	// ---------------------------------------------------------------- keep-alive

	/**
	 * Defines "Connection" header as "Keep-Alive" or "Close".
	 * Existing value is overwritten.
	 */
	public T connectionKeepAlive(boolean keepAlive) {
		if (keepAlive) {
			header(HEADER_CONNECTION, HEADER_KEEP_ALIVE, true);
		} else {
			header(HEADER_CONNECTION, HEADER_CLOSE, true);
		}
		return (T) this;
	}

	/**
	 * Returns <code>true</code> if connection is persistent.
	 * If "Connection" header does not exist, returns <code>true</code>
	 * for HTTP 1.1 and <code>false</code> for HTTP 1.0. If
	 * "Connection" header exist, checks if it is equal to "Close".
	 * <p>
	 * In HTTP 1.1, all connections are considered persistent unless declared otherwise.
	 * Under HTTP 1.0, there is no official specification for how keepalive operates.
	 */
	public boolean isConnectionPersistent() {
		String connection = header(HEADER_CONNECTION);
		if (connection == null) {
			return !httpVersion.equalsIgnoreCase(HTTP_1_0);
		}

		return !connection.equalsIgnoreCase(HEADER_CLOSE);
	}

	// ---------------------------------------------------------------- common headers

	/**
	 * Returns full "Content-Length" header or
	 * <code>null</code> if not set.
	 */
	public String contentLength() {
		return header(HEADER_CONTENT_LENGTH);
	}

	/**
	 * Sets the full "Content-Length" header.
	 */
	public T contentLength(int value) {
		_header(HEADER_CONTENT_LENGTH, String.valueOf(value), true);
		return (T) this;
	}

	/**
	 * Returns "Content-Encoding" header.
	 */
	public String contentEncoding() {
		return header(HEADER_CONTENT_ENCODING);
	}

	/**
	 * Returns "Accept-Encoding" header.
	 */
	public String acceptEncoding() {
		return header(HEADER_ACCEPT_ENCODING);
	}

	/**
	 * Sets "Accept-Encoding" header.
	 */
	public T acceptEncoding(String encodings) {
		header(HEADER_ACCEPT_ENCODING, encodings, true);
		return (T) this;
	}

	// ---------------------------------------------------------------- form

	protected void initForm() {
		if (form == null) {
			form = new HttpValuesMap();
		}
	}

	/**
	 * Wraps non-Strings form values with {@link jodd.http.up.Uploadable uploadable content}.
	 * Detects invalid types and throws an exception. So all uploadable values
	 * are of the same type.
	 */
	protected Object wrapFormValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof CharSequence) {
			return value.toString();
		}
		if (value instanceof File) {
			return new FileUploadable((File) value);
		}
		if (value instanceof byte[]) {
			return new ByteArrayUploadable((byte[]) value, null);
		}
		if (value instanceof Uploadable) {
			return value;
		}

		throw new HttpException("Unsupported value type: " + value.getClass().getName());
	}

	/**
	 * Adds the form parameter. Existing parameter will not be overwritten.
	 */
	public T form(String name, Object value) {
		initForm();

		value = wrapFormValue(value);
		form.add(name, value);

		return (T) this;
	}

	/**
	 * Sets form parameter. Optionally overwrite existing one.
	 */
	public T form(String name, Object value, boolean overwrite) {
		initForm();

		value = wrapFormValue(value);

		if (overwrite) {
			form.set(name, value);
		} else {
			form.add(name, value);
		}

		return (T) this;
	}

	/**
	 * Sets many form parameters at once.
	 */
	public T form(String name, Object value, Object... parameters) {
		initForm();

		form(name, value);

		for (int i = 0; i < parameters.length; i += 2) {
			name = parameters[i].toString();

			form(name, parameters[i + 1]);
		}
		return (T) this;
	}

	/**
	 * Sets many form parameters at once.
	 */
	public T form(Map<String, Object> formMap) {
		initForm();

		for (Map.Entry<String, Object> entry : formMap.entrySet()) {
			form(entry.getKey(), entry.getValue());
		}
		return (T) this;
	}

	/**
	 * Return map of form parameters.
	 * Note that all uploadable values are wrapped with {@link jodd.http.up.Uploadable}.
	 */
	public Map<String, Object[]> form() {
		return form;
	}

	// ---------------------------------------------------------------- form encoding

	protected String formEncoding = JoddHttp.defaultFormEncoding;

	/**
	 * Defines encoding for forms parameters. Default value is
	 * copied from {@link JoddHttp#defaultFormEncoding}.
	 * It is overridden by {@link #charset() charset} value.
	 */
	public T formEncoding(String encoding) {
		this.formEncoding = encoding;
		return (T) this;
	}

	// ---------------------------------------------------------------- body

	/**
	 * Returns <b>raw</b> body as received or set (always in ISO-8859-1 encoding).
	 * If body content is a text, use {@link #bodyText()} to get it converted.
	 * Returns <code>null</code> if body is not specified!
	 */
	public String body() {
		return body;
	}

	/**
	 * Returns <b>raw</b> body bytes.
	 */
	public byte[] bodyBytes() {
		if (body == null) {
			return null;
		}
		try {
			return body.getBytes(StringPool.ISO_8859_1);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	/**
	 * Returns {@link #body() body content} as text. If {@link #charset() charset parameter}
	 * of "Content-Type" header is defined, body string charset is converted, otherwise
	 * the same raw body content is returned.
	 */
	public String bodyText() {
		if (body == null) {
			return null;
		}
		if (charset != null) {
			return StringUtil.convertCharset(body, StringPool.ISO_8859_1, charset);
		}
		return body();
	}

	/**
	 * Sets <b>raw</b> body content and discards all form parameters.
	 * Important: body string is in RAW format, meaning, ISO-8859-1 encoding.
	 * Also sets "Content-Length" parameter. However, "Content-Type" is not set
	 * and it is expected from user to set this one.
	 */
	public T body(String body) {
		this.body = body;
		this.form = null;
		contentLength(body.length());
		return (T) this;
	}

	/**
	 * Defines body text and content type (as media type and charset).
	 * Body string will be converted to {@link #body(String) raw body string}
	 * and "Content-Type" header will be set.
	 */
	public T bodyText(String body, String mediaType, String charset) {
		body = StringUtil.convertCharset(body, charset, StringPool.ISO_8859_1);
		contentType(mediaType, charset);
		body(body);
		return (T) this;
	}

	/**
	 * Defines {@link #bodyText(String, String, String) body text content}
	 * that will be encoded in {@link JoddHttp#defaultBodyEncoding default body encoding}.
	 */
	public T bodyText(String body, String mediaType) {
		return bodyText(body, mediaType, JoddHttp.defaultBodyEncoding);
	}
	/**
	 * Defines {@link #bodyText(String, String, String) body text content}
	 * that will be encoded as {@link JoddHttp#defaultBodyMediaType default body media type}
	 * in {@link JoddHttp#defaultBodyEncoding default body encoding}.
	 */
	public T bodyText(String body) {
		return bodyText(body, JoddHttp.defaultBodyMediaType, JoddHttp.defaultBodyEncoding);
	}

	/**
	 * Sets <b>raw</b> body content and discards form parameters.
	 * Also sets "Content-Length" and "Content-Type" parameter.
	 * @see #body(String)
	 */
	public T body(byte[] content, String contentType) {
		String body = null;
		try {
			body = new String(content, StringPool.ISO_8859_1);
		} catch (UnsupportedEncodingException ignore) {
		}
		contentType(contentType);
		return body(body);
	}

	// ---------------------------------------------------------------- body form

	/**
	 * Returns <code>true</code> if form contains {@link jodd.http.up.Uploadable}.
	 */
	protected boolean isFormMultipart() {
		for (Object[] values : form.values()) {
			if (values == null) {
				continue;
			}

			for (Object value : values) {
				if (value instanceof Uploadable) {
					return true;
				}
			}
		}
	    return false;
	}

	/**
	 * Creates form string and sets few headers.
	 */
	protected String formString() {
		if (form == null || form.isEmpty()) {
			return StringPool.EMPTY;
		}

		// todo allow user to force usage of multipart

		if (!isFormMultipart()) {
			// determine form encoding
			String formEncoding = charset;

			if (formEncoding == null) {
				formEncoding = this.formEncoding;
			}

			// encode
			String formQueryString = HttpUtil.buildQuery(form, formEncoding);

			contentType("application/x-www-form-urlencoded", null);
			contentLength(formQueryString.length());

			return formQueryString;
		}

		String boundary = StringUtil.repeat('-', 10) + RandomStringUtil.randomAlphaNumeric(10);

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Object[]> entry : form.entrySet()) {

			sb.append("--");
			sb.append(boundary);
			sb.append(CRLF);

			String name = entry.getKey();
			Object[] values = entry.getValue();

			for (Object value : values) {
				if (value instanceof String) {
					String string = (String) value;
					sb.append("Content-Disposition: form-data; name=\"").append(name).append('"').append(CRLF);
					sb.append(CRLF);
					sb.append(string);
				}
				else if (value instanceof Uploadable) {
					Uploadable uploadable = (Uploadable) value;

					String fileName = uploadable.getFileName();
					if (fileName == null) {
						fileName = name;
					}

					sb.append("Content-Disposition: form-data; name=\"").append(name);
					sb.append("\"; filename=\"").append(fileName).append('"').append(CRLF);

					String mimeType = uploadable.getMimeType();
					if (mimeType == null) {
						mimeType = MimeTypes.getMimeType(FileNameUtil.getExtension(fileName));
					}
					sb.append(HEADER_CONTENT_TYPE).append(": ").append(mimeType).append(CRLF);

					sb.append("Content-Transfer-Encoding: binary").append(CRLF);
					sb.append(CRLF);

					byte[] bytes = uploadable.getBytes();
					for (byte b : bytes) {
						sb.append(CharUtil.toChar(b));
					}
				} else {
					// should never happened!
					throw new HttpException("Unsupported type");
				}
				sb.append(CRLF);
			}
		}

		sb.append("--").append(boundary).append("--");

		// the end
		contentType("multipart/form-data; boundary=" + boundary);
		contentLength(sb.length());

		return sb.toString();
	}

	// ---------------------------------------------------------------- send

	/**
	 * Returns byte array of request or response.
	 */
	public byte[] toByteArray() {
		try {
			return toString().getBytes(StringPool.ISO_8859_1);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	/**
	 * Sends request or response to output stream.
	 */
	public void sendTo(OutputStream out) throws IOException {
		byte[] bytes = toByteArray();

		out.write(bytes);

		out.flush();
	}

	// ---------------------------------------------------------------- parsing

	/**
	 * Parses headers.
	 */
	protected void readHeaders(BufferedReader reader) {
		while (true) {
			String line;
			try {
				line = reader.readLine();
			} catch (IOException ioex) {
				throw new HttpException(ioex);
			}

			if (StringUtil.isBlank(line)) {
				break;
			}

			int ndx = line.indexOf(':');
			if (ndx != -1) {
				header(line.substring(0, ndx), line.substring(ndx + 1));
			} else {
				throw new HttpException("Invalid header: " + line);
			}
		}
	}

	/**
	 * Parses body.
	 */
	protected void readBody(BufferedReader reader) {
		String bodyString = null;

		// content length
		String contentLen = contentLength();
		int contentLenValue = -1;

		if (contentLen != null) {
			contentLenValue = Integer.parseInt(contentLen);

			if (contentLenValue > 0) {
				FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter(contentLenValue);

				try {
					StreamUtil.copy(reader, fastCharArrayWriter, contentLenValue);
				} catch (IOException ioex) {
					throw new HttpException(ioex);
				}

				bodyString = fastCharArrayWriter.toString();
			}
		}

		// chunked encoding
		String transferEncoding = header("Transfer-Encoding");
		if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked")) {

			FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter();
			try {
				while (true) {
					String line = reader.readLine();

					int len = Integer.parseInt(line, 16);

					if (len > 0) {
						StreamUtil.copy(reader, fastCharArrayWriter, len);
						reader.readLine();
					} else {
						// end reached, read trailing headers, if there is any
						readHeaders(reader);
						break;
					}
				}
			} catch (IOException ioex) {
				throw new HttpException(ioex);
			}

			bodyString = fastCharArrayWriter.toString();
		}

		// no body yet - special case
		if (bodyString == null && contentLenValue != 0) {
			// body ends when stream closes
			FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter();
			try {
				StreamUtil.copy(reader, fastCharArrayWriter);
			} catch (IOException ioex) {
				throw new HttpException(ioex);
			}
			bodyString = fastCharArrayWriter.toString();
		}

		// BODY READY - PARSE BODY
		String charset = this.charset;
		if (charset == null) {
			charset = StringPool.ISO_8859_1;
		}
		body = bodyString;

		String mediaType = mediaType();

		if (mediaType == null) {
			mediaType = StringPool.EMPTY;
		} else {
			mediaType = mediaType.toLowerCase();
		}

		if (mediaType.equals("application/x-www-form-urlencoded")) {
			form = HttpUtil.parseQuery(bodyString, true);
			return;
		}

		if (mediaType.equals("multipart/form-data")) {
			form = new HttpValuesMap();

			MultipartStreamParser multipartParser = new MultipartStreamParser();

			try {
				byte[] bodyBytes = bodyString.getBytes(StringPool.ISO_8859_1);
				ByteArrayInputStream bin = new ByteArrayInputStream(bodyBytes);
				multipartParser.parseRequestStream(bin, charset);
			} catch (IOException ioex) {
				throw new HttpException(ioex);
			}

			// string parameters
			for (String paramName : multipartParser.getParameterNames()) {
				String[] values = multipartParser.getParameterValues(paramName);
				if (values.length == 1) {
					form.add(paramName, values[0]);
				} else {
					form.put(paramName, values);
				}
			}

			// file parameters
			for (String paramName : multipartParser.getFileParameterNames()) {
				FileUpload[] values = multipartParser.getFiles(paramName);
				if (values.length == 1) {
					form.add(paramName, values[0]);
				} else {
					form.put(paramName, values);
				}
			}

			return;
		}

		// body is a simple content

		form = null;
	}

}