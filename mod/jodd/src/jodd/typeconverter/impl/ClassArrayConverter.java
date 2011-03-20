// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.ClassLoaderUtil;

/**
 * Converts given object to <code>Class</code> array.
 */
public class ClassArrayConverter implements TypeConverter<Class[]> {

	public static Class[] valueOf(Object value) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();
		if (type == Class[].class) {
			return (Class[]) value;
		}
		if (type == Class.class) {
			return new Class[] {(Class) value};
		}
		
		String[] allClassNames = StringArrayConverter.valueOf(value);
		int total = allClassNames.length;
		Class[] result = new Class[total];
		try {
			for (int i = 0; i < total; i++) {
				result[i] = ClassLoaderUtil.loadClass(allClassNames[i].trim(), ClassArrayConverter.class);
			}
		} catch (ClassNotFoundException cnfex) {
			throw new TypeConversionException(value, cnfex);
		}
		return result;	
	}

	public Class[] convert(Object value) {
		return valueOf(value);
	}
}
