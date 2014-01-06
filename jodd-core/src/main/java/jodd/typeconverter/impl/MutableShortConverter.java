// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableShort;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

/**
 * Converts given object to {@link MutableShort}.
 */
public class MutableShortConverter implements TypeConverter<MutableShort> {

	protected final TypeConverter<Short> typeConverter;

	@SuppressWarnings("unchecked")
	public MutableShortConverter(TypeConverterManagerBean typeConverterManagerBean) {
		typeConverter = typeConverterManagerBean.lookup(Short.class);
	}

	public MutableShort convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableShort.class) {
			return (MutableShort) value;
		}

		return new MutableShort(typeConverter.convert(value));
	}

}