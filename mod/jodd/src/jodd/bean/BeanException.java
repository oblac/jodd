// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.exception.UncheckedException;

/**
 * Unchecked bean exception.
 */
public class BeanException extends UncheckedException {

	public BeanException(Throwable t) {
		super(t);
	}

	public BeanException() {
	}

	public BeanException(String message) {
		super(message);
	}

	public BeanException(String message, BeanProperty bp) {
		super(message + " Invalid property: " + bp);
	}

	public BeanException(String message, Throwable t) {
		super(message, t);
	}

	public BeanException(String message, BeanProperty bp, Throwable t) {
		super(message + " Invalid property: " + bp, t);
	}

}
