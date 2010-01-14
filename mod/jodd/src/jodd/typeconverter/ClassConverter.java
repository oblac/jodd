// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.util.ClassLoaderUtil;

/**
 * Converts given object to Class.
 */
public class ClassConverter implements TypeConverter<Class> {

	public static Class valueOf(Object value) {

		if (value == null) {
			return null;
		}
		if (value instanceof Class) {
			return (Class) value;
		}
		try {
			return ClassLoaderUtil.loadClass(value.toString().trim(), ClassConverter.class);
		} catch (ClassNotFoundException cnfex) {
			throw new TypeConversionException(value, cnfex);
		}
	}

	public Class convert(Object value) {
		return valueOf(value);
	}

}
