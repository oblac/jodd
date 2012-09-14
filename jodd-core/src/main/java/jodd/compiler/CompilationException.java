// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.compiler;

import java.io.IOException;

/**
 * Java compilation failure.
 */
public class CompilationException extends IOException {

	public CompilationException(String message) {
		super(message);
	}
	public CompilationException(Throwable cause) {
		super(cause.getMessage());
	}
}
