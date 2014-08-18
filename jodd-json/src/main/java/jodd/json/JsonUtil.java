// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.CtorDescriptor;
import jodd.typeconverter.TypeConverterManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Some common utilities.
 */
public class JsonUtil {

	/**
	 * Creates new object or a <code>HashMap</code> if type is not specified.
	 */
	public static Object newObjectInstance(Class targetType) {
		if (targetType == null) {
			return new HashMap();
		}

		if (targetType == Map.class) {
			return new HashMap();
		}

		ClassDescriptor cd = ClassIntrospector.lookup(targetType);

		CtorDescriptor ctorDescriptor = cd.getDefaultCtorDescriptor(true);
		if (ctorDescriptor == null) {
			throw new JsonException("Default ctor not found for: " + targetType.getClass().getName());
		}

		try {
			return ctorDescriptor.getConstructor().newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Converts type of the given value.
	 */
	public static Object convertType(Object value, Class targetType) {
		Class valueClass = value.getClass();

		if (valueClass == targetType) {
			return value;
		}

		try {
			return TypeConverterManager.convertType(value, targetType);
		}
		catch (Exception ex) {
			throw new JsonException("Type conversion failed", ex);
		}
	}


}