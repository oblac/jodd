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

package jodd.http;

import jodd.http.up.ByteArrayUploadable;
import jodd.http.up.FileUploadable;
import jodd.http.up.Uploadable;
import jodd.io.FastCharArrayWriter;
import jodd.io.FileNameUtil;
import jodd.io.StreamUtil;
import jodd.io.upload.FileUpload;
import jodd.io.upload.MultipartStreamParser;
import jodd.net.MimeTypes;
import jodd.time.TimeUtil;
import jodd.util.RandomString;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static jodd.util.StringPool.CRLF;

/**
 * Base class for {@link HttpRequest} and {@link HttpResponse}.
 */
public abstract class HttpBase<T> {

	public static class Defaults {

		public static final int DEFAULT_PORT = -1;

		/**
		 * Default HTTP query parameters encoding (UTF-8).
		 */
		public static String queryEncoding = StringPool.UTF_8;
		/**
		 * Default form encoding (UTF-8).
		 */
		public static String formEncoding = StringPool.UTF_8;
		/**
		 * Default body media type.
		 */
		public static String bodyMediaType = MimeTypes.MIME_TEXT_HTML;
		/**
		 * Default body encoding (UTF-8).
		 */
		public static String bodyEncoding = StringPool.UTF_8;
		/**
		 * Default user agent value.
		 */
		public static String userAgent = "Jodd HTTP";
		/**
		 * Flag that controls if headers should be rewritten and capitalized in PascalCase.
		 * When disabled, header keys are used as they are passed.
		 * When flag is enabled, header keys will be capitalized.
		 */
		public static boolean capitalizeHeaderKeys = true;

	}

	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_AUTHORIZATION = "Authorization";
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
	protected boolean capitalizeHeaderKeys = Defaults.capitalizeHeaderKeys;
	protected final HeadersMultiMap headers = new HeadersMultiMap();

