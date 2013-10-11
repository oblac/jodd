// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableDouble;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

/**
 * Converts given object to {@link MutableDouble}.
 */
public class MutableDoubleConverter implements TypeConverter<MutableDouble> {

	protected final TypeConverter<Double> typeConverter;

	@SuppressWarnings("unchecked")
	public MutableDoubleConverter(TypeConverterManagerBean typeConverterManagerBean) {
		typeConverter = typeConverterManagerBean.lookup(Double.class);
	}

	public MutableDouble convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableDouble.class) {
			return (MutableDouble) value;
		}

		return new MutableDouble(typeConverter.convert(value));
	}

}