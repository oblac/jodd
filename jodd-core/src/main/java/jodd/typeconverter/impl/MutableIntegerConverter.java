// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableInteger;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

/**
 * Converts given object to an {@link MutableInteger}.
 */
public class MutableIntegerConverter implements TypeConverter<MutableInteger> {

	protected final TypeConverter<Integer> typeConverter;

	@SuppressWarnings("unchecked")
	public MutableIntegerConverter(TypeConverterManagerBean typeConverterManagerBean) {
		typeConverter = typeConverterManagerBean.lookup(Integer.class);
	}

	public MutableInteger convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableInteger.class) {
			return (MutableInteger) value;
		}

		return new MutableInteger(typeConverter.convert(value));
	}

}