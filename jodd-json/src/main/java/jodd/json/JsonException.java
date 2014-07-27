// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.exception.UncheckedException;

/**
 * JSON exception.
 */
public class JsonException extends UncheckedException {

	public JsonException(String message) {
		super(message);
	}

	public JsonException(Throwable t) {
		super(t);
	}

	public JsonException(String message, Throwable t) {
		super(message, t);
	}
}