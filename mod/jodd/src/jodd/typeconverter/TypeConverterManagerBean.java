// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.mutable.MutableByte;
import jodd.mutable.MutableDouble;
import jodd.mutable.MutableFloat;
import jodd.mutable.MutableInteger;
import jodd.mutable.MutableLong;
import jodd.mutable.MutableShort;
import jodd.servlet.upload.FileUpload;
import jodd.typeconverter.impl.BigDecimalConverter;
import jodd.typeconverter.impl.BigIntegerConverter;
import jodd.typeconverter.impl.BooleanArrayConverter;
import jodd.typeconverter.impl.BooleanConverter;
import jodd.typeconverter.impl.ByteArrayConverter;
import jodd.typeconverter.impl.ByteConverter;
import jodd.typeconverter.impl.CalendarConverter;
import jodd.typeconverter.impl.CharacterArrayConverter;
import jodd.typeconverter.impl.CharacterConverter;
import jodd.typeconverter.impl.ClassArrayConverter;
import jodd.typeconverter.impl.ClassConverter;
import jodd.typeconverter.impl.DateConverter;
import jodd.typeconverter.impl.DoubleArrayConverter;
import jodd.typeconverter.impl.DoubleConverter;
import jodd.typeconverter.impl.FileConverter;
import jodd.typeconverter.impl.FileUploadConverter;
import jodd.typeconverter.impl.FloatArrayConverter;
import jodd.typeconverter.impl.FloatConverter;
import jodd.typeconverter.impl.IntegerArrayConverter;
import jodd.typeconverter.impl.IntegerConverter;
import jodd.typeconverter.impl.JDateTimeConverter;
import jodd.typeconverter.impl.LocaleConverter;
import jodd.typeconverter.impl.LongArrayConverter;
import jodd.typeconverter.impl.LongConverter;
import jodd.typeconverter.impl.MutableByteConverter;
import jodd.typeconverter.impl.MutableDoubleConverter;
import jodd.typeconverter.impl.MutableFloatConverter;
import jodd.typeconverter.impl.MutableIntegerConverter;
import jodd.typeconverter.impl.MutableLongConverter;
import jodd.typeconverter.impl.MutableShortConverter;
import jodd.typeconverter.impl.ShortArrayConverter;
import jodd.typeconverter.impl.ShortConverter;
import jodd.typeconverter.impl.SqlDateConverter;
import jodd.typeconverter.impl.SqlTimeConverter;
import jodd.typeconverter.impl.SqlTimestampConverter;
import jodd.typeconverter.impl.StringArrayConverter;
import jodd.typeconverter.impl.StringConverter;
import jodd.typeconverter.impl.URIConverter;
import jodd.typeconverter.impl.URLConverter;
import jodd.util.ReflectUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converters.
 * Instantiable version of {@link TypeConverterManager}.
 */
public class TypeConverterManagerBean {

	private final HashMap<Class, TypeConverter> converters = new HashMap<Class, TypeConverter>(64);

	// ---------------------------------------------------------------- converter

	protected ConvertBean convertBean = new ConvertBean();

	/**
	 * Returns {@link ConvertBean}.
	 */
	public ConvertBean getConvertBean() {
		return convertBean;
	}

	// ---------------------------------------------------------------- methods

	public TypeConverterManagerBean() {
		registerDefaults();
	}

