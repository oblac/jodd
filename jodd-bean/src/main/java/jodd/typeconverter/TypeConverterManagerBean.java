// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.mutable.MutableByte;
import jodd.mutable.MutableDouble;
import jodd.mutable.MutableFloat;
import jodd.mutable.MutableInteger;
import jodd.mutable.MutableLong;
import jodd.mutable.MutableShort;
import jodd.typeconverter.impl.ArrayConverter;
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
import jodd.typeconverter.impl.CollectionConverter;
import jodd.typeconverter.impl.DateConverter;
import jodd.typeconverter.impl.DoubleArrayConverter;
import jodd.typeconverter.impl.DoubleConverter;
import jodd.typeconverter.impl.FileConverter;
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
import jodd.typeconverter.impl.TimeZoneConverter;
import jodd.typeconverter.impl.URIConverter;
import jodd.typeconverter.impl.URLConverter;
import jodd.util.ReflectUtil;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides dynamic object conversion to a type.
 * Contains a map of registered converters. User may add new converters.
 * Instantiable version of {@link TypeConverterManager}.
 */
public class TypeConverterManagerBean {

	private final HashMap<Class, TypeConverter> converters = new HashMap<>(70);

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
		register(String[].class, new StringArrayConverter(this));

		IntegerConverter integerConverter = new IntegerConverter();
		register(Integer.class, integerConverter);
		register(int.class, integerConverter);
		register(MutableInteger.class, new MutableIntegerConverter(this));

		ShortConverter shortConverter = new ShortConverter();
		register(Short.class, shortConverter);
		register(short.class, shortConverter);
		register(MutableShort.class, new MutableShortConverter(this));

		LongConverter longConverter = new LongConverter();
		register(Long.class, longConverter);
		register(long.class, longConverter);
		register(MutableLong.class, new MutableLongConverter(this));

		ByteConverter byteConverter = new ByteConverter();
		register(Byte.class, byteConverter);
		register(byte.class, byteConverter);
		register(MutableByte.class, new MutableByteConverter(this));

		FloatConverter floatConverter = new FloatConverter();
		register(Float.class, floatConverter);
		register(float.class, floatConverter);
		register(MutableFloat.class, new MutableFloatConverter(this));

		DoubleConverter doubleConverter = new DoubleConverter();
		register(Double.class, doubleConverter);
		register(double.class, doubleConverter);
		register(MutableDouble.class, new MutableDoubleConverter(this));

		BooleanConverter booleanConverter = new BooleanConverter();
		register(Boolean.class, booleanConverter);
		register(boolean.class, booleanConverter);

		CharacterConverter characterConverter = new CharacterConverter();
		register(Character.class, characterConverter);
		register(char.class, characterConverter);

		register(byte[].class, new ByteArrayConverter(this));
		register(short[].class, new ShortArrayConverter(this));
		register(int[].class, new IntegerArrayConverter(this));
		register(long[].class, new LongArrayConverter(this));
		register(float[].class, new FloatArrayConverter(this));
		register(double[].class, new DoubleArrayConverter(this));
		register(boolean[].class, new BooleanArrayConverter(this));
		register(char[].class, new CharacterArrayConverter(this));

		// we don't really need these, but converters will be cached and not created every time
		register(Integer[].class, new ArrayConverter<Integer>(this, Integer.class) {
			@Override
			protected Integer[] createArray(int length) {
				return new Integer[length];
			}
		});
		register(Long[].class, new ArrayConverter<Long>(this, Long.class) {
			@Override
			protected Long[] createArray(int length) {
				return new Long[length];
			}
		});
		register(Byte[].class, new ArrayConverter<Byte>(this, Byte.class) {
			@Override
			protected Byte[] createArray(int length) {
				return new Byte[length];
			}
		});
		register(Short[].class, new ArrayConverter<Short>(this, Short.class) {
			@Override
			protected Short[] createArray(int length) {
				return new Short[length];
			}
		});
		register(Float[].class, new ArrayConverter<Float>(this, Float.class) {
			@Override
			protected Float[] createArray(int length) {
				return new Float[length];
			}
		});
		register(Double[].class, new ArrayConverter<Double>(this, Double.class) {
			@Override
			protected Double[] createArray(int length) {
				return new Double[length];
			}
		});
		register(Boolean[].class, new ArrayConverter<Boolean>(this, Boolean.class) {
			@Override
			protected Boolean[] createArray(int length) {
				return new Boolean[length];
			}
		});
		register(Character[].class, new ArrayConverter<Character>(this, Character.class) {
			@Override
			protected Character[] createArray(int length) {
				return new Character[length];
			}
		});

