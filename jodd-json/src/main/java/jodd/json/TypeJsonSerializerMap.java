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

package jodd.json;

import jodd.datetime.JDateTime;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
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
import jodd.json.impl.FileJsonSerializer;
import jodd.json.impl.FloatArrayJsonSerializer;
import jodd.json.impl.IntArrayJsonSerializer;
import jodd.json.impl.IterableJsonSerializer;
import jodd.json.impl.JDateTimeSerializer;
import jodd.json.impl.JsonArraySerializer;
import jodd.json.impl.JsonObjectSerializer;
import jodd.json.impl.LongArrayJsonSerializer;
import jodd.json.impl.MapJsonSerializer;
import jodd.json.impl.NumberJsonSerializer;
import jodd.json.impl.ObjectJsonSerializer;
import jodd.util.collection.ClassMap;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Map of {@link jodd.json.TypeJsonSerializer json type serializers}.
 */
public class TypeJsonSerializerMap {

	private final TypeJsonSerializerMap defaultSerializerMap;

	/**
	 * Creates new serializers map and registers defaults.
	 */
	public TypeJsonSerializerMap() {
		registerDefaults();
		defaultSerializerMap = null;
	}

	/**
	 * Creates new empty serializer map with given defaults map.
	 */
	public TypeJsonSerializerMap(TypeJsonSerializerMap defaultSerializerMap) {
		this.defaultSerializerMap = defaultSerializerMap;
	}

	protected final ClassMap<TypeJsonSerializer> map = new ClassMap<>();
	protected final ClassMap<TypeJsonSerializer> cache = new ClassMap<>();

	/**
	 * Registers default set of {@link jodd.json.TypeJsonSerializer serializers}.
	 */
	public void registerDefaults() {

		// main

		map.put(Object.class, new ObjectJsonSerializer());
		map.put(Map.class, new MapJsonSerializer());
		map.put(Iterable.class, new IterableJsonSerializer());

		map.put(JsonObject.class, new JsonObjectSerializer());
		map.put(JsonArray.class, new JsonArraySerializer());

		// arrays
		map.put(int[].class, new IntArrayJsonSerializer());
		map.put(long[].class, new LongArrayJsonSerializer());
		map.put(double[].class, new DoubleArrayJsonSerializer());
		map.put(float[].class, new FloatArrayJsonSerializer());
		map.put(boolean[].class, new BooleanArrayJsonSerializer());
		map.put(byte[].class, new ByteArrayJsonSerializer());

		map.put(Integer[].class, new ArraysJsonSerializer<Integer>() {
			@Override
			protected int getLength(Integer[] array) {
				return array.length;
			}

			@Override
			protected Integer get(Integer[] array, int index) {
				return array[index];
			}
		});
		map.put(Long[].class, new ArraysJsonSerializer<Long>() {
			@Override
			protected int getLength(Long[] array) {
				return array.length;
			}

			@Override
			protected Long get(Long[] array, int index) {
				return array[index];
			}
		});
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
		map.put(File.class, new FileJsonSerializer(FileJsonSerializer.Type.PATH));

		//map.put(Collection.class, new CollectionJsonSerializer());

		jsonSerializer = new CharacterJsonSerializer();

		map.put(Character.class, jsonSerializer);
		map.put(char.class, jsonSerializer);

		map.put(Class.class, new ClassJsonSerializer());

		// clear cache
		cache.clear();
	}

	/**
	 * Registers new serializer.
	 */
	public void register(Class type, TypeJsonSerializer typeJsonSerializer) {
		map.put(type, typeJsonSerializer);
		cache.clear();
	}

	/**
	 * Lookups for the {@link jodd.json.TypeJsonSerializer serializer} for given type.
	 * If serializer not found, then all interfaces and subclasses of the type are checked.
	 * Finally, if no serializer is found, object's serializer is returned.
	 */
	public TypeJsonSerializer lookup(Class type) {
		TypeJsonSerializer tjs = cache.unsafeGet(type);

		if (tjs != null) {
			return tjs;
		}

		tjs = _lookup(type);

		cache.put(type, tjs);

		return tjs;
	}

	/**
	 * Get type serializer from map. First the current map is used.
	 * If element is missing, default map will be used, if exist.
	 */
	protected TypeJsonSerializer lookupSerializer(Class type) {
		TypeJsonSerializer tjs = map.unsafeGet(type);

		if (tjs == null) {
			if (defaultSerializerMap != null) {
				tjs = defaultSerializerMap.map.unsafeGet(type);
			}
		}

		return tjs;
	}

	protected TypeJsonSerializer _lookup(Class type) {
		synchronized (map) {
			TypeJsonSerializer tjs = lookupSerializer(type);

			if (tjs != null) {
				return tjs;
			}

			ClassDescriptor cd = ClassIntrospector.lookup(type);

			// check array

			if (cd.isArray()) {
				return lookupSerializer(Arrays.class);
			}

			// now iterate interfaces

			Class[] interfaces = cd.getAllInterfaces();

			for (Class interfaze : interfaces) {
				tjs = lookupSerializer(interfaze);

				if (tjs != null) {
					return tjs;
				}
			}

			// now iterate all superclases

			Class[] superclasses = cd.getAllSuperclasses();

			for (Class clazz : superclasses) {
				tjs = lookupSerializer(clazz);

				if (tjs != null) {
					return tjs;
				}
			}

			// nothing found, go with the Object

			return lookupSerializer(Object.class);
		}
	}

}