// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.JoddHttp;
import jodd.util.Base64;
import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static jodd.util.StringPool.CRLF;
import static jodd.util.StringPool.SPACE;

/**
 * HTTP request.
 */
public class HttpRequest extends HttpBase<HttpRequest> {

	protected String protocol = "http";
	protected String host = "localhost";
	protected int port = 80;
	protected String method = "GET";
	protected String path = StringPool.SLASH;
	protected HttpParamsMap query;

	// ---------------------------------------------------------------- properties

	/**
	 * Returns request host name.
	 */
	public String host() {
		return host;
	}

	/**
	 * Sets request host name.
	 */
	public HttpRequest host(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Returns used protocol. By default it's "http".
	 */
	public String protocol() {
		return protocol;
	}

	/**
	 * Defines protocol.
	 */
	public HttpRequest protocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	/**
	 * Returns request port number.
	 */
	public int port() {
		return port;
	}

	/**
	 * Sets request port number.
	 */
	public HttpRequest port(int port) {
		this.port = port;
		return this;
	}

	// ---------------------------------------------------------------- set

	/**
	 * Sets the destination (method, host, port... ) at once.
	 */
	public HttpRequest set(String destination) {
		destination = destination.trim();

		// http method

		int ndx = destination.indexOf(' ');

		if (ndx != -1) {
			method = destination.substring(0, ndx).toUpperCase();
			destination = destination.substring(ndx + 1);
		}

		// protocol

		ndx = destination.indexOf("://");

		if (ndx != -1) {
			protocol = destination.substring(0, ndx);
			destination = destination.substring(ndx + 3);
		}

		// host

		ndx = destination.indexOf('/');

		if (ndx == -1) {
			ndx = destination.length();
		}

		if (ndx != 0) {

			host = destination.substring(0, ndx);
			destination = destination.substring(ndx);

			// port

			ndx = host.indexOf(':');

			if (ndx == -1) {
				port = 80;
			} else {
				port = Integer.parseInt(host.substring(ndx + 1));
				host = host.substring(0, ndx);
			}
		}

		// path + query

		path(destination);

		return this;
	}

	// ---------------------------------------------------------------- static factories

	/**
	 * Builds a GET request.
	 */
	public static HttpRequest get(String destination) {
		return new HttpRequest()
				.method("GET")
				.set(destination);
	}
	/**
	 * Builds a POST request.
	 */
	public static HttpRequest post(String destination) {
		return new HttpRequest()
				.method("POST")
				.set(destination);
	}
	/**
	 * Builds a PUT request.
	 */
	public static HttpRequest put(String destination) {
		return new HttpRequest()
				.method("PUT")
				.set(destination);
	}
	/**
	 * Builds a DELETE request.
	 */
	public static HttpRequest delete(String destination) {
		return new HttpRequest()
				.method("DELETE")
				.set(destination);
	}
	/**
	 * Builds a HEAD request.
	 */
	public static HttpRequest head(String destination) {
		return new HttpRequest()
				.method("HEAD")
				.set(destination);
	}
	/**
	 * Builds a TRACE request.
	 */
	public static HttpRequest trace(String destination) {
		return new HttpRequest()
				.method("TRACE")
				.set(destination);
	}
	/**
	 * Builds an OPTIONS request.
	 */
	public static HttpRequest options(String destination) {
		return new HttpRequest()
				.method("OPTIONS")
				.set(destination);
	}

	// ---------------------------------------------------------------- request

	/**
	 * Returns request method.
	 */
	public String method() {
		return method;
	}

	/**
	 * Specifies request method. It will be converted into uppercase.
	 */
	public HttpRequest method(String method) {
		this.method = method.toUpperCase();
		return this;
	}

	/**
	 * Returns request path, without the query.
	 */
	public String path() {
		return path;
	}

	/**
	 * Sets request path. Query string is allowed.
	 * Adds a slash if path doesn't start with one.
	 * Query will be stripped out from the path.
	 * Previous query is discarded.
	 * @see #query()
	 */
	public HttpRequest path(String path) {
		// this must be the only place that sets the path

		if (path.startsWith(StringPool.SLASH) == false) {
			path = StringPool.SLASH + path;
		}

		int ndx = path.indexOf('?');

		if (ndx != -1) {
			String queryString = path.substring(ndx + 1);

			path = path.substring(0, ndx);

			query = HttpUtil.parseQuery(queryString, true);
		} else {
			query = new HttpParamsMap();
		}

		this.path = path;

		return this;
	}

	// ---------------------------------------------------------------- query

	/**
	 * Adds query parameter.
	 */
	public HttpRequest query(String name, String value) {
		query.put(name, value);
		return this;
	}

	/**
	 * Adds many query parameters at once. Although it accepts objects,
	 * each value will be converted to string.
	 */
	public HttpRequest query(String name1, Object value1, Object... parameters) {
		query(name1, value1 == null ? null : value1.toString());

		for (int i = 0; i < parameters.length; i += 2) {
			String name = parameters[i].toString();

			String value = parameters[i + 1].toString();
			query.put(name, value == null ? null : value);
		}
		return this;
	}

	/**
	 * Adds all parameters from the provided map.
	 */
	public HttpRequest query(Map<String, String> queryMap) {
		for (Map.Entry<String, String> entry : queryMap.entrySet()) {
			query.put(entry.getKey(), entry.getValue());
		}
		return this;
	}

	/**
	 * Returns backend map of query parameters. Values can be <code>null</code>,
	 * <code>String</code> or <code>String[]</code>.
	 */
	public Map<String, Object> query() {
		return query;
	}

	/**
	 * Clears all query parameters.
	 */
	public HttpRequest clearQueries() {
		query.clear();
		return this;
	}

	/**
	 * Removes query parameters.
	 */
	public HttpRequest removeQuery(String name) {
		query.remove(name);
		return this;
	}

	// ---------------------------------------------------------------- queryString

	/**
	 * @see #queryString(String, boolean)
	 */
	public HttpRequest queryString(String queryString) {
		return queryString(queryString, true);
	}

	/**
	 * Sets query from provided query string. Previous query values
	 * are discarded.
	 */
	public HttpRequest queryString(String queryString, boolean decode) {
		this.query = HttpUtil.parseQuery(queryString, decode);
		return this;
	}

	/**
	 * Generates query string. All values are URL encoded.
	 */
	public String queryString() {
		if (query == null) {
			return StringPool.EMPTY;
		}
		return HttpUtil.buildQuery(query, queryEncoding);
	}

	// ---------------------------------------------------------------- query encoding

	protected String queryEncoding = JoddHttp.defaultQueryEncoding;

	/**
	 * Defines encoding for query parameters. Default value is
	 * copied from {@link JoddHttp#defaultQueryEncoding}.
	 */
	public HttpRequest queryEncoding(String encoding) {
		this.queryEncoding = encoding;
		return this;
	}

	// ---------------------------------------------------------------- full path

	/**
	 * Returns full URL path.
	 * Simply concatenates {@link #protocol(String) protocol}, {@link #host(String) host},
	 * {@link #port(int) port}, {@link #path(String) path} and {@link #queryString(String) query string}.
	 */
	public String url() {
		StringBand url = new StringBand(8);

		if (protocol != null) {
			url.append(protocol);
			url.append("://");
		}

		if (host != null) {
			url.append(host);
		}

		if (port != 80) {
			url.append(':');
			url.append(port);
		}

		if (path != null) {
			url.append(path);
		}

		String queryString = queryString();

		if (StringUtil.isNotBlank(queryString)) {
			url.append('?');
			url.append(queryString);
		}

		return url.toString();
	}

	// ---------------------------------------------------------------- auth

	/**
	 * Enables basic authentication by adding required header.
	 */
	public HttpRequest basicAuthentication(String username, String password) {
		String data = username.concat(StringPool.COLON).concat(password);

		String base64 = Base64.encodeToString(data);

		header("Authorization", "Basic " + base64);

		return this;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Sets 'Host' header from current host and port.
	 */
	public HttpRequest setHostHeader() {
		String hostPort = this.host;

		if (port != 80) {
			hostPort += StringPool.COLON + port;
		}

		header(HEADER_HOST, hostPort);
		return this;
	}

	// ---------------------------------------------------------------- send

	protected HttpTransport httpTransport;

	/**
	 * Opens transport i.e. connection. Returns used {@link HttpTransport} implementation.
	 */
	public HttpTransport open() {
		httpTransport = new HttpTransport();

		try {
			httpTransport.open(this);
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		return httpTransport;
	}

	/**
	 * Opens request if not already open, sends request, reads response and closes the request.
	 */
	public HttpResponse send() {
		if (httpTransport == null) {
			open();
		}

		HttpResponse httpResponse;
		try {
			httpResponse = httpTransport.send();
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		httpTransport.close();

		return httpResponse;
	}

	// ---------------------------------------------------------------- toString

	/**
	 * Returns string representation of the HTTP request.
	 * Important: some initialization is done here as well, before
	 * resulting string is created.
	 */
	public String toString() {

		// INITIALIZATION

		// host port

		if (header(HEADER_HOST) == null) {
			setHostHeader();
		}

		// form

		String formString = formString();

		// query string

		String queryString = queryString();

		// user-agent

		if (header("User-Agent") == null) {
			header("User-Agent", "Jodd HTTP");
		}

		// POST method requires Content-Type to be set

		if (method.equals("POST") && (contentLength() == null)) {
			contentLength(0);
		}


		// BUILD OUT

		StringBuilder builder = new StringBuilder();

		builder.append(method)
			.append(SPACE)
			.append(path);

		if (query != null && !query.isEmpty()) {
			builder.append('?');
			builder.append(queryString);
		}

		builder.append(SPACE)
			.append(httpVersion)
			.append(CRLF);

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			String headerName = entry.getKey();

			headerName = HttpUtil.prepareHeaderParameterName(headerName);

			builder.append(headerName);
			builder.append(": ");
			builder.append(entry.getValue());
			builder.append(CRLF);
		}

		builder.append(CRLF);

		if (form != null) {
			builder.append(formString);
		} else if (body != null) {
			builder.append(body);
		}

		return builder.toString();
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses input stream and creates new <code>HttpRequest</code> object.
	 */
	public static HttpRequest readFrom(InputStream in) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in, StringPool.ISO_8859_1));
		} catch (UnsupportedEncodingException uneex) {
			return null;
		}

		HttpRequest httpRequest = new HttpRequest();

		String line;
		try {
			line = reader.readLine();
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		if (!StringUtil.isBlank(line)) {
			String[] s = StringUtil.splitc(line, ' ');

			httpRequest.method(s[0]);
			httpRequest.path(s[1]);
			httpRequest.httpVersion(s[2]);

			httpRequest.readHeaders(reader);
			httpRequest.readBody(reader);
		}

		return httpRequest;
	}

}