	/**
	 * Registers default set of converters.
	 */
	@SuppressWarnings( {"UnnecessaryFullyQualifiedName"})
	public void registerDefaults() {
		register(String.class, new StringConverter());
		register(String[].class, new StringArrayConverter());

		IntegerConverter integerConverter = new IntegerConverter();
		register(Integer.class, integerConverter);
		register(int.class, integerConverter);
		register(MutableInteger.class, new MutableIntegerConverter());

		ShortConverter shortConverter = new ShortConverter();
		register(Short.class, shortConverter);
		register(short.class, shortConverter);
		register(MutableShort.class, new MutableShortConverter());

		LongConverter longConverter = new LongConverter();
		register(Long.class, longConverter);
		register(long.class, longConverter);
		register(MutableLong.class, new MutableLongConverter());

		ByteConverter byteConverter = new ByteConverter();
		register(Byte.class, byteConverter);
		register(byte.class, byteConverter);
		register(MutableByte.class, new MutableByteConverter());

		FloatConverter floatConverter = new FloatConverter();
		register(Float.class, floatConverter);
		register(float.class, floatConverter);
		register(MutableFloat.class, new MutableFloatConverter());

		DoubleConverter doubleConverter = new DoubleConverter();
		register(Double.class, doubleConverter);
		register(double.class, doubleConverter);
		register(MutableDouble.class, new MutableDoubleConverter());

		BooleanConverter booleanConverter = new BooleanConverter();
		register(Boolean.class, booleanConverter);
		register(boolean.class, booleanConverter);

		CharacterConverter characterConverter = new CharacterConverter();
		register(Character.class, characterConverter);
		register(char.class, characterConverter);

		register(byte[].class, new ByteArrayConverter(convertBean));
		register(short[].class, new ShortArrayConverter(convertBean));
		register(int[].class, new IntegerArrayConverter(convertBean));
		register(long[].class, new LongArrayConverter(convertBean));
		register(float[].class, new FloatArrayConverter(convertBean));
		register(double[].class, new DoubleArrayConverter(convertBean));
		register(boolean[].class, new BooleanArrayConverter(convertBean));
		register(char[].class, new CharacterArrayConverter(convertBean));

		register(BigDecimal.class, new BigDecimalConverter());
		register(BigInteger.class, new BigIntegerConverter());

		register(java.util.Date.class, new DateConverter());
		register(java.sql.Date.class, new SqlDateConverter());
		register(Time.class, new SqlTimeConverter());
		register(Timestamp.class, new SqlTimestampConverter());
		register(Calendar.class, new CalendarConverter());
		register(GregorianCalendar.class, new CalendarConverter());
		register(JDateTime.class, new JDateTimeConverter());

		register(FileUpload.class, new FileUploadConverter());
		register(File.class, new FileConverter());

		register(Class.class, new ClassConverter());
		register(Class[].class, new ClassArrayConverter(convertBean));

		register(URI.class, new URIConverter());
		register(URL.class, new URLConverter());

		register(Locale.class, new LocaleConverter());
	}

	/**
	 * Registers a converter for specified type.
	 * User must register converter for all super-classes as well.
	 *
	 * @param type		class that converter is for
	 * @param typeConverter	converter for provided class
	 */
	public void register(Class type, TypeConverter typeConverter) {
		convertBean.register(type, typeConverter);
		converters.put(type, typeConverter);
	}

	public void unregister(Class type) {
		convertBean.register(type, null);
		converters.remove(type);
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Retrieves converter for provided type. Only registered types are matched,
	 * therefore subclasses must be also registered.
	 *
	 * @return founded converter or <code>null</code>
	 */
	public TypeConverter lookup(Class type) {
		return converters.get(type);
	}

	// ---------------------------------------------------------------- convert
	
	/**
	 * Converts an object to destination type.If destination type is one of common types,
	 * consider using {@link jodd.typeconverter.Convert} instead for faster approach.
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T convertType(Object value, Class<T> destinationType) {
		TypeConverter converter = lookup(destinationType);

		if (converter != null) {
			try {
				return (T) converter.convert(value);
			} catch (TypeConversionException tcex) {
				throw new ClassCastException("Unable to convert to type: " + destinationType.getName() + '\n' + tcex.toString());
			}
		}

		// no converter

		if (value == null) {
			return null;
		}

		// check same instances
		if (ReflectUtil.isInstanceOf(value, destinationType) == true) {
			return (T) value;
		}

		// handle arrays
		if (destinationType.isArray()) {
			Class componentType = destinationType.getComponentType();

			// value is not an array
			if (value.getClass().isArray() == false) {
				// create single array
				T[] result = (T[]) Array.newInstance(componentType, 1);
				result[0] = (T) convertType(value, componentType);
				return (T) result;
			}

			// value is an array
			Object[] array = (Object[]) value;
			T[] result = (T[]) Array.newInstance(componentType, array.length);
			for (int i = 0; i < array.length; i++) {
				result[i] = (T) convertType(array[i], componentType);
			}
			return (T) result;
		}

		// handle enums
		if (destinationType.isEnum()) {
			Object[] enums = destinationType.getEnumConstants();
			String valStr = value.toString();
			for (Object e : enums) {
				if (e.toString().equals(valStr)) {
					return (T) e;
				}
			}
		}

		// fail
		throw new ClassCastException("Unable to cast to type: " + destinationType.getName());
	}

}