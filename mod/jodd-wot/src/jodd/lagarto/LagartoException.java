package jodd.lagarto;

import jodd.exception.UncheckedException;

/**
 * Lagarto exception.
 */
public class LagartoException extends UncheckedException {

	public LagartoException(Throwable t) {
		super(t);
	}

	public LagartoException() {
		super();
	}

	public LagartoException(String message) {
		super(message);
	}

	public LagartoException(String message, int line, int column) {
		this(message + (line != 1 ? " Error at: " + line + ':' + column : ""));
	}

	public LagartoException(String message, Throwable t) {
		super(message, t);
	}
}
