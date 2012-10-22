// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.JoddCore;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.util.CsvUtil;

import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Converts given object to <code>String</code>.
 * Conversion rules:
 * <ul>
 * <li><code>null</code> value is returned as <code>null</code></li>
 * <li>for <code>CharSequence</code> type returns toString value</li>
 * <li><code>Class</code> returns cass name</li>
 * <li><code>byte[]</code> is used for creating UTF8 string</li>
 * <li><code>char[]</code> is used for creating string</li>
 * <li><code>Clob</code> is converted</li>
 * <li>finally, <code>toString()</code> value is returned.</li>
 * </ul>
 */
public class StringConverter implements TypeConverter<String> {

	public String convert(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof CharSequence) {	// for speed
			return value.toString();
		}
		Class type = value.getClass();
		if (type == Class.class) {
			return ((Class) value).getName();
		}
		if (type.isArray()) {
			if (type == byte[].class) {
				byte[] valueArray = (byte[]) value;
				try {
					return new String(valueArray, 0, valueArray.length, JoddCore.encoding);
				} catch (UnsupportedEncodingException ueex) {
					throw new TypeConversionException(ueex);
				}
			}
			if (type == char[].class) {
				char[] charArray = (char[]) value;
				return new String(charArray);
			}
			return CsvUtil.toCsvString((Object[])value);
		}
		if (value instanceof Clob) {
			Clob clob = (Clob) value;
			try {
				long length = clob.length();
				if (length > Integer.MAX_VALUE) {
					throw new TypeConversionException("Clob is too big.");
				}
				return clob.getSubString(1, (int) length);
			} catch (SQLException sex) {
				throw new TypeConversionException(value, sex);
			}
		}
		return value.toString();
	}

}
