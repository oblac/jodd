package jodd.typeconverter.impl;

import jodd.typeconverter.TypeConverterManagerBean;
import jodd.util.CsvUtil;

/**
 * Converts given object to number array. It is designed for all numeric types.
 * Extends {@link ArrayConverter} and modifies situation when source is not an array
 * and a <code>String</code>. In that case, string is parsed as CSV.
 */
public class ArrayNumberConverter<T> extends ArrayConverter<T> {

	public ArrayNumberConverter(TypeConverterManagerBean typeConverterManagerBean, Class<T> targetComponentType) {
		super(typeConverterManagerBean, targetComponentType);
	}

	public T[] convert(Object value) {
		if (value == null) {
			return null;
		}

		Class valueClass = value.getClass();

		// source value itself is not an array
		if (valueClass.isArray() == false) {
			if (valueClass == String.class) {
				value = CsvUtil.toStringArray(value.toString());
			}
			else {
				return convertToSingleElementArray(value);
			}
		}

		return convertArray(value);
	}
}
