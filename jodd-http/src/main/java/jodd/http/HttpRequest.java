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

import jodd.net.HttpMethod;
import jodd.net.MimeTypes;
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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static jodd.util.StringPool.CRLF;
import static jodd.util.StringPool.SPACE;

/**
 * HTTP request.
 */
public class HttpRequest extends HttpBase<HttpRequest> {

	protected String protocol = "http";
	protected String host = "localhost";
	protected int port = Defaults.DEFAULT_PORT;
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
	public HttpRequest host(final String host) {
		this.host = host;
		if (headers.contains(HEADER_HOST)) {
			headerOverwrite(HEADER_HOST, host);
		}
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
	public HttpRequest protocol(final String protocol) {
		this.protocol = protocol;
		return this;
	}

	/**
	 * Returns request port number. When port is not
	 * explicitly defined, returns default port for
	 * current protocol.
	 */
	public int port() {
		if (port == Defaults.DEFAULT_PORT) {
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
	public HttpRequest port(final int port) {
		this.port = port;
		return this;
	}

	// ---------------------------------------------------------------- set

	/**
	 * Sets the destination (method, host, port... ) at once.
	 */
	public HttpRequest set(String destination) {
		destination = destination.trim();

		// http method, optional

		int ndx = destination.indexOf(' ');

		if (ndx != -1) {
			String method = destination.substring(0, ndx).toUpperCase();

			try {
				HttpMethod httpMethod = HttpMethod.valueOf(method);
				this.method = httpMethod.name();
				destination = destination.substring(ndx + 1);
			}
			catch (IllegalArgumentException ignore) {
				// unknown http method
			}
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

			String hostToSet = destination.substring(0, ndx);
			destination = destination.substring(ndx);

			// port

			ndx = hostToSet.indexOf(':');

			if (ndx == -1) {
				port = Defaults.DEFAULT_PORT;
			} else {
				port = Integer.parseInt(hostToSet.substring(ndx + 1));
				hostToSet = hostToSet.substring(0, ndx);
			}

			host(hostToSet);
		}

		// path + query

		path(destination);

		return this;
	}

	// ---------------------------------------------------------------- static factories

	/**
	 * Generic request builder, usually used when method is a variable.
	 * Otherwise, use one of the other static request builder methods.
	 */
	public static HttpRequest create(final String method, final String destination) {
		return new HttpRequest()
				.method(method.toUpperCase())
				.set(destination);
	}

	/**
	 * Builds a CONNECT request.
	 */
	public static HttpRequest connect(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.CONNECT)
				.set(destination);
	}
	/**
	 * Builds a GET request.
	 */
	public static HttpRequest get(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.GET)
				.set(destination);
	}
	/**
	 * Builds a POST request.
	 */
	public static HttpRequest post(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.POST)
				.set(destination);
	}
	/**
	 * Builds a PUT request.
	 */
	public static HttpRequest put(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.PUT)
				.set(destination);
	}
	/**
	 * Builds a PATCH request.
	 */
	public static HttpRequest patch(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.PATCH)
				.set(destination);
	}
	/**
	 * Builds a DELETE request.
	 */
	public static HttpRequest delete(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.DELETE)
				.set(destination);
	}
	/**
	 * Builds a HEAD request.
	 */
	public static HttpRequest head(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.HEAD)
				.set(destination);
	}
	/**
	 * Builds a TRACE request.
	 */
	public static HttpRequest trace(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.TRACE)
				.set(destination);
	}
	/**
	 * Builds an OPTIONS request.
	 */
	public static HttpRequest options(final String destination) {
		return new HttpRequest()
				.method(HttpMethod.OPTIONS)
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
	 * Does not validate if method is one of the HTTP methods.
	 */
	public HttpRequest method(final String method) {
		this.method = method.toUpperCase();
		return this;
	}
	public HttpRequest method(final HttpMethod httpMethod) {
		this.method = httpMethod.name();
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

		if (!path.startsWith(StringPool.SLASH)) {
			path = StringPool.SLASH + path;
		}

		int ndx = path.indexOf('?');

		if (ndx != -1) {
			String queryString = path.substring(ndx + 1);

			path = path.substring(0, ndx);

			query = HttpUtil.parseQuery(queryString, true);
		} else {
			query = HttpMultiMap.newCaseInsensitiveMap();
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
	public HttpRequest multipart(final boolean multipart) {
		this.multipart = multipart;
		return this;
	}


	// ---------------------------------------------------------------- cookies

	/**
	 * Sets cookies to the request.
	 */
	public HttpRequest cookies(final Cookie... cookies) {
		if (cookies.length == 0) {
			return this;
		}

		StringBuilder cookieString = new StringBuilder();

		boolean first = true;

		for (Cookie cookie : cookies) {
			Integer maxAge = cookie.getMaxAge();
			if (maxAge != null && maxAge.intValue() == 0) {
				continue;
			}

			if (!first) {
				cookieString.append("; ");
			}

			first = false;
			cookieString.append(cookie.getName());
			cookieString.append('=');
			cookieString.append(cookie.getValue());
		}

		headerOverwrite("cookie", cookieString.toString());

		return this;
	}


	// ---------------------------------------------------------------- query

	/**
	 * Adds query parameter.
	 */
	public HttpRequest query(final String name, final String value) {
		query.add(name, value);
		return this;
	}

	/**
	 * Adds many query parameters at once. Although it accepts objects,
	 * each value will be converted to string.
	 */
	public HttpRequest query(final String name1, final Object value1, final Object... parameters) {
		query(name1, value1 == null ? null : value1.toString());

		for (int i = 0; i < parameters.length; i += 2) {
			String name = parameters[i].toString();

			String value = parameters[i + 1].toString();
			query.add(name, value);
		}
		return this;
	}

	/**
	 * Adds all parameters from the provided map.
	 */
	public HttpRequest query(final Map<String, String> queryMap) {
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
	public HttpRequest queryRemove(final String name) {
		query.remove(name);
		return this;
	}

	// ---------------------------------------------------------------- queryString

	/**
	 * @see #queryString(String, boolean)
	 */
	public HttpRequest queryString(final String queryString) {
		return queryString(queryString, true);
	}

	/**
	 * Sets query from provided query string. Previous query values
	 * are discarded.
	 */
	public HttpRequest queryString(final String queryString, final boolean decode) {
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

	protected String queryEncoding = Defaults.queryEncoding;

	/**
	 * Defines encoding for query parameters.
	 */
	public HttpRequest queryEncoding(final String encoding) {
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

		if (port != Defaults.DEFAULT_PORT) {
			url.append(':');
			url.append(port);
		}

		return url.toString();
	}

	// ---------------------------------------------------------------- auth

	/**
	 * Enables basic authentication by adding required header.
	 */
	public HttpRequest basicAuthentication(final String username, final String password) {
		if (username != null && password != null) {
			String data = username.concat(StringPool.COLON).concat(password);

			String base64 = Base64.encodeToString(data);

			headerOverwrite(HEADER_AUTHORIZATION, "Basic " + base64);
		}

		return this;
	}

	/**
	 * Enables token-based authentication.
	 */
	public HttpRequest tokenAuthentication(final String token) {
		if (token != null) {
			headerOverwrite(HEADER_AUTHORIZATION, "Bearer " + token);
		}
		return this;
	}


	// ---------------------------------------------------------------- https

	private boolean trustAllCertificates;
	private boolean verifyHttpsHost = true;

	/**
	 * Trusts all certificates, use with caution.
	 */
	public HttpRequest trustAllCerts(final boolean trust) {
		trustAllCertificates = trust;
		return this;
	}

	/**
	 * Returns a flag if to trusts all certificates.
	 */
	public boolean trustAllCertificates() {
		return trustAllCertificates;
	}

	/**
	 * Verifies HTTPS hosts.
	 */
	public HttpRequest verifyHttpsHost(final boolean verifyHttpsHost) {
		this.verifyHttpsHost = verifyHttpsHost;
		return this;
	}

	/**
	 * Returns a flag if to verify https hosts.
	 */
	public boolean verifyHttpsHost() {
		return verifyHttpsHost;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Sets 'Host' header from current host and port.
	 */
	public HttpRequest setHostHeader() {
		String hostPort = this.host;

		if (port != Defaults.DEFAULT_PORT) {
			hostPort += StringPool.COLON + port;
		}

		headerOverwrite(HEADER_HOST, hostPort);
		return this;
	}

	// ---------------------------------------------------------------- monitor

	/**
	 * Registers {@link jodd.http.HttpProgressListener listener} that will
	 * monitor upload progress. Be aware that the whole size of the
	 * request is being monitored, not only the files content.
	 */
	public HttpRequest monitor(final HttpProgressListener httpProgressListener) {
		this.httpProgressListener = httpProgressListener;
		return this;
	}

	// ---------------------------------------------------------------- connection properties

	protected int timeout = -1;
	protected int connectTimeout = -1;
	protected boolean followRedirects = false;
	protected int maxRedirects = 50;

	/**
	 * Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or,
	 * put differently, a maximum period inactivity between two consecutive data packets).
	 * After establishing the connection, the client socket waits for response after sending
	 * the request. This is the elapsed time since the client has sent request to the
	 * server before server responds. Please note that this is not same as HTTP Error 408 which
	 * the server sends to the client. In other words its maximum period inactivity between
	 * two consecutive data packets arriving at client side after connection is established.
	 * A timeout value of zero is interpreted as an infinite timeout.
	 * @see jodd.http.HttpConnection#setTimeout(int)
	 */
	public HttpRequest timeout(final int milliseconds) {
		this.timeout = milliseconds;
		return this;
	}

	/**
	 * Returns read timeout (SO_TIMEOUT) in milliseconds. Negative value
	 * means that default value is used.
	 * @see #timeout(int)
	 */
	public int timeout() {
		return timeout;
	}

	/**
	 * Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout
	 * for waiting for data or, put differently, a maximum period inactivity between
	 * two consecutive data packets). A timeout value of zero is interpreted as
	 * an infinite timeout.
	 */
	public HttpRequest connectionTimeout(final int milliseconds) {
		this.connectTimeout = milliseconds;
		return this;
	}

	/**
	 * Returns socket connection timeout. Negative value means that default
	 * value is used.
	 * @see #connectionTimeout(int)
	 */
	public int connectionTimeout() {
		return connectTimeout;
	}

	/**
	 * Defines if redirects responses should be followed. NOTE: when redirection is enabled,
	 * the original URL will NOT be preserved in the request!
	 */
	public HttpRequest followRedirects(final boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	/**
	 * Returns {@code true} if redirects are followed.
	 */
	public boolean isFollowRedirects() {
		return this.followRedirects;
	}

	/**
	 * Sets the max number of redirects, used when {@link #followRedirects} is enabled.
	 */
	public HttpRequest maxRedirects(final int maxRedirects) {
		this.maxRedirects = maxRedirects;
		return this;
	}

	/**
	 * Returns max number of redirects, used when {@link #followRedirects} is enabled.
	 */
	public int maxRedirects() {
		return this.maxRedirects;
	}


	// ---------------------------------------------------------------- send

	protected HttpConnection httpConnection;
	protected HttpConnectionProvider httpConnectionProvider;

	/**
	 * Uses custom connection provider when {@link #open() opening} the
	 * connection.
	 */
	public HttpRequest withConnectionProvider(final HttpConnectionProvider httpConnectionProvider) {
		this.httpConnectionProvider = httpConnectionProvider;
		return this;
	}

	/**
	 * Returns http connection provider that was used for creating
	 * current http connection. If <code>null</code>, default
	 * connection provider will be used.
	 */
	public HttpConnectionProvider connectionProvider() {
		return httpConnectionProvider;
	}

	/**
	 * Returns {@link HttpConnection} that is going to be
	 * used for sending this request. Value is available
	 * ONLY after calling {@link #open()} and before {@link #send()}.
	 */
	public HttpConnection connection() {
		return httpConnection;
	}

	/**
	 * Opens a new {@link HttpConnection connection} using either
	 * provided or {@link HttpConnectionProvider default} connection
	 * provider.
	 */
	public HttpRequest open() {
		if (httpConnectionProvider == null) {
			return open(HttpConnectionProvider.get());
		}

		return open(httpConnectionProvider);
	}

	/**
	 * Opens a new {@link jodd.http.HttpConnection connection}
	 * using given {@link jodd.http.HttpConnectionProvider}.
	 */
	public HttpRequest open(final HttpConnectionProvider httpConnectionProvider) {
		if (this.httpConnection != null) {
			throw new HttpException("Connection already opened");
		}
		try {
			this.httpConnectionProvider = httpConnectionProvider;
			this.httpConnection = httpConnectionProvider.createHttpConnection(this);
		} catch (IOException ioex) {
			throw new HttpException("Can't connect to: " + url(), ioex);
		}

		return this;
	}

	/**
	 * Assignees provided {@link jodd.http.HttpConnection} for communication.
	 * It does not actually opens it until the {@link #send() sending}.
	 */
	public HttpRequest open(final HttpConnection httpConnection) {
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
	public HttpRequest keepAlive(final HttpResponse httpResponse, final boolean doContinue) {
		boolean keepAlive = httpResponse.isConnectionPersistent();
		if (keepAlive) {
			HttpConnection previousConnection = httpResponse.getHttpRequest().httpConnection;

			if (previousConnection != null) {
				// keep using the connection!
				this.httpConnection = previousConnection;
				this.httpConnectionProvider = httpResponse.getHttpRequest().connectionProvider();
			}

			//keepAlive = true; (already set)
		} else {
			// close previous connection
			httpResponse.close();

			// force keep-alive on new request
			keepAlive = true;
		}

		// if we don't want to continue with this persistent session, mark this connection as closed
		if (!doContinue) {
			keepAlive = false;
		}

		connectionKeepAlive(keepAlive);

		// if connection is not opened, open it using previous connection provider
		if (httpConnection == null) {
			open(httpResponse.getHttpRequest().connectionProvider());
		}
		return this;
	}

	/**
	 * {@link #open() Opens connection} if not already open, sends request,
	 * reads response and closes the request. If keep-alive mode is enabled
	 * connection will not be closed.
	 */
	public HttpResponse send() {
		if (!followRedirects) {
			return _send();
		}

		int redirects = this.maxRedirects;

		while (redirects > 0) {
			redirects--;

			final HttpResponse httpResponse = _send();

			final int statusCode = httpResponse.statusCode();

			if (HttpStatus.isRedirect(statusCode)) {
				_reset();
				set(httpResponse.location());
				continue;
			}

			return httpResponse;
		}

		throw new HttpException("Max number of redirects exceeded: " + this.maxRedirects);
	}

	/**
	 * Resets the request by resetting all additional values
	 * added during the sending.
	 */
	private void _reset() {
		headers.remove(HEADER_HOST);
	}

	private HttpResponse _send() {
		if (httpConnection == null) {
			open();
		}

		// sends data
		final HttpResponse httpResponse;
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

		if (!keepAlive) {
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
	protected Buffer buffer(final boolean fullRequest) {
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
			header("User-Agent", Defaults.userAgent);
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

		populateHeaderAndBody(request, formBuffer, fullRequest);

		return request;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses input stream and creates new <code>HttpRequest</code> object.
	 * Assumes input stream is in ISO_8859_1 encoding.
	 */
	public static HttpRequest readFrom(final InputStream in) {
		return readFrom(in, StringPool.ISO_8859_1);
	}

	public static HttpRequest readFrom(final InputStream in, final String encoding) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in, encoding));
		} catch (UnsupportedEncodingException uneex) {
			return null;
		}

		final HttpRequest httpRequest = new HttpRequest();
		httpRequest.headers.clear();

		final String line;
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


	// ---------------------------------------------------------------- shortcuts

	/**
	 * Specifies JSON content type.
	 */
	public HttpRequest contentTypeJson() {
		return contentType(MimeTypes.MIME_APPLICATION_JSON);
	}

	/**
	 * Accepts JSON content type.
	 */
	public HttpRequest acceptJson() {
		return accept(MimeTypes.MIME_APPLICATION_JSON);
	}


	// ---------------------------------------------------------------- functional/async

	/**
	 * Sends http request asynchronously using common fork-join pool.
	 * Note that this is not the right non-blocking call (not a NIO), it is just
	 * a regular call that is operated in a separate thread.
	 */
	public CompletableFuture<HttpResponse> sendAsync() {
		return CompletableFuture.supplyAsync(this::send);
	}

	/**
	 * Syntax sugar.
	 */
	public <R> R sendAndReceive(final Function<HttpResponse, R> responseHandler) {
		return responseHandler.apply(send());
	}

	/**
	 * Syntax sugar.
	 */
	public void sendAndReceive(final Consumer<HttpResponse> responseHandler) {
		responseHandler.accept(send());
	}

}
