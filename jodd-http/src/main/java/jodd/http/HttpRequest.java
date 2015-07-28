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

import jodd.util.Base64;
import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static jodd.util.StringPool.CRLF;
import static jodd.util.StringPool.SPACE;

/**
 * HTTP request.
 */
public class HttpRequest extends HttpBase<HttpRequest> {

	private static final int DEFAULT_PORT = -1;

	protected String protocol = "http";
	protected String host = "localhost";
	protected int port = DEFAULT_PORT;
	protected String method = "GET";
	protected String path = StringPool.SLASH;
	protected HttpMultiMap<String> query;

	// ---------------------------------------------------------------- init

	public HttpRequest() {
		initRequest();
	}

	/**
	 * Prepares request on creation. By default, it just
	 * adds "Connection: Close" header.
	 */
	protected void initRequest() {
		connectionKeepAlive(false);
	}

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
	 * Returns request port number. When port is not
	 * explicitly defined, returns default port for
	 * current protocol.
	 */
	public int port() {
		if (port == DEFAULT_PORT) {
			if (protocol == null) {
				return 80;
			}
			if (protocol.equalsIgnoreCase("https")) {
				return 443;
			}
			return 80;
		}
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
				port = DEFAULT_PORT;
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
	 * Builds a CONNECT request.
	 */
	public static HttpRequest connect(String destination) {
		return new HttpRequest()
				.method("CONNECT")
				.set(destination);
	}
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
	 * Builds a PATCH request.
	 */
	public static HttpRequest patch(String destination) {
		return new HttpRequest()
				.method("PATCH")
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
			query = HttpMultiMap.newCaseInsensitveMap();
		}

		this.path = path;

		return this;
	}

	/**
	 * Forces multipart requests. When set to <code>false</code>,
	 * it will be {@link #isFormMultipart() detected} if request
	 * should be multipart. By setting this to <code>true</code>
	 * we are forcing usage of multipart request.
	 */
	public HttpRequest multipart(boolean multipart) {
		this.multipart = true;
		return this;
	}


	// ---------------------------------------------------------------- query

