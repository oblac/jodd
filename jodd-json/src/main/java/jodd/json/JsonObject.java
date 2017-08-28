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

import jodd.util.collection.MapEntry;

import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Representation of JSON object.
 * @see JsonArray
 */
public class JsonObject implements Iterable<Map.Entry<String, Object>> {

	private Map<String, Object> map;

	/**
	 * Create a new, empty instance.
	 */
	public JsonObject() {
		map = new LinkedHashMap<>();
	}

	/**
	 * Create an instance from a Map. The Map is not copied.
	 */
	public JsonObject(Map<String, Object> map) {
		this.map = map;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns the string value with the specified key.
	 */
	public String getString(String key) {
		CharSequence cs = (CharSequence) map.get(key);
		return cs == null ? null : cs.toString();
	}

	/**
	 * returns the integer value with the specified key.
	 */
	public Integer getInteger(String key) {
		Number number = (Number) map.get(key);

		if (number == null) {
			return null;
		}
		if (number instanceof Integer) {
			return (Integer) number;
		}
		return number.intValue();
	}

	/**
	 * Returns the long value with the specified key.
	 */
	public Long getLong(String key) {
		Number number = (Number) map.get(key);

		if (number == null) {
			return null;
		}
		if (number instanceof Long) {
			return (Long) number;
		}
		return number.longValue();
	}

	/**
	 * Returns the double value with the specified key.
	 */
	public Double getDouble(String key) {
		Number number = (Number) map.get(key);

		if (number == null) {
			return null;
		}
		if (number instanceof Double) {
			return (Double) number;
		}
		return number.doubleValue();
	}

	/**
	 * Returns the float value with the specified key.
	 */
	public Float getFloat(String key) {
		Number number = (Number) map.get(key);

		if (number == null) {
			return null;
		}
		if (number instanceof Float) {
			return (Float) number;
		}
		return number.floatValue();
	}

	/**
	 * Returns the boolean value with the specified key.
	 */
	public Boolean getBoolean(String key) {
		return (Boolean) map.get(key);
	}

	/**
	 * Returns the {@code JsonObject} value with the specified key.
	 */
	public JsonObject getJsonObject(String key) {
		Object val = map.get(key);

		if (val instanceof Map) {
			val = new JsonObject((Map) val);
		}
		return (JsonObject) val;
	}

	/**
	 * Returns the {@link JsonArray} value with the specified key
	 */
	public JsonArray getJsonArray(String key) {
		Object val = map.get(key);

		if (val instanceof List) {
			val = new JsonArray((List) val);
		}
		return (JsonArray) val;
	}

	/**
	 * Returns the binary value with the specified key.
	 * <p>
	 * JSON itself has no notion of a binary. This extension complies to the RFC-7493.
	 * THe byte array is Base64 encoded binary.
	 */
	public byte[] getBinary(String key) {
		String encoded = (String) map.get(key);
		return encoded == null ? null : Base64.getDecoder().decode(encoded);
	}

	/**
	 * Returns the value with the specified key, as an object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		T val = (T) map.get(key);

		if (val instanceof Map) {
			return (T) new JsonObject((Map) val);
		}
		if (val instanceof List) {
			return (T) new JsonArray((List) val);
		}
		return val;
	}

	// ---------------------------------------------------------------- get + default

	/**
	 * Like {@link #getString(String)} but specifies a default value to return if there is no entry.
	 */
	public String getString(String key, String def) {
		String val = getString(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	/**
	 * Like {@link #getInteger(String)} but specifies a default value to return if there is no entry.
	 */
	public Integer getInteger(String key, Integer def) {
		Integer val = getInteger(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	/**
	 * Like {@link #getLong(String)} but specifies a default value to return if there is no entry.
	 */
	public Long getLong(String key, Long def) {
		Long val = getLong(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	/**
	 * Like {@link #getDouble(String)} but specifies a default value to return if there is no entry.
	 */
	public Double getDouble(String key, Double def) {
		Double val = getDouble(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	/**
	 * Like {@link #getFloat(String)} but specifies a default value to return if there is no entry.
	 */
	public Float getFloat(String key, Float def) {
		Float val = getFloat(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	/**
	 * Like {@link #getBoolean(String)} but specifies a default value to return if there is no entry.
	 */
	public Boolean getBoolean(String key, Boolean def) {
		Boolean val = getBoolean(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	/**
	 * Like {@link #getJsonObject(String)} but specifies a default value to return if there is no entry.
	 */
	public JsonObject getJsonObject(String key, JsonObject def) {
		JsonObject val = getJsonObject(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	/**
	 * Like {@link #getJsonArray(String)} but specifies a default value to return if there is no entry.
	 */
	public JsonArray getJsonArray(String key, JsonArray def) {
		JsonArray val = getJsonArray(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;

	}

	/**
	 * Like {@link #getBinary(String)} but specifies a default value to return if there is no entry.
	 */
	public byte[] getBinary(String key, byte[] def) {
		byte[] val = getBinary(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;

	}

	/**
	 * Like {@link #getValue(String)} but specifies a default value to return if there is no entry.
	 */
	public <T> T getValue(String key, T def) {
		T val = getValue(key);

		if (val == null) {
			if (map.containsKey(key)) {
				return null;
			}
			return def;
		}

		return val;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Returns {@code true} if the JSON object contain the specified key.
	 */
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	/**
	 * Return the set of field names in the JSON objects.
	 */
	public Set<String> fieldNames() {
		return map.keySet();
	}

	// ---------------------------------------------------------------- put

	/**
	 * Puts an Enum into the JSON object with the specified key.
	 * <p>
	 * JSON has no concept of encoding Enums, so the Enum will be converted to a String using the {@code java.lang.Enum#name}
	 * method and the value put as a String.
	 */
	public JsonObject put(String key, Enum value) {
		Objects.requireNonNull(key);
		map.put(key, value == null ? null : value.name());
		return this;
	}

	/**
	 * Puts an {@code CharSequence} into the JSON object with the specified key.
	 */
	public JsonObject put(String key, CharSequence value) {
		Objects.requireNonNull(key);
		map.put(key, value == null ? null : value.toString());
		return this;
	}

	/**
	 * Puts a string into the JSON object with the specified key.
	 */
	public JsonObject put(String key, String value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts an integer into the JSON object with the specified key.
	 */
	public JsonObject put(String key, Integer value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts a long into the JSON object with the specified key.
	 */
	public JsonObject put(String key, Long value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts a double into the JSON object with the specified key.
	 */
	public JsonObject put(String key, Double value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts a float into the JSON object with the specified key.
	 */
	public JsonObject put(String key, Float value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts a boolean into the JSON object with the specified key.
	 */
	public JsonObject put(String key, Boolean value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts a {@code null} value into the JSON object with the specified key.
	 */
	public JsonObject putNull(String key) {
		Objects.requireNonNull(key);
		map.put(key, null);
		return this;
	}

	/**
	 * Puts another JSON object into the JSON object with the specified key.
	 */
	public JsonObject put(String key, JsonObject value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts a {@link JsonArray} into the JSON object with the specified key.
	 */
	public JsonObject put(String key, JsonArray value) {
		Objects.requireNonNull(key);
		map.put(key, value);
		return this;
	}

	/**
	 * Puts a {@code byte[]} into the JSON object with the specified key.
	 * <p>
	 * Follows JSON extension RFC7493, where binary will first be Base64
	 * encoded before being put as a String.
	 */
	public JsonObject put(String key, byte[] value) {
		Objects.requireNonNull(key);
		map.put(key, value == null ? null : Base64.getEncoder().encodeToString(value));
		return this;
	}

	/**
	 * Puts an object into the JSON object with the specified key.
	 */
	public JsonObject put(String key, Object value) {
		Objects.requireNonNull(key);

		value = resolveValue(value);

		map.put(key, value);
		return this;
	}

	@SuppressWarnings("StatementWithEmptyBody")
	static Object resolveValue(Object value) {
		if (value == null) {
			// OK
		}
		else if (value instanceof Number) {
			// OK
		}
		else if (value instanceof Boolean) {
			// OK
		}
		else if (value instanceof String) {
			// OK
		}
		else if (value instanceof Character) {
			// OK
		}
		else if (value instanceof CharSequence) {
			value = value.toString();
		}
		else if (value instanceof JsonObject) {
			// OK
		}
		else if (value instanceof JsonArray) {
			// OK
		}
		else if (value instanceof Map) {
			value = new JsonObject((Map) value);
		}
		else if (value instanceof List) {
			value = new JsonArray((List) value);
		}
		else if (value instanceof byte[]) {
			value = Base64.getEncoder().encodeToString((byte[]) value);
		}
		else {
			throw new JsonException("Illegal JSON type: " + value.getClass());
		}

		return value;
	}


	// ---------------------------------------------------------------- remove

	/**
	 * Removes an entry from this object.
	 */
	public Object remove(String key) {
		return map.remove(key);
	}

	// ---------------------------------------------------------------- merge

	/**
	 * Merges in another JSON object.
	 * <p>
	 * This is the equivalent of putting all the entries of the other JSON object into this object. This is not a deep
	 * merge, entries containing (sub) JSON objects will be replaced entirely.
	 */
	public JsonObject mergeIn(JsonObject other) {
		return mergeIn(other, 1);
	}

	/**
	 * Merges in another JSON object.
	 * A deep merge (recursive) matches (sub) JSON objects in the existing tree and replaces all
	 * matching entries. JsonArrays are treated like any other entry, i.e. replaced entirely.
	 */
	public JsonObject mergeInDeep(JsonObject other) {
		return mergeIn(other, Integer.MAX_VALUE);
	}

	/**
	 * Merges in another JSON object.
	 * The merge is deep (recursive) to the specified level. If depth is 0, no merge is performed,
	 * if depth is greater than the depth of one of the objects, a full deep merge is performed.
	 */
	@SuppressWarnings("unchecked")
	public JsonObject mergeIn(JsonObject other, int depth) {
		if (depth < 1) {
			return this;
		}
		if (depth == 1) {
			map.putAll(other.map);
			return this;
		}

		for (Map.Entry<String, Object> e : other.map.entrySet()) {
			map.merge(e.getKey(), e.getValue(), (oldVal, newVal) -> {
				if (oldVal instanceof Map) {
					oldVal = new JsonObject((Map) oldVal);
				}
				if (newVal instanceof Map) {
					newVal = new JsonObject((Map) newVal);
				}
				if (oldVal instanceof JsonObject && newVal instanceof JsonObject) {
					return ((JsonObject) oldVal).mergeIn((JsonObject) newVal, depth - 1);
				}
				return newVal;
			});
		}
		return this;
	}

	// ---------------------------------------------------------------- encode

	/**
	 * Returns the underlying {@code Map} as is.
	 */
	public Map<String, Object> map() {
		return map;
	}

	/**
	 * Returns a stream of the entries in the JSON object.
	 */
	public Stream<Map.Entry<String, Object>> stream() {
		return map.entrySet().stream();
	}

	/**
	 * Returns an iterator of the entries in the JSON object.
	 */
	@Override
	public Iterator<Map.Entry<String, Object>> iterator() {
		return new Iter(map.entrySet().iterator());
	}

	/**
	 * Returns the number of entries in the JSON object.
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Removes all the entries in this JSON object.
	 */
	public JsonObject clear() {
		map.clear();
		return this;
	}

	/**
	 * Returns {@code true} if JSON object is empty.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns JSON serialized by default {@link JsonSerializer}.
	 */
	@Override
	public String toString() {
		return JsonSerializer.create().deep(true).serialize(map);
	}

	// ---------------------------------------------------------------- equals/hash

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return objectEquals(map, o);
	}

	static boolean objectEquals(Map<?, ?> m1, Object o2) {
		Map<?, ?> m2;
		if (o2 instanceof JsonObject) {
			m2 = ((JsonObject) o2).map;
		} else if (o2 instanceof Map<?, ?>) {
			m2 = (Map<?, ?>) o2;
		} else {
			return false;
		}
		if (m1.size() != m2.size()) {
			return false;
		}
		for (Map.Entry<?, ?> entry : m1.entrySet()) {
			Object val = entry.getValue();
			if (val == null) {
				if (m2.get(entry.getKey()) != null) {
					return false;
				}
			} else {
				if (!elementEquals(entry.getValue(), m2.get(entry.getKey()))) {
					return false;
				}
			}
		}
		return true;
	}

	static boolean elementEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 instanceof JsonObject) {
			return objectEquals(((JsonObject) o1).map, o2);
		}
		if (o1 instanceof Map<?, ?>) {
			return objectEquals((Map<?, ?>) o1, o2);
		}
		if (o1 instanceof JsonArray) {
			return JsonArray.arrayEquals(((JsonArray) o1).list(), o2);
		}
		if (o1 instanceof List<?>) {
			return JsonArray.arrayEquals((List<?>) o1, o2);
		}
		if (o1 instanceof Number && o2 instanceof Number && o1.getClass() != o2.getClass()) {
			Number n1 = (Number) o1;
			Number n2 = (Number) o2;
			if (o1 instanceof Float || o1 instanceof Double || o2 instanceof Float || o2 instanceof Double) {
				return n1.doubleValue() == n2.doubleValue();
			} else {
				return n1.longValue() == n2.longValue();
			}
		}
		return o1.equals(o2);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	// ---------------------------------------------------------------- iterator

	/**
	 * Iterator over values that handles maps and lists and wraps them in the JSON objects.
	 */
	private class Iter implements Iterator<Map.Entry<String, Object>> {

		final Iterator<Map.Entry<String, Object>> mapIterator;

		Iter(Iterator<Map.Entry<String, Object>> mapIterator) {
			this.mapIterator = mapIterator;
		}

		@Override
		public boolean hasNext() {
			return mapIterator.hasNext();
		}

		@Override
		public Map.Entry<String, Object> next() {
			Map.Entry<String, Object> entry = mapIterator.next();

			if (entry.getValue() instanceof Map) {
				return MapEntry.createUnmodifiable(entry.getKey(), new JsonObject((Map) entry.getValue()));
			}
			if (entry.getValue() instanceof List) {
				return MapEntry.createUnmodifiable(entry.getKey(), new JsonArray((List) entry.getValue()));
			}

			return entry;
		}

		@Override
		public void remove() {
			mapIterator.remove();
		}
	}
}