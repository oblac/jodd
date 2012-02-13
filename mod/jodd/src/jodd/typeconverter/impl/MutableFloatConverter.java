// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableFloat;
import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to {@link MutableFloat}.
 */
public class MutableFloatConverter implements TypeConverter<MutableFloat> {

	protected final ConvertBean convertBean;

	public MutableFloatConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public MutableFloat convert(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value.getClass() == MutableFloat.class) {
			return (MutableFloat) value;
		}

		return new MutableFloat(convertBean.toFloatValue(value));
	}

}