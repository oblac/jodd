// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.ClassLoaderUtil;

/**
 * Converts given object to <code>Class</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>object of destination type is simply casted</li>
 * <li>string value of the object is trimmed and used for class loading.</li>
 * </ul>
 */
public class ClassConverter implements TypeConverter<Class> {

	public Class convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == Class.class) {
			return (Class) value;
		}
		try {
			return ClassLoaderUtil.loadClass(value.toString().trim());
		} catch (ClassNotFoundException cnfex) {
			throw new TypeConversionException(value, cnfex);
		}
	}

}