// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableByte;
import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to {@link MutableByte}.
 */
public class MutableByteConverter implements TypeConverter<MutableByte> {

	protected final ConvertBean convertBean;

	public MutableByteConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public MutableByte convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableByte.class) {
			return (MutableByte) value;
		}

		return new MutableByte(convertBean.toByteValue(value));
	}

}