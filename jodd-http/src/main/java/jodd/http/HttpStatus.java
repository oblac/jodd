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

/**
 * HTTP status codes.
 */
public class HttpStatus {

	/**
	 * Returns {@code true} if status code indicates successful result.
	 */
	public static boolean isSuccessful(final int statusCode) {
		return statusCode < 400;
	}

	/**
	 * Returns {@code true} if status code indicates a redirect.
	 */
	public static boolean isRedirect(final int statusCode) {
		return statusCode >=300 && statusCode < 400;
	}

	/**
	 * Returns {@code true} if status code indicates an error.
	 */
	public static boolean isError(final int statusCode) {
		return statusCode >= 500;
	}

	// ---------------------------------------------------------------- 1xx

	/**
	 * HTTP Status-Code 100: Continue.
	 */
	public static final int HTTP_CONTINUE = 100;

	// ---------------------------------------------------------------- 2xx

	/**
	 * HTTP Status-Code 200: OK.
	 */
	public static final int HTTP_OK = 200;

	/**
	 * HTTP Status-Code 201: Created.
	 */
	public static final int HTTP_CREATED = 201;

	/**
	 * HTTP Status-Code 202: Accepted.
	 */
	public static final int HTTP_ACCEPTED = 202;

	/**
	 * HTTP Status-Code 203: Non-Authoritative Information.
	 */
	public static final int HTTP_NOT_AUTHORITATIVE = 203;

	/**
	 * HTTP Status-Code 204: No Content.
	 */
	public static final int HTTP_NO_CONTENT = 204;

	/**
	 * HTTP Status-Code 205: Reset Content.
	 */
	public static final int HTTP_RESET = 205;

	/**
	 * HTTP Status-Code 206: Partial Content.
	 */
	public static final int HTTP_PARTIAL = 206;

	// ---------------------------------------------------------------- 3xx

	/**
	 * HTTP Status-Code 300: Multiple Choices.
	 */
	public static final int HTTP_MULTIPLE_CHOICES = 300;

	/**
	 * HTTP Status-Code 301: Moved Permanently.
	 */
	public static final int HTTP_MOVED_PERMANENTLY = 301;

	/**
	 * HTTP Status-Code 302: Temporary Redirect.
	 */
	public static final int HTTP_MOVED_TEMPORARY = 302;

	/**
	 * HTTP Status-Code 303: See Other.
	 */
	public static final int HTTP_SEE_OTHER = 303;

	/**
	 * HTTP Status-Code 304: Not Modified.
	 */
	public static final int HTTP_NOT_MODIFIED = 304;

	/**
	 * HTTP Status-Code 305: Use Proxy.
	 */
	public static final int HTTP_USE_PROXY = 305;

	/**
	 * HTTP Status-Code 307: Temporary Redirect.
	 */
	public static final int HTTP_TEMPORARY_REDIRECT = 307;

	// ---------------------------------------------------------------- 4xx

	/**
	 * HTTP Status-Code 400: Bad Request.
	 */
	public static final int HTTP_BAD_REQUEST = 400;

	/**
	 * HTTP Status-Code 401: Unauthorized.
	 */
	public static final int HTTP_UNAUTHORIZED = 401;

	/**
	 * HTTP Status-Code 402: Payment Required.
	 */
	public static final int HTTP_PAYMENT_REQUIRED = 402;

	/**
	 * HTTP Status-Code 403: Forbidden.
	 */
	public static final int HTTP_FORBIDDEN = 403;

	/**
	 * HTTP Status-Code 404: Not Found.
	 */
	public static final int HTTP_NOT_FOUND = 404;

	/**
	 * HTTP Status-Code 405: Method Not Allowed.
	 */
	public static final int HTTP_BAD_METHOD = 405;

	/**
	 * HTTP Status-Code 406: Not Acceptable.
	 */
	public static final int HTTP_NOT_ACCEPTABLE = 406;

	/**
	 * HTTP Status-Code 407: Proxy Authentication Required.
	 */
	public static final int HTTP_PROXY_AUTH_REQUIRED = 407;

	/**
	 * HTTP Status-Code 408: Request Time-Out.
	 */
	public static final int HTTP_CLIENT_TIMEOUT = 408;

	/**
	 * HTTP Status-Code 409: Conflict.
	 */
	public static final int HTTP_CONFLICT = 409;

	/**
	 * HTTP Status-Code 410: Gone.
	 */
	public static final int HTTP_GONE = 410;

	/**
	 * HTTP Status-Code 411: Length Required.
	 */
	public static final int HTTP_LENGTH_REQUIRED = 411;

	/**
	 * HTTP Status-Code 412: Precondition Failed.
	 */
	public static final int HTTP_PRECON_FAILED = 412;

	/**
	 * HTTP Status-Code 413: Request Entity Too Large.
	 */
	public static final int HTTP_ENTITY_TOO_LARGE = 413;

	/**
	 * HTTP Status-Code 414: Request-URI Too Large.
	 */
	public static final int HTTP_REQ_TOO_LONG = 414;

	/**
	 * HTTP Status-Code 415: Unsupported Media Type.
	 */
	public static final int HTTP_UNSUPPORTED_TYPE = 415;

	// ---------------------------------------------------------------- 5xx

	/**
	 * HTTP Status-Code 500: Internal Server Error.
	 */
	public static final int HTTP_INTERNAL_ERROR = 500;

	/**
	 * HTTP Status-Code 501: Not Implemented.
	 */
	public static final int HTTP_NOT_IMPLEMENTED = 501;

	/**
	 * HTTP Status-Code 502: Bad Gateway.
	 */
	public static final int HTTP_BAD_GATEWAY = 502;

	/**
	 * HTTP Status-Code 503: Service Unavailable.
	 */
	public static final int HTTP_UNAVAILABLE = 503;

	/**
	 * HTTP Status-Code 504: Gateway Timeout.
	 */
	public static final int HTTP_GATEWAY_TIMEOUT = 504;

	/**
	 * HTTP Status-Code 505: HTTP Version Not Supported.
	 */
	public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

}
