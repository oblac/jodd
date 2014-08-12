package jodd.json;

import jodd.json.impl.ArraysJsonSerializer;
import jodd.json.impl.BooleanJsonSerializer;
import jodd.json.impl.CalendarJsonSerializer;
import jodd.json.impl.DateJsonSerializer;
import jodd.json.impl.EnumJsonSerializer;
import jodd.json.impl.IterableJsonSerializer;
import jodd.json.impl.MapJsonSerializer;
import jodd.json.impl.NumberJsonSerializer;
import jodd.json.impl.ObjectJsonSerializer;
import jodd.json.impl.StringJsonSerializer;

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
		map.put(Arrays.class, new ArraysJsonSerializer());

		// strings

		TypeJsonSerializer jsonSerializer = new StringJsonSerializer();

		map.put(String.class, jsonSerializer);		// todo cover stringbuilder etc

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
		map.put(Enum.class, new EnumJsonSerializer());

		// todo char
		// todo class

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