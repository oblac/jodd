package jodd.http;

import jodd.exception.UncheckedException;

/**
 * HTTP exception.
 */
public class HttpException extends UncheckedException {

	public HttpException(Throwable t) {
		super(t);
	}

	public HttpException() {
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(String message, Throwable t) {
		super(message, t);
	}
}
