// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.io.StreamUtil;
import jodd.io.StringInputStream;
import jodd.io.StringOutputStream;
import jodd.util.StringPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static jodd.util.StringPool.CRLF;
import static jodd.util.StringPool.SPACE;

/**
 * HTTP response.
 */
public class HttpResponse extends HttpBase<HttpResponse> {

	protected int statusCode;
	protected String statusPhrase;

	/**
	 * Returns response status code.
	 */
	public int statusCode() {
		return statusCode;
	}

	/**
	 * Sets response status code.
	 */
	public HttpResponse statusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	/**
	 * Returns response status phrase.
	 */
	public String statusPhrase() {
		return statusPhrase;
	}

	/**
	 * Sets response status phrase.
	 */
	public HttpResponse statusPhrase(String statusPhrase) {
		this.statusPhrase = statusPhrase;
		return this;
	}

	// ---------------------------------------------------------------- body

	/**
	 * Unzips GZip-ed body content, removes the content-encoding header
	 * and sets the new content-length value.
	 */
	public HttpResponse unzip() {
		String contentEncoding = contentEncoding();

		if (contentEncoding != null && contentEncoding().equals("gzip")) {
			if (body != null) {
				removeHeader(HEADER_CONTENT_ENCODING);
				try {
					StringInputStream in = new StringInputStream(body, StringInputStream.Mode.STRIP);
					GZIPInputStream gzipInputStream = new GZIPInputStream(in);
					StringOutputStream out = new StringOutputStream();

					StreamUtil.copy(gzipInputStream, out);

					body(out.toString());
				} catch (IOException ioex) {
					throw new HttpException(ioex);
				}
			}
		}
		return this;
	}

	// ---------------------------------------------------------------- toString

	/**
	 * String representation of the HTTP response.
	 */
	public String toString() {
		// form

		String formString = formString();

		// response

		StringBuilder response = new StringBuilder();

		response.append(httpVersion)
			.append(SPACE)
			.append(statusCode)
			.append(SPACE)
			.append(statusPhrase)
			.append(CRLF);

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			String headerName = entry.getKey();

			headerName = HttpUtil.prepareHeaderParameterName(headerName);

			response.append(headerName);
			response.append(": ");
			response.append(entry.getValue());
			response.append(CRLF);
		}

		response.append(CRLF);

		if (form != null) {
			response.append(formString);
		} else if (body != null) {
			response.append(body);
		}

		return response.toString();
	}

	// ---------------------------------------------------------------- read from

	/**
	 * Reads response input stream and returns {@link HttpResponse response}.
	 * Supports both streamed and chunked response.
	 */
	public static HttpResponse readFrom(InputStream in) {
		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(in, StringPool.ISO_8859_1);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);

		HttpResponse httpResponse = new HttpResponse();

		// the first line
		String line;
		try {
			line = reader.readLine();
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		if (line != null) {

			line = line.trim();

			int ndx = line.indexOf(' ');
			httpResponse.httpVersion(line.substring(0, ndx));

			int ndx2 = line.indexOf(' ', ndx + 1);
			httpResponse.statusCode(Integer.parseInt(line.substring(ndx, ndx2).trim()));

			httpResponse.statusPhrase(line.substring(ndx2).trim());
		}

		httpResponse.readHeaders(reader);
		httpResponse.readBody(reader);

		return httpResponse;
	}

}