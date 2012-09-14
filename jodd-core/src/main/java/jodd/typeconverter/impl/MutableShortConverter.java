// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableShort;
import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to {@link MutableShort}.
 */
public class MutableShortConverter implements TypeConverter<MutableShort> {

	protected final ConvertBean convertBean;

	public MutableShortConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public MutableShort convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableShort.class) {
			return (MutableShort) value;
		}

		return new MutableShort(convertBean.toShortValue(value));
	}

}