	protected HttpMultiMap<?> form;			// holds form data (when used)
	protected String body;					// holds raw body string (always)

	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}

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
	public T httpVersion(final String httpVersion) {
		this.httpVersion = httpVersion;
		return _this();
	}

	/**
	 * Returns whether header keys should be strict or not, when they are
	 * modified by changing them to PascalCase.
	 * @see Defaults#capitalizeHeaderKeys
	 */
	public boolean capitalizeHeaderKeys() {
		return capitalizeHeaderKeys;
	}
	
	/**
	 * Sets headers behavior.
	 * @see Defaults#capitalizeHeaderKeys
	 */
	public T capitalizeHeaderKeys(final boolean capitalizeHeaderKeys) {
		this.capitalizeHeaderKeys = capitalizeHeaderKeys;
		return _this();
	}

	// ---------------------------------------------------------------- headers

	/**
	 * Returns value of header parameter.
	 * If multiple headers with the same names exist,
	 * the first value will be returned. Returns <code>null</code>
	 * if header doesn't exist.
	 */
	public String header(final String name) {
		return headers.getHeader(name);
	}

	/**
	 * Returns all values for given header name.
	 */
	public List<String> headers(final String name) {
		return headers.getAll(name);
	}

	/**
	 * Removes all header parameters for given name.
	 */
	public void headerRemove(final String name) {
		headers.remove(name.trim());
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
	public T header(final String name, final String value) {
		return _header(name, value, false);
	}

	/**
	 * Adds many header parameters at once.
	 * @see #header(String, String)
	 */
	public T header(final Map<String, String> headerMap) {
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			header(entry.getKey(), entry.getValue());
		}
		return _this();
	}

	/**
	 * Sets the header by overwriting it.
	 * @see #header(String, String)
	 */
	public T headerOverwrite(final String name, String value) {
		return _header(name, value, true);
	}

	/**
	 * Adds or sets header parameter.
	 * @see #header(String, String)
	 */
	protected T _header(final String name, String value, final boolean overwrite) {
		String key = name.trim();

		if (key.equalsIgnoreCase(HEADER_CONTENT_TYPE)) {
			value = value.trim();

			mediaType = HttpUtil.extractMediaType(value);
			charset = HttpUtil.extractContentTypeCharset(value);
		}

		_headerRaw(name, value, overwrite);

		return _this();
	}

	/**
	 * Internal direct header setting.
	 */
	protected void _headerRaw(String name, String value, final boolean overwrite) {
		name = name.trim();
		value = value.trim();

		if (overwrite) {
			headers.setHeader(name, value);
		} else {
			headers.addHeader(name, value);
		}
	}

	/**
	 * Adds <code>int</code> value as header parameter,
	 * @see #header(String, String)
	 */
	public T header(final String name, final int value) {
		_headerRaw(name, String.valueOf(value), false);
		return _this();
	}

	/**
	 * Adds date value as header parameter.
	 * @see #header(String, String)
	 */
	public T header(final String name, final long millis) {
		_headerRaw(name, TimeUtil.formatHttpDate(millis), false);
		return _this();
	}

	/**
	 * Returns collection of all header names. Depends on
	 * {@link #capitalizeHeaderKeys()} flag.
	 */
	public Collection<String> headerNames() {
		return headers.names();
	}

	/**
	 * Returns Bearer token or {@code null} if not set.
	 */
	public String tokenAuthentication() {
		final String value = headers.get(HEADER_AUTHORIZATION);
		if (value == null) {
			return null;
		}
		final int ndx = value.indexOf("Bearer ");
		if (ndx == -1) {
			return null;
		}
		return value.substring(ndx + 7).trim();
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
	public T charset(final String charset) {
		this.charset = null;
		contentType(null, charset);
		return _this();
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
	public T mediaType(final String mediaType) {
		contentType(mediaType, null);
		return _this();
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
	public T contentType(final String contentType) {
		headerOverwrite(HEADER_CONTENT_TYPE, contentType);
		return _this();
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

		_headerRaw(HEADER_CONTENT_TYPE, contentType, true);
		return _this();
	}

	// ---------------------------------------------------------------- keep-alive

	/**
	 * Defines "Connection" header as "Keep-Alive" or "Close".
	 * Existing value is overwritten.
	 */
	public T connectionKeepAlive(final boolean keepAlive) {
		if (keepAlive) {
			headerOverwrite(HEADER_CONNECTION, HEADER_KEEP_ALIVE);
		} else {
			headerOverwrite(HEADER_CONNECTION, HEADER_CLOSE);
		}
		return _this();
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
	 * <code>null</code> if not set. Returned value is raw and unchecked, exactly the same
	 * as it was specified or received. It may be even invalid.
	 */
	public String contentLength() {
		return header(HEADER_CONTENT_LENGTH);
	}

	/**
	 * Sets the full "Content-Length" header.
	 */
	public T contentLength(final int value) {
		_headerRaw(HEADER_CONTENT_LENGTH, String.valueOf(value), true);
		return _this();
	}

	/**
	 * Returns "Content-Encoding" header.
	 */
	public String contentEncoding() {
		return header(HEADER_CONTENT_ENCODING);
	}

	/**
	 * Returns "Accept" header.
	 */
	public String accept() {
		return header(HEADER_ACCEPT);
	}

	/**
	 * Sets "Accept" header.
	 */
	public T accept(final String encodings) {
		headerOverwrite(HEADER_ACCEPT, encodings);
		return _this();
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
	public T acceptEncoding(final String encodings) {
		headerOverwrite(HEADER_ACCEPT_ENCODING, encodings);
		return _this();
	}

	// ---------------------------------------------------------------- form

	/**
	 * Initializes form.
	 */
	protected void initForm() {
		if (form == null) {
			form = HttpMultiMap.newCaseInsensitiveMap();
		}
	}

	/**
	 * Wraps non-Strings form values with {@link jodd.http.up.Uploadable uploadable content}.
	 * Detects invalid types and throws an exception. So all uploadable values
	 * are of the same type.
	 */
	protected Object wrapFormValue(final Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof CharSequence) {
			return value.toString();
		}
		if (value instanceof Number) {
			return value.toString();
		}
		if (value instanceof Boolean) {
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
	public T form(final String name, Object value) {
		initForm();

		value = wrapFormValue(value);
		((HttpMultiMap<Object>)form).add(name, value);

		return _this();
	}

	/**
	 * Sets form parameter by overwriting.
	 */
	public T formOverwrite(final String name, Object value) {
		initForm();

		value = wrapFormValue(value);
		((HttpMultiMap<Object>)form).set(name, value);

		return _this();
	}

	/**
	 * Sets many form parameters at once.
	 */
	public T form(String name, final Object value, final Object... parameters) {
		initForm();

		form(name, value);

		for (int i = 0; i < parameters.length; i += 2) {
			name = parameters[i].toString();

			form(name, parameters[i + 1]);
		}
		return _this();
	}

	/**
	 * Sets many form parameters at once.
	 */
	public T form(final Map<String, Object> formMap) {
		initForm();

		for (Map.Entry<String, Object> entry : formMap.entrySet()) {
			form(entry.getKey(), entry.getValue());
		}
		return _this();
	}

	/**
	 * Return map of form parameters.
	 * Note that all uploadable values are wrapped with {@link jodd.http.up.Uploadable}.
	 */
	public HttpMultiMap<?> form() {
		return form;
	}

	// ---------------------------------------------------------------- form encoding

	protected String formEncoding = Defaults.formEncoding;

	/**
	 * Defines encoding for forms parameters. Default value is
	 * copied from {@link Defaults#formEncoding}.
	 * It is overridden by {@link #charset() charset} value.
	 */
	public T formEncoding(final String encoding) {
		this.formEncoding = encoding;
		return _this();
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
	 * Returns <b>raw</b> body bytes. Returns <code>null</code> if body is not specified.
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
	 * the same raw body content is returned. Never returns <code>null</code>.
	 */
	public String bodyText() {
		if (body == null) {
			return StringPool.EMPTY;
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
	public T body(final String body) {
		this.body = body;
		this.form = null;
		contentLength(body.length());
		return _this();
	}

	/**
	 * Defines body text and content type (as media type and charset).
	 * Body string will be converted to {@link #body(String) raw body string}
	 * and "Content-Type" header will be set.
	 */
	public T bodyText(String body, final String mediaType, final String charset) {
		body = StringUtil.convertCharset(body, charset, StringPool.ISO_8859_1);
		contentType(mediaType, charset);
		body(body);
		return _this();
	}

	/**
	 * Defines {@link #bodyText(String, String, String) body text content}
	 * that will be encoded in {@link Defaults#bodyEncoding default body encoding}.
	 */
	public T bodyText(final String body, final String mediaType) {
		return bodyText(body, mediaType, charset != null ? charset : Defaults.bodyEncoding);
	}
	/**
	 * Defines {@link #bodyText(String, String, String) body text content}
	 * that will be encoded as {@link Defaults#bodyMediaType default body media type}
	 * in {@link Defaults#bodyEncoding default body encoding} if missing.
	 */
	public T bodyText(final String body) {
		return bodyText(
			body,
			mediaType != null ? mediaType : Defaults.bodyMediaType,
			charset != null ? charset : Defaults.bodyEncoding);
	}

	/**
	 * Sets <b>raw</b> body content and discards form parameters.
	 * Also sets "Content-Length" and "Content-Type" parameter.
	 * @see #body(String)
	 */
	public T body(final byte[] content, final String contentType) {
		String body = null;
		try {
			body = new String(content, StringPool.ISO_8859_1);
		} catch (UnsupportedEncodingException ignore) {
		}
		contentType(contentType);
		return body(body);
	}

	// ---------------------------------------------------------------- body form

	protected boolean multipart = false;

	/**
	 * Returns <code>true</code> if form contains {@link jodd.http.up.Uploadable}.
	 */
	protected boolean isFormMultipart() {
		if (multipart) {
			return true;
		}

		for (Map.Entry<String, ?> entry : form) {
			Object value = entry.getValue();
			if (value instanceof Uploadable) {
				return true;
			}
		}

	    return false;
	}

	/**
	 * Creates form {@link jodd.http.Buffer buffer} and sets few headers.
	 */
	protected Buffer formBuffer() {
		Buffer buffer = new Buffer();
		if (form == null || form.isEmpty()) {
			return buffer;
		}

		if (!isFormMultipart()) {
			String formEncoding = resolveFormEncoding();

			// encode
			String formQueryString = HttpUtil.buildQuery(form, formEncoding);

			contentType("application/x-www-form-urlencoded", null);
			contentLength(formQueryString.length());

			buffer.append(formQueryString);
			return buffer;
		}

		String boundary = StringUtil.repeat('-', 10) + RandomString.get().randomAlphaNumeric(10);

		for (Map.Entry<String, ?> entry : form) {

			buffer.append("--");
			buffer.append(boundary);
			buffer.append(CRLF);

			String name = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof String) {
				String string = (String) value;
				buffer.append("Content-Disposition: form-data; name=\"").append(name).append('"').append(CRLF);
				buffer.append(CRLF);

				String formEncoding = resolveFormEncoding();

				String utf8String = StringUtil.convertCharset(
					string, formEncoding, StringPool.ISO_8859_1);

				buffer.append(utf8String);
			}
			else if (value instanceof Uploadable) {
				Uploadable uploadable = (Uploadable) value;

				String fileName = uploadable.getFileName();
				if (fileName == null) {
					fileName = name;
				} else {
					String formEncoding = resolveFormEncoding();

					fileName = StringUtil.convertCharset(
						fileName, formEncoding, StringPool.ISO_8859_1);
				}

				buffer.append("Content-Disposition: form-data; name=\"").append(name);
				buffer.append("\"; filename=\"").append(fileName).append('"').append(CRLF);

				String mimeType = uploadable.getMimeType();
				if (mimeType == null) {
					mimeType = MimeTypes.getMimeType(FileNameUtil.getExtension(fileName));
				}
				buffer.append(HEADER_CONTENT_TYPE).append(": ").append(mimeType).append(CRLF);

				buffer.append("Content-Transfer-Encoding: binary").append(CRLF);
				buffer.append(CRLF);

				buffer.append(uploadable);

				//byte[] bytes = uploadable.getBytes();
				//for (byte b : bytes) {
					//buffer.append(CharUtil.toChar(b));
				//}
			} else {
				// should never happened!
				throw new HttpException("Unsupported type");
			}
			buffer.append(CRLF);
		}

		buffer.append("--").append(boundary).append("--");
		buffer.append(CRLF);

		// the end
		contentType("multipart/form-data; boundary=" + boundary);
		contentLength(buffer.size());

		return buffer;
	}

	/**
	 * Resolves form encodings.
	 */
	protected String resolveFormEncoding() {
		// determine form encoding
		String formEncoding = charset;

		if (formEncoding == null) {
			formEncoding = this.formEncoding;
		}
		return formEncoding;
	}

	// ---------------------------------------------------------------- buffer

	/**
	 * Returns string representation of this request or response.
	 */
	@Override
	public String toString() {
		return toString(true);
	}

	/**
	 * Returns full request/response, or just headers.
	 * Useful for debugging.
	 */
	public String toString(final boolean fullResponse) {
		Buffer buffer = buffer(fullResponse);

		StringWriter stringWriter = new StringWriter();

		try {
			buffer.writeTo(stringWriter);
		}
		catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		return stringWriter.toString();
	}

	/**
	 * Returns byte array of request or response.
	 */
	public byte[] toByteArray() {
		Buffer buffer = buffer(true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.size());

		try {
			buffer.writeTo(baos);
		}
		catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		return baos.toByteArray();
	}

	/**
	 * Creates {@link jodd.http.Buffer buffer} ready to be consumed.
	 * Buffer can, optionally, contains just headers.
	 */
	protected abstract Buffer buffer(boolean full);

	protected void populateHeaderAndBody(final Buffer target, final Buffer formBuffer, final boolean fullRequest) {
		for (String name : headers.names()) {
			List<String> values = headers.getAll(name);

			String key = capitalizeHeaderKeys ? HttpUtil.prepareHeaderParameterName(name) : name;

			target.append(key);
			target.append(": ");
			int count = 0;

			for (String value : values) {
				if (count++ > 0) {
					target.append(", ");
				}
				target.append(value);
			}

			target.append(CRLF);
		}

		if (fullRequest) {
			target.append(CRLF);

			if (form != null) {
				target.append(formBuffer);
			} else if (body != null) {
				target.append(body);
			}
		}
	}


	// ---------------------------------------------------------------- send

	protected HttpProgressListener httpProgressListener;

	/**
	 * Sends request or response to output stream.
	 */
	public void sendTo(final OutputStream out) throws IOException {
		Buffer buffer = buffer(true);

		if (httpProgressListener == null) {
			buffer.writeTo(out);
		}
		else {
			buffer.writeTo(out, httpProgressListener);
		}

		out.flush();
	}

	// ---------------------------------------------------------------- parsing

	/**
	 * Parses headers.
	 */
	protected void readHeaders(final BufferedReader reader) {
		while (true) {
			final String line;
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
	protected void readBody(final BufferedReader reader) {
		String bodyString = null;

		// first determine if chunked encoding is specified
		boolean isChunked = false;

		String transferEncoding = header("Transfer-Encoding");
		if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked")) {
			isChunked = true;
		}


		// content length
		String contentLen = contentLength();
		int contentLenValue = -1;

		if (contentLen != null && !isChunked) {
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
		if (isChunked) {

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
			form = HttpMultiMap.newCaseInsensitiveMap();

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

				for (String value : values) {
					((HttpMultiMap<Object>)form).add(paramName, value);
				}
			}

			// file parameters
			for (String paramName : multipartParser.getFileParameterNames()) {
				FileUpload[] uploads = multipartParser.getFiles(paramName);

				for (FileUpload upload : uploads) {
					((HttpMultiMap<Object>)form).add(paramName, upload);
				}
			}

			return;
		}

		// body is a simple content

		form = null;
	}

}
