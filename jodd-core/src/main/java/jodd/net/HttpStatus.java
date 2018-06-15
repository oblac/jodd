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

package jodd.net;

/**
 * Simple developer-friendly set of HttpStatus codes and messages.
 */
public class HttpStatus {

	private final int status;
	protected String message;

	public HttpStatus(final int status, final String message) {
		this.status = status;
		this.message = message;
	}
	public HttpStatus(final int status) {
		this.status = status;
	}

	/**
	 * Returns {@code true} is status is error.
	 */
	public boolean isError() {
		return status >= 400;
	}
	/**
	 * Returns {@code true} is status is successful.
	 */
	public boolean isSuccess() {
		return status < 400;
	}

	/**
	 * Returns status code.
	 */
	public int status() {
		return status;
	}

	/**
	 * Returns status message.
	 */
	public String message() {
		return message;
	}

	// ----------------------------------------------------------------

	public static class Redirection {
		private Redirection() { }

		static Redirection instance = new Redirection();

		public HttpStatus multipleChoice300() {
			return new HttpStatus(300, "The requested resource points to a destination with multiple representations.");
		}

		public HttpStatus movedPermanently301() {
			return new HttpStatus(301, "The requested resource has been assigned a new permanent URL.");
		}
		public HttpStatus movedTemporarily302() {
			return new HttpStatus(302, "The requested resource has been temporarily moved to a different URL.");
		}
		public HttpStatus notModified304() {
			return new HttpStatus(304, "The contents of the requested web page have not been modified since the last access.");
		}
		public HttpStatus temporaryRedirect307() {
			return new HttpStatus(307, "The requested URL resides temporarily under a different URL");
		}
		public HttpStatus permanentRedirect308() {
			return new HttpStatus(308, "All future requests should be sent using a different URI.");
		}
	}

	// ---------------------------------------------------------------- 400

	public static class HttpStatus400 extends HttpStatus {

		public HttpStatus400() {
			super(400);
		}
		public HttpStatus400 badContent() {
			message = "The content type of the request data or the content type of a" +
				" part of a multipart request is not supported.";
			return this;
		}
		public HttpStatus400 badRequest() {
			message = "The API request is invalid or improperly formed.";
			return this;
		}
		public HttpStatus400 exists() {
			message = "Resource already exists.";
			return this;
		}
		public HttpStatus400 invalidDocumentValue() {
			message = "The request failed because it contained an invalid parameter or" +
				" parameter value for the document. Review the API" +
				" documentation to determine which parameters are valid for" +
				" your request.";
			return this;
		}
		public HttpStatus400 invalidQuery() {
			message = "The request is invalid. Check the API documentation to determine" +
				" what parameters are supported for the request and to see if" +
				" the request contains an invalid combination of parameters" +
				" or an invalid parameter value.";
			return this;
		}
		public HttpStatus400 keyExpired() {
			message = "The API key provided in the request is invalid, which means the" +
				" API server is unable to make the request.";
			return this;
		}
		public HttpStatus400 required() {
			message = "The API request is missing required information. The required" +
				" information could be a parameter or resource property.";
			return this;
		}
		public HttpStatus400 validationError() {
			message = "Validation of input failed.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 401

	public static class HttpStatus401 extends HttpStatus {
		public HttpStatus401() {
			super(401);
		}

		public HttpStatus401 unauthorized(String message) {
			message = "Access is denied due to invalid credentials.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 403

	public static class HttpStatus403 extends HttpStatus {
		public HttpStatus403() {
			super(403);
		}

		public HttpStatus403 corsRequestOrigin() {
			message = "The CORS request is from an unknown origin.";
			return this;
		}

		public HttpStatus403 forbidden() {
			message = "The requested operation is forbidden and cannot be completed.";
			return this;
		}
		public HttpStatus403 limitExceeded() {
			message = "The request cannot be completed due to access or rate limitations.";
			return this;
		}
		public HttpStatus403 quotaExceeded() {
			message = "The requested operation requires more resources than the quota" +
				" allows.";
			return this;
		}
		public HttpStatus403 rateLimitExceeded() {
			message = "Too many requests have been sent within a given time span.";
			return this;
		}
		public HttpStatus403 responseTooLarge() {
			message = "The requested resource is too large to return.";
			return this;
		}
		public HttpStatus403 unknownAuth() {
			message = "The API server does not recognize the authorization scheme used" +
				" for the request.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 404

	public static class HttpStatus404 extends HttpStatus {
		public HttpStatus404() {
			super(404);
		}

		public HttpStatus404 notFound() {
			message = "The requested operation failed because a resource associated" +
				" with the request could not be found.";
			return this;
		}
		public HttpStatus404 unsupportedProtocol() {
			message = "The protocol used in the request is not supported.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 405

	public static class HttpStatus405 extends HttpStatus {
		public HttpStatus405() {
			super(405);
		}

		public HttpStatus405 httpMethodNotAllowed() {
			message = "The HTTP method associated with the request is not supported.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 408

	public static class HttpStatus408 extends HttpStatus {
		public HttpStatus408() {
			super(408);
		}

		public HttpStatus408 requestTimeout() {
			message = "The server did not produce a response within the time that the " +
				"server was prepared to wait.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 409

	public static class HttpStatus409 extends HttpStatus {
		public HttpStatus409() {
			super(409);
		}

		public HttpStatus409 conflict() {
			message = "Indicates that the request could not be processed because of " +
				"conflict in the request, such as an edit conflict between " +
				"multiple simultaneous updates.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 500

	public static class HttpStatus500 extends HttpStatus {
		public HttpStatus500() {
			super(500);
		}

		public HttpStatus500 internalError() {
			message = "The request failed due to an internal error.";
			return this;
		}
	}

	// ---------------------------------------------------------------- 503

	public static class HttpStatus503 extends HttpStatus {
		public HttpStatus503() {
			super(503);
		}

		public HttpStatus503 serviceUnavailable() {
			message = "The server is currently unavailable (because it is overloaded or down for maintenance).";
			return this;
		}
	}

	// ---------------------------------------------------------------- static

	public static HttpStatus of(final int status, final String message) {
		return new HttpStatus(status, message);
	}

	public static HttpStatus ok() {
		return new HttpStatus(200, "OK");
	}

	public static Redirection redirection() {
		return Redirection.instance;
	}

	public static HttpStatus400 error400() {
		return new HttpStatus400();
	}
	public static HttpStatus401 error401() {
		return new HttpStatus401();
	}
	public static HttpStatus403 error403() {
		return new HttpStatus403();
	}
	public static HttpStatus404 error404() {
		return new HttpStatus404();
	}
	public static HttpStatus405 error405() {
		return new HttpStatus405();
	}
	public static HttpStatus408 error408() {
		return new HttpStatus408();
	}
	public static HttpStatus409 error409() {
		return new HttpStatus409();
	}
	public static HttpStatus500 error500() {
		return new HttpStatus500();
	}
	public static HttpStatus503 error503() {
		return new HttpStatus503();
	}

}
