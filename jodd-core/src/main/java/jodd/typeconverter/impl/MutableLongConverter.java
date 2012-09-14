// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableLong;
import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to a {@link MutableLong}.
 */
public class MutableLongConverter implements TypeConverter<MutableLong> {

	protected final ConvertBean convertBean;

	public MutableLongConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public MutableLong convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableLong.class) {
			return (MutableLong) value;
		}

		return new MutableLong(convertBean.toLongValue(value));
	}
}