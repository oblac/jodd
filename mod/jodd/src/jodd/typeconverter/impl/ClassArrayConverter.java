// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to <code>Class</code> array.
 * Conversion rules:
 * <li><code>null</code> value is returned as <code>null</code>
 * <li>object of destination type is simply casted
 * <li>single Class is wrapped in 1-length array
 * <li>string value is converted to string array (from CSV format) and
 * then each element is converted
 */
public class ClassArrayConverter implements TypeConverter<Class[]> {

	protected final ConvertBean convertBean;

	public ClassArrayConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public Class[] convert(Object value) {
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
		
		String[] classNames = convertBean.toStringArray(value);
		Class[] result = new Class[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			result[i] = convertBean.toClass(classNames[i]);
		}
		return result;
	}

}