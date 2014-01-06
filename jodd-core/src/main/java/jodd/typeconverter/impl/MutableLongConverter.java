// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableLong;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

/**
 * Converts given object to a {@link MutableLong}.
 */
public class MutableLongConverter implements TypeConverter<MutableLong> {

	protected final TypeConverter<Long> typeConverter;

	@SuppressWarnings("unchecked")
	public MutableLongConverter(TypeConverterManagerBean typeConverterManagerBean) {
		typeConverter = typeConverterManagerBean.lookup(Long.class);
	}

	public MutableLong convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableLong.class) {
			return (MutableLong) value;
		}

		return new MutableLong(typeConverter.convert(value));
	}
}