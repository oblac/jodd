// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.datetime.TimeUtil;
import jodd.io.FastCharArrayWriter;
import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.upload.FileUpload;
import jodd.upload.MultipartStreamParser;
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
import java.util.LinkedHashMap;
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

	protected String httpVersion = "HTTP/1.1";
	protected Map<String, String> headers = new LinkedHashMap<String, String>();

	protected HttpParamsMap form;	// holds form data (when used)
	protected String body;			// holds body string (always)

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
	 */
	public String header(String name) {
		String key = name.trim().toLowerCase();

		return headers.get(key);
	}

	/**
	 * Removes header parameter.
	 */
	public void removeHeader(String name) {
		String key = name.trim().toLowerCase();

		headers.remove(key);
	}

	/**
	 * Sets header parameter. Existing parameter is overwritten.
	 * The order of header parameters is preserved.
	 */
	public T header(String name, String value) {
		String key = name.trim().toLowerCase();

		headers.put(key, value.trim());
		return (T) this;
	}

	/**
	 * Sets <code>int</code> value as header parameter,
	 * @see #header(String, String)
	 */
	public T header(String name, int value) {
		header(name, String.valueOf(value));
		return (T) this;
	}

	/**
	 * Sets date value as header parameter.
	 * @see #header(String, String)
	 */
	public T header(String name, long millis) {
		header(name, TimeUtil.formatHttpDate(millis));
		return (T) this;
	}

	// ---------------------------------------------------------------- common headers

	/**
	 * Returns "Content-Type" header.
	 */
	public String contentType() {
		return header(HEADER_CONTENT_TYPE);
	}

	/**
	 * Sets "Content-Type" header.
	 */
	public T contentType(String contentType) {
		header(HEADER_CONTENT_TYPE, contentType);
		return (T) this;
	}

	/**
	 * Returns "Content-Length" header.
	 */
	public String contentLength() {
		return header(HEADER_CONTENT_LENGTH);
	}

	/**
	 * Sets the "Content-Length" header.
	 */
	public T contentLength(int value) {
		header(HEADER_CONTENT_LENGTH, value);
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
		header(HEADER_ACCEPT_ENCODING, encodings);
		return (T) this;
	}

	// ---------------------------------------------------------------- form

	protected void initForm() {
		if (form == null) {
			form = new HttpParamsMap();
		}
	}

	/**
	 * Sets the form parameter.
	 */
	public T form(String name, Object value) {
		initForm();
		form.put(name, value);
		return (T) this;
	}

	/**
	 * Sets many form parameters at once.
	 */
	public T form(String name, Object value, Object... parameters) {
		initForm();
		if (form == null) {
			form = new HttpParamsMap();
		}

		form.put(name, value);
		for (int i = 0; i < parameters.length; i += 2) {
			name = parameters[i].toString();

			form.put(name, parameters[i + 1]);
		}
		return (T) this;
	}

	/**
	 * Return map of form parameters.
	 */
	public Map<String, Object> form() {
		return form;
	}

	/**
	 * Returns body as received or set. Any form parameter change
	 * will NOT be reflected here until sending the request.
	 */
	public String body() {
		return body;
	}

	/**
	 * Sets complete body and discards all form parameters.
	 * Also sets "Content-Length" parameter.
	 */
	public T body(String body) {
		this.body = body;
		this.form = null;
		contentLength(body.length());
		return (T) this;
	}

	// ---------------------------------------------------------------- body form

	/**
	 * Returns <code>true</code> if form contains non-string elements (i.e. files).
	 */
	protected boolean isFormMultipart() {
		for (Object o : form.values()) {
			Class type = o.getClass();

			if (type.equals(String.class) || type.equals(String[].class)) {
				continue;
			}
			return true;
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
			String queryString = HttpUtil.buildQuery(form);

			contentType("application/x-www-form-urlencoded");
			contentLength(queryString.length());

			return queryString;
		}

		String boundary = StringUtil.repeat('-', 10) + RandomStringUtil.randomAlphaNumeric(10);

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Object> entry : form.entrySet()) {

			sb.append("--");
			sb.append(boundary);
			sb.append(CRLF);

			String name = entry.getKey();
			Object value =  entry.getValue();
			Class type = value.getClass();

			if (type == String.class) {
				sb.append("Content-Disposition: form-data; name=\"").append(name).append('"').append(CRLF);
				sb.append(CRLF);
				sb.append(value);
			} else if (type == String[].class) {
				String[] array = (String[]) value;
				for (String v : array) {
					sb.append("Content-Disposition: form-data; name=\"").append(name).append('"').append(CRLF);
					sb.append(CRLF);
					sb.append(v);
				}
			} else if (type == File.class) {
				File file = (File) value;
				String fileName = FileNameUtil.getName(file.getName());

				sb.append("Content-Disposition: form-data; name=\"").append(name);
				sb.append("\"; filename=\"").append(fileName).append('"').append(CRLF);

				String mimeType = MimeTypes.getMimeType(FileNameUtil.getExtension(fileName));
				sb.append("Content-Type: ").append(mimeType).append(CRLF);
				sb.append("Content-Transfer-Encoding: binary").append(CRLF);
				sb.append(CRLF);

				try {
					char[] chars = FileUtil.readChars(file, StringPool.ISO_8859_1);
					sb.append(chars);
				} catch (IOException ioex) {
					throw new HttpException(ioex);
				}
			} else {
				throw new HttpException("Unsupported parameter type: " + type.getName());
			}
			sb.append(CRLF);
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
		if (contentLen != null) {
			int len = Integer.parseInt(contentLen);

			FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter(len);

			try {
				StreamUtil.copy(reader, fastCharArrayWriter, len);
			} catch (IOException ioex) {
				throw new HttpException(ioex);
			}

			bodyString = fastCharArrayWriter.toString();
		}

		// chunked encoding
		String transferEncoding = header("Transfer-Encoding");
		if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked")) {

			FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter();
			try {
				while (true) {
					String line = reader.readLine();

					if (StringUtil.isBlank(line)) {
						break;
					}

					int len = Integer.parseInt(line, 16);

					if (len != 0) {
						StreamUtil.copy(reader, fastCharArrayWriter, len);
						reader.readLine();
					}
				}
			} catch (IOException ioex) {
				throw new HttpException(ioex);
			}

			bodyString = fastCharArrayWriter.toString();
		}

		// no body
		if (bodyString == null) {

			if (httpVersion().equals("HTTP/1.0")) {
				// in HTTP 1.0 body ends when stream closes
				FastCharArrayWriter fastCharArrayWriter = new FastCharArrayWriter();
				try {
					StreamUtil.copy(reader, fastCharArrayWriter);
				} catch (IOException ioex) {
					throw new HttpException(ioex);
				}
				bodyString = fastCharArrayWriter.toString();
			} else {
				body = null;
				return;
			}
		}

		// PARSE BODY
		body = bodyString;

		String contentType = contentType();

		if (contentType == null) {
			contentType = StringPool.EMPTY;
		} else {
			contentType = contentType.toLowerCase();
		}

		if (contentType.equals("application/x-www-form-urlencoded")) {
			form = HttpUtil.parseQuery(bodyString, true);
			return;
		}

		if (contentType.startsWith("multipart/form-data")) {
			form = new HttpParamsMap();

			MultipartStreamParser multipartParser = new MultipartStreamParser();

			try {
				byte[] bodyBytes = bodyString.getBytes(StringPool.ISO_8859_1);
				ByteArrayInputStream bin = new ByteArrayInputStream(bodyBytes);
				multipartParser.parseRequestStream(bin, StringPool.ISO_8859_1);
			} catch (IOException ioex) {
				throw new HttpException(ioex);
			}

			// string parameters
			for (String paramName : multipartParser.getParameterNames()) {
				String[] values = multipartParser.getParameterValues(paramName);
				if (values.length == 1) {
					form.put(paramName, values[0]);
				} else {
					form.put(paramName, values);
				}
			}

			// file parameters
			for (String paramName : multipartParser.getFileParameterNames()) {
				FileUpload[] values = multipartParser.getFiles(paramName);
				if (values.length == 1) {
					form.put(paramName, values[0]);
				} else {
					form.put(paramName, values);
				}
			}

			return;
		}

		form = null;
	}

}