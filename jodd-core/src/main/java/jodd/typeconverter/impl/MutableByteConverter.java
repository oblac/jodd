// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter.impl;

import jodd.mutable.MutableByte;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManagerBean;

/**
 * Converts given object to {@link MutableByte}.
 */
public class MutableByteConverter implements TypeConverter<MutableByte> {

	protected final TypeConverter<Byte> typeConverter;

	@SuppressWarnings("unchecked")
	public MutableByteConverter(TypeConverterManagerBean typeConverterManagerBean) {
		typeConverter = typeConverterManagerBean.lookup(Byte.class);
	}

	public MutableByte convert(Object value) {
		if (value == null) {
			return null;
		}

		if (value.getClass() == MutableByte.class) {
			return (MutableByte) value;
		}

		return new MutableByte(typeConverter.convert(value));
	}

}