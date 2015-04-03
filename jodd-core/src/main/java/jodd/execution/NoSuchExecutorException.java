package jodd.execution;

import jodd.exception.UncheckedException;

/**
 * @author Vilmos Papp
 */
public class NoSuchExecutorException extends UncheckedException{
	public NoSuchExecutorException(Throwable t) {
		super(t);
	}

	public NoSuchExecutorException(String message) {
		super(message);
	}

	public NoSuchExecutorException(String message, Throwable t) {
		super(message, t);
	}
}
