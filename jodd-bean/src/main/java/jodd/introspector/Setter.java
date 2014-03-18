// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.InvocationTargetException;

/**
 * Unified setter property interface for both methods and fields.
 */
public interface Setter {

	void invokeSetter(Object target, Object argument) throws IllegalAccessException, InvocationTargetException;

	Class getSetterRawType();

	Class getSetterRawComponentType();

}