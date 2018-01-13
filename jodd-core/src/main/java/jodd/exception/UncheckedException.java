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

package jodd.exception;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.concurrent.Callable;

/**
 * Unchecked exception and also a wrapper for checked exceptions.
 */
@SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter"})
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

	public UncheckedException(final Throwable t) {
		super(t.getMessage());
		cause = t;
		this.showCauseDetails = true;
	}

	public UncheckedException(final Throwable t, final boolean showCauseDetails) {
		super(t.getMessage());
		cause = t;
		this.showCauseDetails = showCauseDetails;
	}

	public UncheckedException() {
		super();
		cause = null;
		this.showCauseDetails = false;
	}

	public UncheckedException(final String message) {
		super(message);
		cause = null;
		this.showCauseDetails = false;
	}

	public UncheckedException(final String message, final Throwable t) {
		super(message, t);
		cause = t;
		this.showCauseDetails = true;
	}

	public UncheckedException(final String message, final Throwable t, final boolean showCauseDetails) {
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
	public void printStackTrace(final PrintStream ps) {
		synchronized (ps) {
			super.printStackTrace(ps);
			if ((cause != null) && showCauseDetails) {
				Throwable rootCause = ExceptionUtil.getRootCause(cause);
				ps.println(CAUSE_DIV);
				rootCause.printStackTrace(ps);
			}
		}
	}

	@Override
	public void printStackTrace(final PrintWriter pw) {
		synchronized (pw) {
			super.printStackTrace(pw);
			if ((cause != null) && showCauseDetails) {
				Throwable rootCause = ExceptionUtil.getRootCause(cause);
				pw.println(CAUSE_DIV);
				rootCause.printStackTrace(pw);
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
	public static <V> V callAndWrapException(final Callable<V> callable) {
		try {
			return callable.call();
		}
		catch (IOException ioex) {
			throw new UncheckedIOException(ioex);
		}
		catch (RuntimeException rtex) {
			throw rtex;
		}
		catch (Exception t) {
			throw new UncheckedException(t);
		}
	}

	@FunctionalInterface
	public interface CallableVoid {
		public void call() throws Exception;
	}

	/**
	 * Wraps checked exceptions in a <code>UncheckedException</code>.
	 * Unchecked exceptions are not wrapped.
	 */
	public static void runAndWrapException(final CallableVoid callable) {
		try {
			callable.call();
		}
		catch (IOException ioex) {
			throw new UncheckedIOException(ioex);
		}
		catch (RuntimeException rtex) {
			throw rtex;
		}
		catch (Exception t) {
			throw new UncheckedException(t);
		}
	}

	/**
	 * Wraps all exceptions in a <code>UncheckedException</code>
	 */
	public static RuntimeException wrap(final Throwable t) {
		return new UncheckedException(t);
	}

	/**
	 * Wraps all exceptions in a <code>UncheckedException</code>
	 */
	public static RuntimeException wrap(final Throwable t, final String message) {
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
