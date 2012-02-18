// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.http;

import jodd.io.FastByteArrayOutputStream;
import jodd.io.StreamUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP util class for creating request and response {@link HttpTransfer}.
 */
public class Http {

	// ---------------------------------------------------------------- request

	/**
	 * Creates request {@link HttpTransfer}.
	 * @see #createRequest(String, String, int, String)
	 */
	public static HttpTransfer createRequest(String method, String fullUrl) throws IOException {
		URL url = new URL(fullUrl);
		String path = url.getPath();

		String query = url.getQuery();
		if (query != null) {
			path = url.getPath() + '?' + query;
		}

		int port = url.getPort();
		if (port == -1) {
			port = 80;
		}

		String host = url.getHost();

		return createRequest(method, host, port, path);
	}

	/**
	 * Creates request {@link HttpTransfer}. Also sets the "Hosts" header.
	 */
	public static HttpTransfer createRequest(String method, String hostName, int port, String path) {
		HttpTransfer httpTransfer = new HttpTransfer();

		httpTransfer.setMethod(method);
		httpTransfer.setPath(path);

		httpTransfer.setHost(hostName);
		httpTransfer.setPort(port);

		httpTransfer.addHeader("Host", hostName);
		return httpTransfer;
	}

	/**
	 * Reads {@link HttpTransfer} from incoming request.
	 */
	public static HttpTransfer readRequest(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, StringPool.ISO_8859_1));

		HttpTransfer httpTransfer = new HttpTransfer();

		String line = reader.readLine();
		String[] s = StringUtil.splitc(line, ' ');

		httpTransfer.setMethod(s[0]);
		httpTransfer.setPath(s[1]);
		httpTransfer.setHttpVersion(s[2]);

		readHeaders(httpTransfer, reader);
		readBody(httpTransfer, reader);

		return httpTransfer;
	}

	// ---------------------------------------------------------------- response

	/**
	 * Creates simple response {@link HttpTransfer}.
	 */
	public static HttpTransfer createResponse(int statusCode, String statusPhrase) {
		HttpTransfer httpTransfer = new HttpTransfer();

		httpTransfer.setStatusCode(statusCode);
		httpTransfer.setStatusPhrase(statusPhrase);

		return httpTransfer;
	}

	/**
	 * Reads response input stream and returns response {@link HttpTransfer}.
	 * Supports both streamed and chunked response.
	 */
	public static HttpTransfer readResponse(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, StringPool.ISO_8859_1));

		HttpTransfer httpTransfer = new HttpTransfer();

		// the first line
		String line = reader.readLine().trim();

		int ndx = line.indexOf(' ');
		httpTransfer.setHttpVersion(line.substring(0, ndx));

		int ndx2 = line.indexOf(' ', ndx + 1);
		httpTransfer.setStatusCode(Integer.parseInt(line.substring(ndx, ndx2).trim()));

		httpTransfer.setStatusPhrase(line.substring(ndx2).trim());

		readHeaders(httpTransfer, reader);
		readBody(httpTransfer, reader);

		return httpTransfer;
	}

	/**
	 * Reads response from HTTP URL connection.
	 */
	public static HttpTransfer readResponse(HttpURLConnection huc) throws IOException {
		HttpTransfer httpTransfer = new HttpTransfer();

		// the first line
		String line = huc.getHeaderField(0);

		int ndx = line.indexOf(' ');
		httpTransfer.setHttpVersion(line.substring(0, ndx));

		int ndx2 = line.indexOf(' ', ndx + 1);
		httpTransfer.setStatusCode(Integer.parseInt(line.substring(ndx, ndx2).trim()));

		httpTransfer.setStatusPhrase(line.substring(ndx2).trim());

		int count = 1;
		while (true) {
			String key = huc.getHeaderFieldKey(count);
			if (key == null) {
				break;
			}
			String value = huc.getHeaderField(count);

			httpTransfer.addHeader(key, value);

			count++;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream(), StringPool.ISO_8859_1));
		readBody(httpTransfer, reader);

		return httpTransfer;
	}


	protected static void readHeaders(HttpTransfer httpTransfer, BufferedReader reader) throws IOException {
		while (true) {
			String line = reader.readLine();
			if (StringUtil.isBlank(line)) {
				break;
			}

			int ndx = line.indexOf(':');
			if (ndx != -1) {
				httpTransfer.addHeader(line.substring(0, ndx), line.substring(ndx + 1));
			} else {
				throw new IOException("Invalid header " + line);
			}
		}
	}

	protected static void readBody(HttpTransfer httpTransfer, BufferedReader reader) throws IOException {
		// content length
		String contentLen = httpTransfer.getHeader("Content-Length");
		if (contentLen != null) {
			int len = Integer.parseInt(contentLen);

			FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream(len);
			StreamUtil.copy(reader, fbaos, StringPool.ISO_8859_1, len);

			httpTransfer.setBody(fbaos.toByteArray());
		}

		// chunked encoding
		String transferEncoding = httpTransfer.getHeader("Transfer-Encoding");
		if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked")) {

			FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();

			while (true) {
				String line = reader.readLine();
				if (StringUtil.isBlank(line)) {
					break;
				}

				int len = Integer.parseInt(line, 16);

				if (len != 0) {
					StreamUtil.copy(reader, fbaos, StringPool.ISO_8859_1, len);
					reader.readLine();
				}
			}

			httpTransfer.setBody(fbaos.toByteArray());
		}
	}

}