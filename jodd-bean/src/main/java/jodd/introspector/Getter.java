// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.InvocationTargetException;

/**
 * Unified getter property interface for both methods and fields.
 */
public interface Getter {

	Object invokeGetter(Object target) throws InvocationTargetException, IllegalAccessException;

	Class getGetterRawType();

	Class getGetterRawComponentType();

	Class getGetterRawKeyComponentType();

}