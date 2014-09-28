// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.watch;

import jodd.exception.UncheckedException;

/**
 * Exception for {@link jodd.io.watch.DirWatcher}.
 */
public class DirWatcherException extends UncheckedException {

	public DirWatcherException(String message) {
		super(message);
	}

	public DirWatcherException(String message, Throwable t) {
		super(message, t);
	}
}