// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableByte;
import jodd.mutable.MutableDouble;
import jodd.mutable.MutableFloat;
import jodd.mutable.MutableInteger;
import jodd.mutable.MutableLong;
import jodd.mutable.MutableShort;
import jodd.servlet.upload.FileUpload;
import jodd.datetime.JDateTime;

import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.net.URL;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converter.
 */
public class TypeConverterManager {

	private static HashMap<Class, TypeConverter> converters = new HashMap<Class, TypeConverter>();

	static {
		registerDefaults();
	}

	/**
	 * Unregisters all converters.
	 */
	public static void unregisterAll() {
		converters.clear();
	}

	/**
	 * Registers default set of converters.
	 */
	public static void registerDefaults() {
		register(String.class, new StringConverter());
		register(String[].class, new StringArrayConverter());

		register(Integer.class, new IntegerConverter());
		register(int.class, new IntegerConverter());
		register(MutableInteger.class, new MutableIntegerConverter());

		register(Short.class, new ShortConverter());
		register(short.class, new ShortConverter());
		register(MutableShort.class, new MutableShortConverter());

		register(Long.class, new LongConverter());
		register(long.class, new LongConverter());
		register(MutableLong.class, new MutableLongConverter());

		register(Byte.class, new ByteConverter());
		register(byte.class, new ByteConverter());
		register(MutableByte.class, new MutableByteConverter());

		register(Float.class, new FloatConverter());
		register(float.class, new FloatConverter());
		register(MutableFloat.class, new MutableFloatConverter());

		register(Double.class, new DoubleConverter());
		register(double.class, new DoubleConverter());
		register(MutableDouble.class, new MutableDoubleConverter());

		register(Boolean.class, new BooleanConverter());
		register(boolean.class, new BooleanConverter());

		register(Character.class, new CharacterConverter());
		register(char.class, new CharacterConverter());

		register(byte[].class, new ByteArrayConverter());
		register(short[].class, new ShortArrayConverter());
		register(int[].class, new IntegerArrayConverter());
		register(long[].class, new LongArrayConverter());
		register(float[].class, new FloatArrayConverter());
		register(double[].class, new DoubleArrayConverter());
		register(boolean[].class, new BooleanArrayConverter());


		register(BigDecimal.class, new BigDecimalConverter());
		register(BigInteger.class, new BigIntegerConverter());
		register(Date.class, new SqlDateConverter());
		register(Time.class, new SqlTimeConverter());
		register(Timestamp.class, new SqlTimestampConverter());

		register(FileUpload.class, new FileUploadConverter());
		register(JDateTime.class, new JDateTimeConverter());
		register(Class.class, new ClassConverter());
		register(Class[].class, new ClassArrayConverter());
		register(URL.class, new URLConverter());
	}

	/**
	 * Registers a converter for specified type.
	 * User must register converter for all super-classes as well.
	 *
	 * @param type		class that converter is for
	 * @param typeConverter	converter for provided class
	 */
	public static void register(Class type, TypeConverter typeConverter) {
		converters.put(type, typeConverter);
	}


	public static void unregister(Class type) {
		converters.remove(type);
	}

	// ---------------------------------------------------------------- lookup

	/**
	 * Retrieves converter for provided type. Only registered types are matched,
	 * therefore subclasses must be also registered.
	 *
	 * @return founded converter or <code>null</code>
	 */
	public static TypeConverter lookup(Class type) {
		return converters.get(type);
	}


}