	/**
	 * Adds query parameter.
	 */
	public HttpRequest query(String name, String value) {
		query.add(name, value);
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
			query.add(name, value == null ? null : value);
		}
		return this;
	}

	/**
	 * Adds all parameters from the provided map.
	 */
	public HttpRequest query(Map<String, String> queryMap) {
		for (Map.Entry<String, String> entry : queryMap.entrySet()) {
			query.add(entry.getKey(), entry.getValue());
		}
		return this;
	}

	/**
	 * Returns backend map of query parameters.
	 */
	public HttpMultiMap<String> query() {
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
	 * Removes query parameters for given name.
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
		StringBuilder url = new StringBuilder();

		url.append(hostUrl());

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

	/**
	 * Returns just host url, without path and query.
	 */
	public String hostUrl() {
		StringBand url = new StringBand(8);

		if (protocol != null) {
			url.append(protocol);
			url.append("://");
		}

		if (host != null) {
			url.append(host);
		}

		if (port != DEFAULT_PORT) {
			url.append(':');
			url.append(port);
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

		header("Authorization", "Basic " + base64, true);

		return this;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Sets 'Host' header from current host and port.
	 */
	public HttpRequest setHostHeader() {
		String hostPort = this.host;

		if (port != DEFAULT_PORT) {
			hostPort += StringPool.COLON + port;
		}

		header(HEADER_HOST, hostPort, true);
		return this;
	}

	// ---------------------------------------------------------------- monitor

	/**
	 * Registers {@link jodd.http.HttpProgressListener listener} that will
	 * monitor upload progress. Be aware that the whole size of the
	 * request is being monitored, not only the files content.
	 */
	public HttpRequest monitor(HttpProgressListener httpProgressListener) {
		this.httpProgressListener = httpProgressListener;
		return this;
	}

	// ---------------------------------------------------------------- connection properties

	protected int timeout = -1;

	/**
	 * Defines connection timeout in milliseconds.
	 * @see jodd.http.HttpConnection#setTimeout(int)
	 */
	public HttpRequest timeout(int milliseconds) {
		this.timeout = milliseconds;
		return this;
	}

	/**
	 * Returns timeout in milliseconds. Negative value means that
	 * default value is used.
	 */
	public int timeout() {
		return timeout;
	}

	// ---------------------------------------------------------------- send

	protected HttpConnection httpConnection;
	protected HttpConnectionProvider httpConnectionProvider;

	/**
	 * Returns http connection provider that was used for creating
	 * current http connection.
	 */
	public HttpConnectionProvider httpConnectionProvider() {
		return httpConnectionProvider;
	}

	/**
	 * Returns {@link HttpConnection} that is going to be
	 * used for sending this request. Value is available
	 * ONLY after calling {@link #open()} and before {@link #send()}.
	 */
	public HttpConnection httpConnection() {
		return httpConnection;
	}

	/**
	 * Opens a new {@link HttpConnection connection} using
	 * {@link JoddHttp#httpConnectionProvider default connection provider}.
	 */
	public HttpRequest open() {
		return open(JoddHttp.httpConnectionProvider);
	}

	/**
	 * Opens a new {@link jodd.http.HttpConnection connection}
	 * using given {@link jodd.http.HttpConnectionProvider}.
	 */
	public HttpRequest open(HttpConnectionProvider httpConnectionProvider) {
		if (this.httpConnection != null) {
			throw new HttpException("Connection already opened");
		}
		try {
			this.httpConnectionProvider = httpConnectionProvider;
			this.httpConnection = httpConnectionProvider.createHttpConnection(this);
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		return this;
	}

	/**
	 * Assignees provided {@link jodd.http.HttpConnection} for communication.
	 * It does not actually opens it until the {@link #send() sending}.
	 */
	public HttpRequest open(HttpConnection httpConnection) {
		if (this.httpConnection != null) {
			throw new HttpException("Connection already opened");
		}
		this.httpConnection = httpConnection;
		this.httpConnectionProvider = null;
		return this;
	}

	/**
	 * Continues using the same keep-alive connection.
	 * Don't use any variant of <code>open()</code> when
	 * continuing the communication!
	 * First it checks if "Connection" header exist in the response
	 * and if it is equal to "Keep-Alive" value. Then it
	 * checks the "Keep-Alive" headers "max" parameter.
	 * If its value is positive, then the existing {@link jodd.http.HttpConnection}
	 * from the request will be reused. If max value is 1,
	 * connection will be sent with "Connection: Close" header, indicating
	 * its the last request. When new connection is created, the
	 * same {@link jodd.http.HttpConnectionProvider} that was used for
	 * creating initial connection is used for opening the new connection.
	 *
	 * @param doContinue set it to <code>false</code> to indicate the last connection
	 */
	public HttpRequest keepAlive(HttpResponse httpResponse, boolean doContinue) {
		boolean keepAlive = httpResponse.isConnectionPersistent();
		if (keepAlive) {
			HttpConnection previousConnection = httpResponse.getHttpRequest().httpConnection;

			if (previousConnection != null) {
				// keep using the connection!
				this.httpConnection = previousConnection;
				this.httpConnectionProvider = httpResponse.getHttpRequest().httpConnectionProvider();
			}

			//keepAlive = true; (already set)
		} else {
			// close previous connection
			httpResponse.close();

			// force keep-alive on new request
			keepAlive = true;
		}

		// if we don't want to continue with this persistent session, mark this connection as closed
		if (doContinue == false) {
			keepAlive = false;
		}

		connectionKeepAlive(keepAlive);

		// if connection is not opened, open it using previous connection provider
		if (httpConnection == null) {
			open(httpResponse.getHttpRequest().httpConnectionProvider());
		}
		return this;
	}

	/**
	 * {@link #open() Opens connection} if not already open, sends request,
	 * reads response and closes the request. If keep-alive mode is enabled
	 * connection will not be closed.
	 */
	public HttpResponse send() {
		if (httpConnection == null) {
			open();
		}

		// prepare http connection

		if (timeout != -1) {
			httpConnection.setTimeout(timeout);
		}

		// sends data
		HttpResponse httpResponse;
		try {
			OutputStream outputStream = httpConnection.getOutputStream();

			sendTo(outputStream);

			InputStream inputStream = httpConnection.getInputStream();

			httpResponse = HttpResponse.readFrom(inputStream);

			httpResponse.assignHttpRequest(this);
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}

		boolean keepAlive = httpResponse.isConnectionPersistent();

		if (keepAlive == false) {
			// closes connection if keep alive is false, or if counter reached 0
			httpConnection.close();
			httpConnection = null;
		}

		return httpResponse;
	}

	// ---------------------------------------------------------------- buffer

	/**
	 * Prepares the request buffer.
	 */
	@Override
	protected Buffer buffer(boolean fullRequest) {
		// INITIALIZATION

		// host port

		if (header(HEADER_HOST) == null) {
			setHostHeader();
		}

		// form

		Buffer formBuffer = formBuffer();

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

		Buffer request = new Buffer();

		request.append(method)
			.append(SPACE)
			.append(path);

		if (query != null && !query.isEmpty()) {
			request.append('?');
			request.append(queryString);
		}

		request.append(SPACE)
			.append(httpVersion)
			.append(CRLF);

		for (String key : headers.names()) {
			List<String> values = headers.getAll(key);

			String headerName = HttpUtil.prepareHeaderParameterName(key);

			for (String value : values) {
				request.append(headerName);
				request.append(": ");
				request.append(value);
				request.append(CRLF);
			}
		}

		if (fullRequest) {
			request.append(CRLF);

			if (form != null) {
				request.append(formBuffer);
			} else if (body != null) {
				request.append(body);
			}
		}

		return request;
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