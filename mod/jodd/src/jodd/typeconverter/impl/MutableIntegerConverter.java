// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableInteger;
import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to an {@link MutableInteger}.
 */
public class MutableIntegerConverter implements TypeConverter<MutableInteger> {

	protected final ConvertBean convertBean;

	public MutableIntegerConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public MutableInteger convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableInteger.class) {
			return (MutableInteger) value;
		}

		return new MutableInteger(convertBean.toIntValue(value));
	}

}