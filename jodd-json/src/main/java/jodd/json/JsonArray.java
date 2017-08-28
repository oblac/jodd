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

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Representation of JSON array.
 * @see JsonObject
 */
public class JsonArray implements Iterable<Object> {

	private List<Object> list;

	/**
	 * Creates an empty instance.
	 */
	public JsonArray() {
		list = new ArrayList<>();
	}

	/**
	 * Creates an instance from a List. The List is not copied.
	 */
	public JsonArray(List list) {
		this.list = list;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns the string at position {@code pos} in the array.
	 */
	public String getString(int pos) {
		CharSequence cs = (CharSequence) list.get(pos);
		return cs == null ? null : cs.toString();
	}

	/**
	 * Returns the integer at position {@code pos} in the array.
	 */
	public Integer getInteger(int pos) {
		Number number = (Number) list.get(pos);

		if (number == null) {
			return null;
		}
		if (number instanceof Integer) {
			// avoid unnecessary unbox/box
			return (Integer) number;
		}
		return number.intValue();
	}

	/**
	 * Returns the long at position {@code pos} in the array.
	 */
	public Long getLong(int pos) {
		Number number = (Number) list.get(pos);
		if (number == null) {
			return null;
		}
		if (number instanceof Long) {
			// avoids unnecessary unbox/box
			return (Long) number;
		}
		return number.longValue();
	}

	/**
	 * Returns the double at position {@code pos} in the array.
	 */
	public Double getDouble(int pos) {
		Number number = (Number) list.get(pos);
		if (number == null) {
			return null;
		}
		if (number instanceof Double) {
			// avoids unnecessary unbox/box
			return (Double) number;
		}
		return number.doubleValue();
	}

	/**
	 * Returns the Float at position {@code pos} in the array.
	 */
	public Float getFloat(int pos) {
		Number number = (Number) list.get(pos);
		if (number == null) {
			return null;
		}
		if (number instanceof Float) {
			// avoids unnecessary unbox/box
			return (Float) number;
		}
		return number.floatValue();
	}

	/**
	 * Returns the boolean at position {@code pos} in the array.
	 */
	public Boolean getBoolean(int pos) {
		return (Boolean) list.get(pos);
	}

	/**
	 * Retruns the JsonObject at position {@code pos} in the array.
	 */
	public JsonObject getJsonObject(int pos) {
		Object val = list.get(pos);
		if (val instanceof Map) {
			val = new JsonObject((Map) val);
		}
		return (JsonObject) val;
	}

	/**
	 * Returns the JsonArray at position {@code pos} in the array.
	 */
	public JsonArray getJsonArray(int pos) {
		Object val = list.get(pos);
		if (val instanceof List) {
			val = new JsonArray((List) val);
		}
		return (JsonArray) val;
	}

	/**
	 * Returns the byte[] at position {@code pos} in the array.
	 * <p>
	 * JSON itself has no notion of a binary, so this method assumes there is a String value and
	 * it contains a Base64 encoded binary, which it decodes if found and returns.
	 */
	public byte[] getBinary(int pos) {
		String val = (String) list.get(pos);
		if (val == null) {
			return null;
		}
		return Base64.getDecoder().decode(val);
	}

	/**
	 * Returns the object value at position {@code pos} in the array.
	 */
	public Object getValue(int pos) {
		Object val = list.get(pos);

		if (val instanceof Map) {
			val = new JsonObject((Map) val);
		}
		else if (val instanceof List) {
			val = new JsonArray((List) val);
		}
		return val;
	}

	/**
	 * Returns {@code true} if there is a {@code null} value at given index.
	 */
	public boolean hasNull(int pos) {
		return list.get(pos) == null;
	}

	// ---------------------------------------------------------------- add

	/**
	 * Adds an enum to the JSON array.
	 * <p>
	 * JSON has no concept of encoding Enums, so the Enum will be converted to a String using the {@link java.lang.Enum#name}
	 * method and the value added as a String.
	 */
	public JsonArray add(Enum value) {
		if (value == null) {
			list.add(null);
		} else {
			list.add(value.name());
		}
		return this;
	}

	/**
	 * Adds a {@code CharSequence} to the JSON array.
	 */
	public JsonArray add(CharSequence value) {
		list.add(value.toString());
		return this;
	}

	/**
	 * Adds a string to the JSON array.
	 */
	public JsonArray add(String value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds an integer to the JSON array.
	 */
	public JsonArray add(Integer value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds a long to the JSON array.
	 */
	public JsonArray add(Long value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds a double to the JSON array.
	 */
	public JsonArray add(Double value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds a float to the JSON array.
	 */
	public JsonArray add(Float value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds a boolean to the JSON array.
	 */
	public JsonArray add(Boolean value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds a {@code null} value to the JSON array.
	 */
	public JsonArray addNull() {
		list.add(null);
		return this;
	}

	/**
	 * Adds a JSON object to the JSON array.
	 */
	public JsonArray add(JsonObject value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds another JSON array to the JSON array.
	 */
	public JsonArray add(JsonArray value) {
		list.add(value);
		return this;
	}

	/**
	 * Adds a binary value to the JSON array.
	 * <p>
	 * JSON has no notion of binary so the binary will be base64 encoded to a String, and the String added.
	 */
	public JsonArray add(byte[] value) {
		list.add(Base64.getEncoder().encodeToString(value));
		return this;
	}

	/**
	 * Adds an object to the JSON array.
	 */
	public JsonArray add(Object value) {
		Objects.requireNonNull(value);

		value = JsonObject.resolveValue(value);

		list.add(value);
		return this;
	}

	/**
	 * Appends all of the elements in the specified array to the end of this JSON array.
	 */
	public JsonArray addAll(JsonArray array) {
		Objects.requireNonNull(array);
		list.addAll(array.list);
		return this;
	}

	// ---------------------------------------------------------------- misc

	/**
	 * Returns {@code true} if given value exist.
	 */
	public boolean contains(Object value) {
		return list.contains(value);
	}

	/**
	 * Removes the specified value from the JSON array.
	 */
	public boolean remove(Object value) {
		return list.remove(value);
	}

	/**
	 * Removes the value at the specified position in the JSON array.
	 */
	public Object remove(int pos) {
		Object removed = list.remove(pos);
		if (removed instanceof Map) {
			return new JsonObject((Map) removed);
		}
		if (removed instanceof ArrayList) {
			return new JsonArray((List) removed);
		}
		return removed;
	}

	/**
	 * Returns the number of values in this JSON array.
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Returns {@code true} if JSON array is empty.
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Returns the underlying list.
	 */
	public List<Object> list() {
		return list;
	}

	/**
	 * Removes all entries from the JSON array.
	 */
	public JsonArray clear() {
		list.clear();
		return this;
	}

	/**
	 * Returns an iterator over the values in the JSON array.
	 */
	@Override
	public Iterator<Object> iterator() {
		return new Iter(list.iterator());
	}

	/**
	 * Returns a Stream over the entries in the JSON array
	 */
	public Stream<Object> stream() {
		return list.stream();
	}

	@Override
	public String toString() {
		return JsonSerializer.create().deep(true).serialize(this);
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
		return arrayEquals(list, o);
	}

	static boolean arrayEquals(List<?> l1, Object o2) {
		List<?> l2;
		if (o2 instanceof JsonArray) {
			l2 = ((JsonArray) o2).list;
		} else if (o2 instanceof List<?>) {
			l2 = (List<?>) o2;
		} else {
			return false;
		}
		if (l1.size() != l2.size()) {
			return false;
		}
		Iterator<?> iter = l2.iterator();
		for (Object entry : l1) {
			Object other = iter.next();
			if (entry == null) {
				if (other != null) {
					return false;
				}
			} else if (!JsonObject.elementEquals(entry, other)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	private class Iter implements Iterator<Object> {

		final Iterator<Object> listIter;

		Iter(Iterator<Object> listIter) {
			this.listIter = listIter;
		}

		@Override
		public boolean hasNext() {
			return listIter.hasNext();
		}

		@Override
		public Object next() {
			Object val = listIter.next();
			if (val instanceof Map) {
				val = new JsonObject((Map) val);
			} else if (val instanceof List) {
				val = new JsonArray((List) val);
			}
			return val;
		}

		@Override
		public void remove() {
			listIter.remove();
		}
	}
}