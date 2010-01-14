// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.exception.UncheckedException;

public class TypeConversionException extends UncheckedException {

	public TypeConversionException(Throwable t) {
		super(t);
	}

	public TypeConversionException() {
		super();
	}

	public TypeConversionException(String message) {
		super(message);
	}

	public TypeConversionException(String message, Throwable t) {
		super(message, t);
	}

	public TypeConversionException(Object value) {
		this("Unable to convert value: '" + value + "'.");
	}

	public TypeConversionException(Object value, Throwable t) {
		this("Unable to convert value: '" + value + "'.", t);
	}
}
