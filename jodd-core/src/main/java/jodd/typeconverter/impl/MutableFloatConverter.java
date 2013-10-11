// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableFloat;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

/**
 * Converts given object to {@link MutableFloat}.
 */
public class MutableFloatConverter implements TypeConverter<MutableFloat> {

	protected final TypeConverter<Float> typeConverter;

	@SuppressWarnings("unchecked")
	public MutableFloatConverter(TypeConverterManagerBean typeConverterManagerBean) {
		typeConverter = typeConverterManagerBean.lookup(Float.class);
	}

	public MutableFloat convert(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value.getClass() == MutableFloat.class) {
			return (MutableFloat) value;
		}

		return new MutableFloat(typeConverter.convert(value));
	}

}