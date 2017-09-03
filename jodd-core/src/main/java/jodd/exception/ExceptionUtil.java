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

import jodd.io.StreamUtil;
import jodd.util.StringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Few exception utilities.
 */
public class ExceptionUtil {

	/**
	 * Returns current stack trace in form of array of stack trace elements.
	 * First stack trace element is removed.
	 * Since an exception is thrown internally, this method is slow.
	 */
	@SuppressWarnings({"ThrowCaughtLocally"})
	public static StackTraceElement[] getCurrentStackTrace() {
		StackTraceElement[] ste = new Exception().getStackTrace();
		if (ste.length > 1) {
			StackTraceElement[] result = new StackTraceElement[ste.length - 1];
			System.arraycopy(ste, 1, result, 0, ste.length - 1);
			return result;
		} else {
			return ste;
		}
	}

	// ---------------------------------------------------------------- exception stack trace

	/**
	 * Returns stack trace filtered by class names.
	 */
	public static StackTraceElement[] getStackTrace(Throwable t, String[] allow, String[] deny) {
		StackTraceElement[] st = t.getStackTrace();
		ArrayList<StackTraceElement> result = new ArrayList<>(st.length);

		elementLoop:
		for (StackTraceElement element : st) {
			String className = element.getClassName();
			if (allow != null) {
				boolean validElemenet = false;
				for (String filter : allow) {
					if (className.contains(filter)) {
						validElemenet = true;
						break;
					}
				}
				if (!validElemenet) {
					continue;
				}
			}
			if (deny != null) {
				for (String filter : deny) {
					if (className.contains(filter)) {
						continue elementLoop;
					}
				}
			}
			result.add(element);
		}
		st = new StackTraceElement[result.size()];
		return result.toArray(st);
	}

	/**
	 * Returns stack trace chain filtered by class names.
	 */
	public static StackTraceElement[][] getStackTraceChain(Throwable t, String[] allow, String[] deny) {
		ArrayList<StackTraceElement[]> result = new ArrayList<>();
		while (t != null) {
			StackTraceElement[] stack = getStackTrace(t, allow, deny);
			result.add(stack);
			t = t.getCause();
		}
		StackTraceElement[][] allStacks = new StackTraceElement[result.size()][];
		for (int i = 0; i < allStacks.length; i++) {
			allStacks[i] = result.get(i);
		}
		return allStacks;
	}


	/**
	 * Returns exception chain starting from top up to root cause.
	 */
	public static Throwable[] getExceptionChain(Throwable throwable) {
		ArrayList<Throwable> list = new ArrayList<>();
		list.add(throwable);
		while ((throwable = throwable.getCause()) != null) {
			list.add(throwable);
		}
		Throwable[] result = new Throwable[list.size()];
		return list.toArray(result);
	}


	// ---------------------------------------------------------------- exception to string