		register(MutableInteger[].class, new ArrayConverter<>(this, MutableInteger.class));
		register(MutableLong[].class, new ArrayConverter<>(this, MutableLong.class));
		register(MutableByte[].class, new ArrayConverter<>(this, MutableByte.class));
		register(MutableShort[].class, new ArrayConverter<>(this, MutableShort.class));
		register(MutableFloat[].class, new ArrayConverter<>(this, MutableFloat.class));
		register(MutableDouble[].class, new ArrayConverter<>(this, MutableDouble.class));

		register(BigDecimal.class, new BigDecimalConverter());
		register(BigInteger.class, new BigIntegerConverter());
		register(BigDecimal[].class, new ArrayConverter<>(this, BigDecimal.class));
		register(BigInteger[].class, new ArrayConverter<>(this, BigInteger.class));

		register(java.util.Date.class, new DateConverter());
		register(java.sql.Date.class, new SqlDateConverter());
		register(Time.class, new SqlTimeConverter());
		register(Timestamp.class, new SqlTimestampConverter());
		register(Calendar.class, new CalendarConverter());
		register(GregorianCalendar.class, new CalendarConverter());
		register(JDateTime.class, new JDateTimeConverter());

		register(File.class, new FileConverter());

		register(Class.class, new ClassConverter());
		register(Class[].class, new ClassArrayConverter(this));

		register(URI.class, new URIConverter());
		register(URL.class, new URLConverter());

		register(Locale.class, new LocaleConverter());
		register(TimeZone.class, new TimeZoneConverter());
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

	/**
	 * Un-registers converter for given type.
	 */
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
	 * Converts an object to destination type. If type is registered, it's
	 * {@link TypeConverter} will be used. If not, it scans of destination is
	 * an array or enum, as those two cases are handled in a special way.
	 * <p>
	 * If destination type is one of common types, consider using {@link jodd.typeconverter.Convert}
	 * instead for somewhat faster approach (no lookup).
	 */
	@SuppressWarnings({"unchecked"})
	public <T> T convertType(Object value, Class<T> destinationType) {
		if (destinationType == Object.class) {
			// no conversion :)
			return (T) value;
		}
		TypeConverter converter = lookup(destinationType);

		if (converter != null) {
			return (T) converter.convert(value);
		}

		// no converter

		if (value == null) {
			return null;
		}

		// handle destination arrays
		if (destinationType.isArray()) {
			ArrayConverter<T> arrayConverter = new ArrayConverter(this, destinationType.getComponentType());

			return (T) arrayConverter.convert(value);
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

		// check same instances
		if (ReflectUtil.isInstanceOf(value, destinationType)) {
			return (T) value;
		}

		// collection
		if (ReflectUtil.isTypeOf(destinationType, Collection.class)) {
			// component type is unknown because of Java's type-erasure
			CollectionConverter<T> collectionConverter =
					new CollectionConverter(this, destinationType, Object.class);

			return (T) collectionConverter.convert(value);
		}

		// fail
		throw new TypeConversionException("Conversion failed: " + destinationType.getName());
	}

	/**
	 * Special case of {@link #convertType(Object, Class)} when target is collection and
	 * when component type is known.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> convertToCollection(Object value, Class<? extends Collection> destinationType, Class componentType) {
		if (value == null) {
			return null;
		}

		// check same instances
		if (ReflectUtil.isInstanceOf(value, destinationType)) {
			return (Collection<T>) value;
		}

		if (componentType == null) {
			componentType = Object.class;
		}

		CollectionConverter collectionConverter = new CollectionConverter(destinationType, componentType);

		return collectionConverter.convert(value);
	}

}