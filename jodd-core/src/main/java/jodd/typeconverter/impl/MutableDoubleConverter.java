// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableDouble;
import jodd.typeconverter.ConvertBean;
import jodd.typeconverter.TypeConverter;

/**
 * Converts given object to {@link MutableDouble}.
 */
public class MutableDoubleConverter implements TypeConverter<MutableDouble> {

	protected final ConvertBean convertBean;

	public MutableDoubleConverter(ConvertBean convertBean) {
		this.convertBean = convertBean;
	}

	public MutableDouble convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableDouble.class) {
			return (MutableDouble) value;
		}

		return new MutableDouble(convertBean.toDoubleValue(value));
	}

}