	/**
	 * Prints stack trace into a String.
	 */
	public static String exceptionStackTraceToString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);

		t.printStackTrace(pw);

		StreamUtil.close(pw);
		StreamUtil.close(sw);

		return sw.toString();
	}

	/**
	 * Prints full exception stack trace, from top to root cause, into a String.
	 */
	public static String exceptionChainToString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		while (t != null) {
			t.printStackTrace(pw);
			t = t.getCause();
		}

		StreamUtil.close(pw);
		StreamUtil.close(sw);

		return sw.toString();
	}

	/**
	 * Build a message for the given base message and its cause.
	 */
	public static String buildMessage(String message, Throwable cause) {
		if (cause != null) {
			cause = getRootCause(cause);
			StringBuilder buf = new StringBuilder();
			if (message != null) {
				buf.append(message).append("; ");
			}
			buf.append("<--- ").append(cause);
			return buf.toString();
		} else {
			return message;
		}
	}

	// ---------------------------------------------------------------- root cause

	/**
	 * Introspects the <code>Throwable</code> to obtain the root cause.
	 * <p>
	 * This method walks through the exception chain to the last element,
	 * "root" of the tree, and returns that exception. If no root cause found
	 * returns provided throwable.
	 */
	public static Throwable getRootCause(Throwable throwable) {
		Throwable cause = throwable.getCause();
		if (cause == null) {
			return throwable;
		}

		Throwable t = throwable;

		// defend against (malicious?) circularity
		for (int i = 0; i < 1000; i++) {
			cause = t.getCause();
			if (cause == null) {
				return t;
			}
			t = cause;
		}

		return throwable;
	}

	/**
	 * Finds throwing cause in exception stack. Returns throwable object if cause class is matched.
	 * Otherwise, returns <code>null</code>.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T extends Throwable> T findCause(Throwable throwable, Class<T> cause) {
		while (throwable != null) {
			if (throwable.getClass().equals(cause)) {
				return (T) throwable;
			}
			throwable = throwable.getCause();
		}
		return null;
	}


	// ---------------------------------------------------------------- sql

	/**
     * Rolls up SQL exceptions by taking each proceeding exception
     * and making it a child of the previous using the <code>setNextException</code>
     * method of SQLException.
     */
	public static SQLException rollupSqlExceptions(Collection<SQLException> exceptions) {
		SQLException parent = null;
		for (SQLException exception : exceptions) {
			if (parent != null) {
				exception.setNextException(parent);
			}
			parent = exception;
		}
		return parent;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Throws target of <code>InvocationTargetException</code> if it is exception.
	 */
	public static void throwTargetException(InvocationTargetException itex) throws Exception {
		throw extractTargetException(itex);
	}
	public static Exception extractTargetException(InvocationTargetException itex) {
		Throwable target = itex.getTargetException();
		return target instanceof Exception ? (Exception) target : itex;
	}


	/**
	 * Throws checked exceptions in un-checked manner.
	 * Uses deprecated method.
	 * @see #throwException(Throwable)
	 */
	@SuppressWarnings({"deprecation"})
	public static void throwExceptionAlt(Throwable throwable) {
		if (throwable instanceof RuntimeException) {
			throw (RuntimeException) throwable;
		}
		Thread.currentThread().stop(throwable);
	}

	/**
	 * Throws checked exceptions in un-checked manner.
	 * @see #throwException(Throwable) 
	 */
	public static void throwException(Throwable throwable) {
		if (throwable instanceof RuntimeException) {
			throw (RuntimeException) throwable;
		}
		// can't handle these types
		if ((throwable instanceof IllegalAccessException) || (throwable instanceof InstantiationException)) {
			throw new IllegalArgumentException(throwable);
		}

		try {
			synchronized (ThrowableThrower.class) {
				ThrowableThrower.throwable = throwable;
				ThrowableThrower.class.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException iex) {
			throw new RuntimeException(iex);
		} finally {
			ThrowableThrower.throwable = null;
		}
	}

	/**
	 * Returns <code>non-null</code> message for a throwable.
	 */
	public static String message(Throwable throwable) {
		String message = throwable.getMessage();

		if (StringUtil.isBlank(message)) {
			message = throwable.toString();
		}

		return message;
	}

	/**
	 * Wraps exception to {@code RuntimeException}.
	 */
	public static RuntimeException wrapRuntime(Throwable throwable) {
		if (throwable instanceof RuntimeException) {
			return (RuntimeException) throwable;
		} else {
			return new RuntimeException(throwable);
		}
	}

	/**
	 * Unwraps invocation and undeclared exceptions to real cause.
	 */
	public static Throwable unwrap(Throwable wrapped) {
		Throwable unwrapped = wrapped;
		while (true) {
			if (unwrapped instanceof InvocationTargetException) {
				unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
			}
			else if (unwrapped instanceof UndeclaredThrowableException) {
				unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
			}
			else {
				return unwrapped;
			}
		}
	}

	private static class ThrowableThrower {
		private static Throwable throwable;
		ThrowableThrower() throws Throwable {
			throw throwable;
		}
	}
}
