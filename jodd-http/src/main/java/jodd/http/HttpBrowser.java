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

import jodd.exception.ExceptionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Emulates HTTP Browser and persist cookies between requests.
 */
public class HttpBrowser {

	protected HttpConnectionProvider httpConnectionProvider;
	protected HttpRequest httpRequest;
	protected HttpResponse httpResponse;
	protected HttpMultiMap<Cookie> cookies = HttpMultiMap.newCaseInsensitiveMap();
	protected HeadersMultiMap defaultHeaders = new HeadersMultiMap();
	protected boolean keepAlive;
	protected long elapsedTime;
	protected boolean catchTransportExceptions = true;

	public HttpBrowser() {
		httpConnectionProvider = HttpConnectionProvider.get();
	}

	/**
	 * Returns <code>true</code> if keep alive is used.
	 */
	public boolean isKeepAlive() {
		return keepAlive;
	}

	/**
	 * Defines that persistent HTTP connection should be used.
	 */
	public HttpBrowser setKeepAlive(final boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	/**
	 * Defines if transport exceptions should be thrown.
	 */
	public HttpBrowser setCatchTransportExceptions(final boolean catchTransportExceptions) {
		this.catchTransportExceptions = catchTransportExceptions;
		return this;
	}

	/**
	 * Defines proxy for a browser.
	 */
	public HttpBrowser setProxyInfo(final ProxyInfo proxyInfo) {
		httpConnectionProvider.useProxy(proxyInfo);
		return this;
	}

	/**
	 * Defines {@link jodd.http.HttpConnectionProvider} for this browser session.
	 * Resets the previous proxy definition, if set.
	 */
	public HttpBrowser setHttpConnectionProvider(final HttpConnectionProvider httpConnectionProvider) {
		this.httpConnectionProvider = httpConnectionProvider;
		return this;
	}

	/**
	 * Adds default header to all requests.
	 */
	public HttpBrowser setDefaultHeader(final String name, final String value) {
		defaultHeaders.addHeader(name, value);
		return this;
	}

	/**
	 * Returns last used request.
	 */
	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	/**
	 * Returns last received {@link HttpResponse HTTP response} object.
	 */
	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	/**
	 * Returns last response HTML page.
	 */
	public String getPage() {
		if (httpResponse == null) {
			return null;
		}
		return httpResponse.bodyText();
	}


	/**
	 * Sends new request as a browser. Before sending,
	 * all browser cookies are added to the request.
	 * After sending, the cookies are read from the response.
	 * Moreover, status codes 301 and 302 are automatically
	 * handled. Returns very last response.
	 */
	public HttpResponse sendRequest(HttpRequest httpRequest) {
		elapsedTime = System.currentTimeMillis();

		// send request

		httpRequest.followRedirects(false);

		while (true) {
			this.httpRequest = httpRequest;
			HttpResponse previousResponse = this.httpResponse;
			this.httpResponse = null;

			addDefaultHeaders(httpRequest);
			addCookies(httpRequest);

			// send request
			if (catchTransportExceptions) {
				try {
					this.httpResponse = _sendRequest(httpRequest, previousResponse);
				}
				catch (HttpException httpException) {
					httpResponse = new HttpResponse();
					httpResponse.assignHttpRequest(httpRequest);
					httpResponse.statusCode(503);
					httpResponse.statusPhrase("Service unavailable. " + ExceptionUtil.message(httpException));
				}
			}
			else {
				this.httpResponse =_sendRequest(httpRequest, previousResponse);
			}

			readCookies(httpResponse);

			int statusCode = httpResponse.statusCode();

			// 301: moved permanently
			if (statusCode == 301) {
				String newPath = httpResponse.location();

				if (newPath == null) {
					break;
				}

				httpRequest = HttpRequest.get(newPath);
				continue;
			}

			// 302: redirect, 303: see other
			if (statusCode == 302 || statusCode == 303) {
				String newPath = httpResponse.location();

				if (newPath == null) {
					break;
				}

				httpRequest = HttpRequest.get(newPath);
				continue;
			}

			// 307: temporary redirect, 308: permanent redirect
			if (statusCode == 307 || statusCode == 308) {
				String newPath = httpResponse.location();

				if (newPath == null) {
					break;
				}

				String originalMethod = httpRequest.method();
				httpRequest = new HttpRequest()
						.method(originalMethod)
						.set(newPath);
				continue;
			}

			break;
		}

		elapsedTime = System.currentTimeMillis() - elapsedTime;

		return this.httpResponse;
	}

	/**
	 * Opens connection and sends a response.
	 */
	protected HttpResponse _sendRequest(final HttpRequest httpRequest, final HttpResponse previouseResponse) {
		if (!keepAlive) {
			httpRequest.open(httpConnectionProvider);
		} else {
			// keeping alive
			if (previouseResponse == null) {
				httpRequest.open(httpConnectionProvider).connectionKeepAlive(true);
			} else {
				httpRequest.keepAlive(previouseResponse, true);
			}
		}

		return httpRequest.send();
	}

	/**
	 * Add default headers to the request. If request already has a header set,
	 * default header will be ignored.
	 */
	protected void addDefaultHeaders(final HttpRequest httpRequest) {
		for (Map.Entry<String, String> entry : defaultHeaders.entries()) {
			String name = entry.getKey();

			if (!httpRequest.headers.contains(name)) {
				httpRequest.headers.add(name, entry.getValue());
			}
		}
	}

	/**
	 * Returns elapsed time of last {@link #sendRequest(HttpRequest)} in milliseconds.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	// ---------------------------------------------------------------- close

	/**
	 * Closes browser explicitly, needed when keep-alive connection is used.
	 */
	public void close() {
		if (httpResponse != null) {
			httpResponse.close();
		}
	}

	// ---------------------------------------------------------------- cookies

	/**
	 * Deletes all cookies.
	 */
	public void clearCookies() {
		cookies.clear();
	}

	/**
	 * Reads cookies from response and adds to cookies list.
	 */
	protected void readCookies(final HttpResponse httpResponse) {
		Cookie[] newCookies = httpResponse.cookies();

		for (Cookie cookie : newCookies) {
			cookies.add(cookie.getName(), cookie);
		}
	}

	/**
	 * Add cookies to the request.
	 */
	protected void addCookies(final HttpRequest httpRequest) {
		// prepare all cookies
		List<Cookie> cookiesList = new ArrayList<>();

		if (!cookies.isEmpty()) {
			for (Map.Entry<String, Cookie> cookieEntry : cookies) {
				cookiesList.add(cookieEntry.getValue());
			}

			httpRequest.cookies(cookiesList.toArray(new Cookie[0]));
		}
	}
}
