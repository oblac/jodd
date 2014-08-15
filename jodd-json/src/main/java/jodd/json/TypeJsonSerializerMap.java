// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.datetime.JDateTime;
import jodd.json.impl.ArraysJsonSerializer;
import jodd.json.impl.BooleanArrayJsonSerializer;
import jodd.json.impl.BooleanJsonSerializer;
import jodd.json.impl.ByteArrayJsonSerializer;
import jodd.json.impl.CalendarJsonSerializer;
import jodd.json.impl.CharSequenceJsonSerializer;
import jodd.json.impl.CharacterJsonSerializer;
import jodd.json.impl.ClassJsonSerializer;
import jodd.json.impl.DateJsonSerializer;
import jodd.json.impl.DoubleArrayJsonSerializer;
import jodd.json.impl.EnumJsonSerializer;
import jodd.json.impl.FloatArrayJsonSerializer;
import jodd.json.impl.IntArrayJsonSerializer;
import jodd.json.impl.IterableJsonSerializer;
import jodd.json.impl.JDateTimeSerializer;
import jodd.json.impl.LongArrayJsonSerializer;
import jodd.json.impl.MapJsonSerializer;
import jodd.json.impl.NumberJsonSerializer;
import jodd.json.impl.ObjectJsonSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Map of {@link jodd.json.TypeJsonSerializer json type serializers}.
 */
public class TypeJsonSerializerMap {

	protected HashMap<Class, TypeJsonSerializer> map = new HashMap<Class, TypeJsonSerializer>();

	public void registerDefaults() {

		// main

		map.put(Object.class, new ObjectJsonSerializer());
		map.put(Map.class, new MapJsonSerializer());
		map.put(Iterable.class, new IterableJsonSerializer());

		// arrays
		map.put(int[].class, new IntArrayJsonSerializer());
		map.put(long[].class, new LongArrayJsonSerializer());
		map.put(double[].class, new DoubleArrayJsonSerializer());
		map.put(float[].class, new FloatArrayJsonSerializer());
		map.put(boolean[].class, new BooleanArrayJsonSerializer());
		map.put(byte[].class, new ByteArrayJsonSerializer());
		map.put(Arrays.class, new ArraysJsonSerializer());

		// strings

		TypeJsonSerializer jsonSerializer = new CharSequenceJsonSerializer();

		map.put(String.class, jsonSerializer);
		map.put(StringBuilder.class, jsonSerializer);
		map.put(CharSequence.class, jsonSerializer);

		// number

		jsonSerializer = new NumberJsonSerializer();

		map.put(Number.class, jsonSerializer);

		map.put(Integer.class, jsonSerializer);
		map.put(int.class, jsonSerializer);

		map.put(Long.class, jsonSerializer);
		map.put(long.class, jsonSerializer);

		map.put(Double.class, jsonSerializer);
		map.put(double.class, jsonSerializer);

		map.put(Float.class, jsonSerializer);
		map.put(float.class, jsonSerializer);

		map.put(BigInteger.class, jsonSerializer);
		map.put(BigDecimal.class, jsonSerializer);

		// other

		map.put(Boolean.class, new BooleanJsonSerializer());
		map.put(Date.class, new DateJsonSerializer());
		map.put(Calendar.class, new CalendarJsonSerializer());
		map.put(JDateTime.class, new JDateTimeSerializer());
		map.put(Enum.class, new EnumJsonSerializer());

		jsonSerializer = new CharacterJsonSerializer();

		map.put(Character.class, jsonSerializer);
		map.put(char.class, jsonSerializer);

		map.put(Class.class, new ClassJsonSerializer());
	}

	/**
	 * Registers new serializer.
	 */
	public void register(Class type, TypeJsonSerializer typeJsonSerializer) {
		map.put(type, typeJsonSerializer);
	}

	public TypeJsonSerializer lookup(Class type) {
		TypeJsonSerializer tjs = map.get(type);

		if (tjs != null) {
			return tjs;
		}

		if (type.isArray()) {
			return map.get(Arrays.class);
		}

		for (Class interfaze : type.getInterfaces()) {
			tjs = lookup(interfaze);

			if (tjs != null) {
				return tjs;
			}
		}

		Class superClass = type.getSuperclass();

		if (superClass == null) {
			return null;
		}

		if (type.isInterface() && superClass == Object.class) {
			return null;
		}

		return lookup(superClass);
	}

}