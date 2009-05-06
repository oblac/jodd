// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.exception;

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * Unchecked exception and also a wrapper for checked exceptions.
 */
public class UncheckedException extends RuntimeException {

	protected final Throwable cause;

	/**
	 * Divider between causes printouts.
	 */
	protected static final String CAUSE_DIV = "---[cause]------------------------------------------------------------------------";

	/**
	 * If set to <code>true</code> stack trace will be enhanced with cause's stack traces.
	 */
	protected final boolean showCauseDetails;

	// ---------------------------------------------------------------- constructors

	public UncheckedException(Throwable t) {
		super(t.getMessage());
		cause = t;
		this.showCauseDetails = true;
	}

	public UncheckedException(Throwable t, boolean showCauseDetails) {
		super(t.getMessage());
		cause = t;
		this.showCauseDetails = showCauseDetails;
	}

	public UncheckedException() {
		super();
		cause = null;
		this.showCauseDetails = false;
	}

	public UncheckedException(String message) {
		super(message);
		cause = null;
		this.showCauseDetails = false;
	}

	public UncheckedException(String message, Throwable t) {
		super(message, t);
		cause = t;
		this.showCauseDetails = true;
	}

	public UncheckedException(String message, Throwable t, boolean showCauseDetails) {
		super(message, t);
		cause = t;
		this.showCauseDetails = showCauseDetails;
	}

	// ---------------------------------------------------------------- stack trace

	@Override
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	@Override
	public void printStackTrace(PrintStream ps) {
		synchronized (ps) {
			super.printStackTrace(ps);
			if ((cause != null) && showCauseDetails) {
				ps.println(CAUSE_DIV);
				cause.printStackTrace(ps);
			}
		}
	}

	@Override
	public void printStackTrace(PrintWriter pw) {
		synchronized (pw) {
			super.printStackTrace(pw);
			if ((cause != null) && showCauseDetails) {
				pw.println(CAUSE_DIV);
				cause.printStackTrace(pw);
			}
		}
	}

	// ---------------------------------------------------------------- txt

	/**
	 * Returns the detail message, including the message from the nested exception if there is one.
	 */
	@Override
	public String getMessage() {
		return ExceptionUtil.buildMessage(super.getMessage(), cause);
	}

	// ---------------------------------------------------------------- wrap

	/**
	 * Wraps checked exceptions in a <code>UncheckedException</code>.
	 * Unchecked exceptions are not wrapped.
	 */
	public static RuntimeException wrapChecked(Throwable t) {
		if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		}
		return new UncheckedException(t);
	}

	/**
	 * Wraps all exceptions in a <code>UncheckedException</code>
	 */
	public static RuntimeException wrap(Throwable t) {
		return new UncheckedException(t);
	}

	/**
	 * Wraps all exceptions in a <code>UncheckedException</code>
	 */
	public static RuntimeException wrap(Throwable t, String message) {
		return new UncheckedException(message, t);
	}


	// ---------------------------------------------------------------- cause

	/**
	 * Re-throws cause if exists.
	 */
	public void rethrow() throws Throwable {
		if (cause == null) {
			return;
		}
		throw cause;
	}

	/**
	 * Returns exception cause.
	 */
	@Override
	public Throwable getCause() {
		return cause;
	}

}
