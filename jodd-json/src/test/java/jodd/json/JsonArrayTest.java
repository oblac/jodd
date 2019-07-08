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

import jodd.json.fixtures.JsonParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class JsonArrayTest {

	private JsonArray jsonArray;

	@BeforeEach
	void setUp() {
		jsonArray = new JsonArray();
	}

	@Test
	void testGetInteger() {
		jsonArray.add(123);
		assertEquals(Integer.valueOf(123), jsonArray.getInteger(0));
		try {
			jsonArray.getInteger(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getInteger(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		// Different number types
		jsonArray.add(123L);
		assertEquals(Integer.valueOf(123), jsonArray.getInteger(1));
		jsonArray.add(123f);
		assertEquals(Integer.valueOf(123), jsonArray.getInteger(2));
		jsonArray.add(123d);
		assertEquals(Integer.valueOf(123), jsonArray.getInteger(3));
		jsonArray.add("foo");
		try {
			jsonArray.getInteger(4);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getInteger(5));
	}

	@Test
	void testGetLong() {
		jsonArray.add(123L);
		assertEquals(Long.valueOf(123L), jsonArray.getLong(0));
		try {
			jsonArray.getLong(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getLong(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		// Different number types
		jsonArray.add(123);
		assertEquals(Long.valueOf(123L), jsonArray.getLong(1));
		jsonArray.add(123f);
		assertEquals(Long.valueOf(123L), jsonArray.getLong(2));
		jsonArray.add(123d);
		assertEquals(Long.valueOf(123L), jsonArray.getLong(3));
		jsonArray.add("foo");
		try {
			jsonArray.getLong(4);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getLong(5));
	}

	@Test
	void testGetFloat() {
		jsonArray.add(123f);
		assertEquals(Float.valueOf(123f), jsonArray.getFloat(0));
		try {
			jsonArray.getFloat(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getFloat(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		// Different number types
		jsonArray.add(123);
		assertEquals(Float.valueOf(123f), jsonArray.getFloat(1));
		jsonArray.add(123);
		assertEquals(Float.valueOf(123f), jsonArray.getFloat(2));
		jsonArray.add(123d);
		assertEquals(Float.valueOf(123f), jsonArray.getFloat(3));
		jsonArray.add("foo");
		try {
			jsonArray.getFloat(4);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getFloat(5));
	}

	@Test
	void testGetDouble() {
		jsonArray.add(123d);
		assertEquals(Double.valueOf(123d), jsonArray.getDouble(0));
		try {
			jsonArray.getDouble(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getDouble(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		// Different number types
		jsonArray.add(123);
		assertEquals(Double.valueOf(123d), jsonArray.getDouble(1));
		jsonArray.add(123);
		assertEquals(Double.valueOf(123d), jsonArray.getDouble(2));
		jsonArray.add(123d);
		assertEquals(Double.valueOf(123d), jsonArray.getDouble(3));
		jsonArray.add("foo");
		try {
			jsonArray.getDouble(4);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getDouble(5));
	}

	@Test
	void testGetString() {
		jsonArray.add("foo");
		assertEquals("foo", jsonArray.getString(0));
		try {
			jsonArray.getString(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getString(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		jsonArray.add(123);
		try {
			jsonArray.getString(1);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getString(2));
	}

	@Test
	void testGetBoolean() {
		jsonArray.add(true);
		assertEquals(true, jsonArray.getBoolean(0));
		jsonArray.add(false);
		assertEquals(false, jsonArray.getBoolean(1));
		try {
			jsonArray.getBoolean(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getBoolean(2);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		jsonArray.add(123);
		try {
			jsonArray.getBoolean(2);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getBoolean(3));
	}

	@Test
	void testGetBinary() {
		byte[] bytes = randomByteArray(10);
		jsonArray.add(bytes);
		assertArrayEquals(bytes, jsonArray.getBinary(0));
		assertArrayEquals(bytes, Base64.getDecoder().decode(jsonArray.getString(0)));
		try {
			jsonArray.getBinary(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getBinary(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		jsonArray.add(123);
		try {
			jsonArray.getBinary(1);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getBinary(2));
	}

	@Test
	void testGetJsonObject() {
		JsonObject obj = new JsonObject().put("foo", "bar");
		jsonArray.add(obj);
		assertEquals(obj, jsonArray.getJsonObject(0));
		try {
			jsonArray.getJsonObject(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getJsonObject(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		jsonArray.add(123);
		try {
			jsonArray.getJsonObject(1);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getJsonObject(2));
	}

	@Test
	void testGetJsonArray() {
		JsonArray arr = new JsonArray().add("foo");
		jsonArray.add(arr);
		assertEquals(arr, jsonArray.getJsonArray(0));
		try {
			jsonArray.getJsonArray(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getJsonArray(1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		jsonArray.add(123);
		try {
			jsonArray.getJsonArray(1);
			fail("error");
		}
		catch (ClassCastException e) {
			// OK
		}
		jsonArray.addNull();
		assertNull(jsonArray.getJsonArray(2));
	}

	@Test
	void testGetValue() {
		jsonArray.add(123);
		assertEquals(123, jsonArray.getValue(0));
		jsonArray.add(123L);
		assertEquals(123L, jsonArray.getValue(1));
		jsonArray.add(123f);
		assertEquals(123f, jsonArray.getValue(2));
		jsonArray.add(123d);
		assertEquals(123d, jsonArray.getValue(3));
		jsonArray.add(false);
		assertEquals(false, jsonArray.getValue(4));
		jsonArray.add(true);
		assertEquals(true, jsonArray.getValue(5));
		jsonArray.add("bar");
		assertEquals("bar", jsonArray.getValue(6));
		JsonObject obj = new JsonObject().put("blah", "wibble");
		jsonArray.add(obj);
		assertEquals(obj, jsonArray.getValue(7));
		JsonArray arr = new JsonArray().add("blah").add("wibble");
		jsonArray.add(arr);
		assertEquals(arr, jsonArray.getValue(8));
		byte[] bytes = randomByteArray(100);
		jsonArray.add(bytes);
		assertArrayEquals(bytes, Base64.getDecoder().decode((String) jsonArray.getValue(9)));
		jsonArray.addNull();
		assertNull(jsonArray.getValue(10));
		try {
			jsonArray.getValue(-1);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		try {
			jsonArray.getValue(11);
			fail("error");
		}
		catch (IndexOutOfBoundsException e) {
			// OK
		}
		// JsonObject with inner Map
		List<Object> list = new ArrayList<>();
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put("blah", "wibble");
		list.add(innerMap);
		jsonArray = new JsonArray(list);
		obj = (JsonObject) jsonArray.getValue(0);
		assertEquals("wibble", obj.getString("blah"));
		// JsonObject with inner List
		list = new ArrayList<>();
		List<Object> innerList = new ArrayList<>();
		innerList.add("blah");
		list.add(innerList);
		jsonArray = new JsonArray(list);
		arr = (JsonArray) jsonArray.getValue(0);
		assertEquals("blah", arr.getString(0));
	}

	@Test
	void testAddEnum() {
		assertSame(jsonArray, jsonArray.add(JsonObjectTest.SomeEnum.FOO));
		assertEquals(JsonObjectTest.SomeEnum.FOO.toString(), jsonArray.getString(0));
	}

	@Test
	void testAddString() {
		assertSame(jsonArray, jsonArray.add("foo"));
		assertEquals("foo", jsonArray.getString(0));
	}

	@Test
	void testAddCharSequence() {
		assertSame(jsonArray, jsonArray.add(new StringBuilder("bar")));
		assertEquals("bar", jsonArray.getString(0));
	}

	@Test
	void testAddInteger() {
		assertSame(jsonArray, jsonArray.add(123));
		assertEquals(Integer.valueOf(123), jsonArray.getInteger(0));
	}

	@Test
	void testAddLong() {
		assertSame(jsonArray, jsonArray.add(123L));
		assertEquals(Long.valueOf(123L), jsonArray.getLong(0));
	}

	@Test
	void testAddFloat() {
		assertSame(jsonArray, jsonArray.add(123f));
		assertEquals(Float.valueOf(123f), jsonArray.getFloat(0));
	}

	@Test
	void testAddDouble() {
		assertSame(jsonArray, jsonArray.add(123d));
		assertEquals(Double.valueOf(123d), jsonArray.getDouble(0));
	}

	@Test
	void testAddBoolean() {
		assertSame(jsonArray, jsonArray.add(true));
		assertEquals(true, jsonArray.getBoolean(0));
		jsonArray.add(false);
		assertEquals(false, jsonArray.getBoolean(1));
	}

	@Test
	void testAddJsonObject() {
		JsonObject obj = new JsonObject().put("foo", "bar");
		assertSame(jsonArray, jsonArray.add(obj));
		assertEquals(obj, jsonArray.getJsonObject(0));
	}

	@Test
	void testAddJsonArray() {
		JsonArray arr = new JsonArray().add("foo");
		assertSame(jsonArray, jsonArray.add(arr));
		assertEquals(arr, jsonArray.getJsonArray(0));
	}

	@Test
	void testAddBinary() {
		byte[] bytes = randomByteArray(10);
		assertSame(jsonArray, jsonArray.add(bytes));
		assertArrayEquals(bytes, jsonArray.getBinary(0));
	}

	@Test
	@SuppressWarnings("UnnecessaryBoxing")
	void testAddObject() {
		jsonArray.add((Object) "bar");
		jsonArray.add((Object) (Integer.valueOf(123)));
		jsonArray.add((Object) (Long.valueOf(123L)));
		jsonArray.add((Object) (Float.valueOf(1.23f)));
		jsonArray.add((Object) (Double.valueOf(1.23d)));
		jsonArray.add((Object) true);
		byte[] bytes = randomByteArray(10);
		jsonArray.add((Object) (bytes));
		JsonObject obj = new JsonObject().put("foo", "blah");
		JsonArray arr = new JsonArray().add("quux");
		jsonArray.add((Object) obj);
		jsonArray.add((Object) arr);
		assertEquals("bar", jsonArray.getString(0));
		assertEquals(Integer.valueOf(123), jsonArray.getInteger(1));
		assertEquals(Long.valueOf(123L), jsonArray.getLong(2));
		assertEquals(Float.valueOf(1.23f), jsonArray.getFloat(3));
		assertEquals(Double.valueOf(1.23d), jsonArray.getDouble(4));
		assertEquals(true, jsonArray.getBoolean(5));
		assertArrayEquals(bytes, jsonArray.getBinary(6));
		assertEquals(obj, jsonArray.getJsonObject(7));
		assertEquals(arr, jsonArray.getJsonArray(8));
		try {
			jsonArray.add(new SomeClass());
			fail("error");
		}
		catch (JsonException e) {
			// OK
		}

		jsonArray.add(new BigDecimal(123));

		try {
			jsonArray.add(new Date());
			fail("error");
		}
		catch (JsonException e) {
			// OK
		}

	}

	@Test
	void testAddAllJsonArray() {
		jsonArray.add("bar");
		JsonArray arr = new JsonArray().add("foo").add(48);
		assertSame(jsonArray, jsonArray.addAll(arr));
		assertEquals(arr.getString(0), jsonArray.getString(1));
		assertEquals(arr.getInteger(1), jsonArray.getInteger(2));
	}

	@Test
	void testAddNull() {
		assertSame(jsonArray, jsonArray.addNull());
		assertEquals(null, jsonArray.getString(0));
		assertTrue(jsonArray.hasNull(0));
	}

	@Test
	void testHasNull() {
		jsonArray.addNull();
		jsonArray.add("foo");
		assertEquals(null, jsonArray.getString(0));
		assertTrue(jsonArray.hasNull(0));
		assertFalse(jsonArray.hasNull(1));
	}

	@Test
	void testContains() {
		jsonArray.add("wibble");
		jsonArray.add(true);
		jsonArray.add(123);
		JsonObject obj = new JsonObject();
		JsonArray arr = new JsonArray();
		jsonArray.add(obj);
		jsonArray.add(arr);
		assertFalse(jsonArray.contains("eek"));
		assertFalse(jsonArray.contains(false));
		assertFalse(jsonArray.contains(321));
		assertFalse(jsonArray.contains(new JsonObject().put("blah", "flib")));
		assertFalse(jsonArray.contains(new JsonArray().add("oob")));
		assertTrue(jsonArray.contains("wibble"));
		assertTrue(jsonArray.contains(true));
		assertTrue(jsonArray.contains(123));
		assertTrue(jsonArray.contains(obj));
		assertTrue(jsonArray.contains(arr));
	}

	@Test
	void testRemoveByObject() {
		jsonArray.add("wibble");
		jsonArray.add(true);
		jsonArray.add(123);
		assertEquals(3, jsonArray.size());
		assertTrue(jsonArray.remove("wibble"));
		assertEquals(2, jsonArray.size());
		assertFalse(jsonArray.remove("notthere"));
		assertTrue(jsonArray.remove(true));
		assertTrue(jsonArray.remove(Integer.valueOf(123)));
		assertTrue(jsonArray.isEmpty());
	}

	@Test
	void testRemoveByPos() {
		jsonArray.add("wibble");
		jsonArray.add(true);
		jsonArray.add(123);
		assertEquals(3, jsonArray.size());
		assertEquals("wibble", jsonArray.remove(0));
		assertEquals(2, jsonArray.size());
		assertEquals(123, jsonArray.remove(1));
		assertEquals(1, jsonArray.size());
		assertEquals(true, jsonArray.remove(0));
		assertTrue(jsonArray.isEmpty());
	}

	@Test
	void testSize() {
		jsonArray.add("wibble");
		jsonArray.add(true);
		jsonArray.add(123);
		assertEquals(3, jsonArray.size());
	}

	@Test
	void testClear() {
		jsonArray.add("wibble");
		jsonArray.add(true);
		jsonArray.add(123);
		assertEquals(3, jsonArray.size());
		assertEquals(jsonArray, jsonArray.clear());
		assertEquals(0, jsonArray.size());
		assertTrue(jsonArray.isEmpty());
	}

	@Test
	void testIterator() {
		jsonArray.add("foo");
		jsonArray.add(123);
		JsonObject obj = new JsonObject().put("foo", "bar");
		jsonArray.add(obj);
		Iterator<Object> iter = jsonArray.iterator();
		assertTrue(iter.hasNext());
		Object entry = iter.next();
		assertEquals("foo", entry);
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals(123, entry);
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals(obj, entry);
		assertFalse(iter.hasNext());
		iter.remove();
		assertFalse(jsonArray.contains(obj));
		assertEquals(2, jsonArray.size());
	}

	@Test
	void testStream() {
		jsonArray.add("foo");
		jsonArray.add(123);
		JsonObject obj = new JsonObject().put("foo", "bar");
		jsonArray.add(obj);
		List<Object> list = jsonArray.stream().collect(Collectors.toList());
		Iterator<Object> iter = list.iterator();
		assertTrue(iter.hasNext());
		Object entry = iter.next();
		assertEquals("foo", entry);
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals(123, entry);
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals(obj, entry);
		assertFalse(iter.hasNext());
	}

	class SomeClass {
	}

	@Test
	void testEncode() throws Exception {
		jsonArray.add("foo");
		jsonArray.add(123);
		jsonArray.add(1234L);
		jsonArray.add(1.23f);
		jsonArray.add(2.34d);
		jsonArray.add(true);
		byte[] bytes = randomByteArray(10);
		jsonArray.add(bytes);
		jsonArray.addNull();
		jsonArray.add(new JsonObject().put("foo", "bar"));
		jsonArray.add(new JsonArray().add("foo").add(123));
		String strBytes = Base64.getEncoder().encodeToString(bytes);
		String expected = "[\"foo\",123,1234,1.23,2.34,true,\"" + strBytes + "\",null,{\"foo\":\"bar\"},[\"foo\",123]]";
		String json = JsonSerializer.create().serialize(jsonArray);
		assertEquals(expected, json);
	}

	@Test
	void testDecode() {
		JsonParsers.forEachParser(jsonParser -> {
			byte[] bytes = randomByteArray(10);
			String strBytes = Base64.getEncoder().encodeToString(bytes);
			String json = "[\"foo\",123,1234,1.23,2.34,true,\"" + strBytes + "\",null,{\"foo\":\"bar\"},[\"foo\",123]]";
			JsonArray arr = jsonParser.parseAsJsonArray(json);
			assertEquals("foo", arr.getString(0));
			assertEquals(Integer.valueOf(123), arr.getInteger(1));
			assertEquals(Long.valueOf(1234L), arr.getLong(2));
			assertEquals(Float.valueOf(1.23f), arr.getFloat(3));
			assertEquals(Double.valueOf(2.34d), arr.getDouble(4));
			assertEquals(true, arr.getBoolean(5));
			assertArrayEquals(bytes, arr.getBinary(6));
			assertTrue(arr.hasNull(7));
			JsonObject obj = arr.getJsonObject(8);
			assertEquals("bar", obj.getString("foo"));
			JsonArray arr2 = arr.getJsonArray(9);
			assertEquals("foo", arr2.getString(0));
			assertEquals(Integer.valueOf(123), arr2.getInteger(1));
		});
	}

	@Test
	void testToString() {
		jsonArray.add("foo").add(123);
		assertEquals(JsonSerializer.create().serialize(jsonArray), jsonArray.toString());
	}

	@Test
	void testGetList() {
		JsonObject obj = new JsonObject().put("quux", "wibble");
		jsonArray.add("foo").add(123).add(obj);
		List<Object> list = jsonArray.list();
		list.remove("foo");
		assertFalse(jsonArray.contains("foo"));
		list.add("floob");
		assertTrue(jsonArray.contains("floob"));
		assertSame(obj, list.get(1));
		obj.remove("quux");
	}

	@Test
	void testCreateFromList() {
		List<Object> list = new ArrayList<>();
		list.add("foo");
		list.add(123);
		JsonArray arr = new JsonArray(list);
		assertEquals("foo", arr.getString(0));
		assertEquals(Integer.valueOf(123), arr.getInteger(1));
		assertSame(list, arr.list());
	}

	@Test
	void testCreateFromListCharSequence() {
		List<Object> list = new ArrayList<>();
		list.add("foo");
		list.add(123);
		list.add(new StringBuilder("eek"));
		JsonArray arr = new JsonArray(list);
		assertEquals("foo", arr.getString(0));
		assertEquals(Integer.valueOf(123), arr.getInteger(1));
		assertEquals("eek", arr.getString(2));
		assertSame(list, arr.list());
	}

	@Test
	void testCreateFromListNestedJsonObject() {
		List<Object> list = new ArrayList<>();
		list.add("foo");
		list.add(123);
		JsonObject obj = new JsonObject().put("blah", "wibble");
		list.add(obj);
		JsonArray arr = new JsonArray(list);
		assertEquals("foo", arr.getString(0));
		assertEquals(Integer.valueOf(123), arr.getInteger(1));
		assertSame(list, arr.list());
		assertSame(obj, arr.getJsonObject(2));
	}

	@Test
	void testCreateFromListNestedMap() {
		List<Object> list = new ArrayList<>();
		list.add("foo");
		list.add(123);
		Map<String, Object> map = new HashMap<>();
		map.put("blah", "wibble");
		list.add(map);
		JsonArray arr = new JsonArray(list);
		assertEquals("foo", arr.getString(0));
		assertEquals(Integer.valueOf(123), arr.getInteger(1));
		assertSame(list, arr.list());
		JsonObject obj = arr.getJsonObject(2);
		assertSame(map, obj.map());
	}

	@Test
	void testCreateFromListNestedJsonArray() {
		List<Object> list = new ArrayList<>();
		list.add("foo");
		list.add(123);
		JsonArray arr2 = new JsonArray().add("blah").add("wibble");
		list.add(arr2);
		JsonArray arr = new JsonArray(list);
		assertEquals("foo", arr.getString(0));
		assertEquals(Integer.valueOf(123), arr.getInteger(1));
		assertSame(list, arr.list());
		assertSame(arr2, arr.getJsonArray(2));
	}

	@Test
	void testCreateFromListNestedList() {
		List<Object> list = new ArrayList<>();
		list.add("foo");
		list.add(123);
		List<Object> list2 = new ArrayList<>();
		list2.add("blah");
		list2.add("wibble");
		list.add(list2);
		JsonArray arr = new JsonArray(list);
		assertEquals("foo", arr.getString(0));
		assertEquals(Integer.valueOf(123), arr.getInteger(1));
		assertSame(list, arr.list());
		JsonArray arr2 = arr.getJsonArray(2);
		assertSame(list2, arr2.list());
	}

	@Test
	void testJsonArrayEquality() {
		JsonObject obj = new JsonObject(Collections.singletonMap("abc", Collections.singletonList(3)));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonList(3))));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonList(3L))));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonArray().add(3))));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonArray().add(3L))));
		assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonList(4))));
		assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonArray().add(4))));
		JsonArray array = new JsonArray(Collections.singletonList(Collections.singletonList(3)));
		assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonList(3))));
		assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonList(3L))));
		assertEquals(array, new JsonArray(Collections.singletonList(new JsonArray().add(3))));
		assertEquals(array, new JsonArray(Collections.singletonList(new JsonArray().add(3L))));
		assertNotEquals(array, new JsonArray(Collections.singletonList(Collections.singletonList(4))));
		assertNotEquals(array, new JsonArray(Collections.singletonList(new JsonArray().add(4))));
	}

	@Test
	void testRemoveMethodReturnedObject() {
		JsonArray obj = new JsonArray();
		obj.add("bar")
			.add(new JsonObject().put("name", "vert.x").put("count", 2))
			.add(new JsonArray().add(1.0).add(2.0));

		Object removed = obj.remove(0);
		assertTrue(removed instanceof String);

		removed = obj.remove(0);
		assertTrue(removed instanceof JsonObject);
		assertEquals(((JsonObject) removed).getString("name"), "vert.x");

		removed = obj.remove(0);
		assertTrue(removed instanceof JsonArray);
		assertEquals(((JsonArray) removed).getDouble(0), 1.0, 0.1);
	}

	private byte[] randomByteArray(int size) {
		byte[] bytes = new byte[size];
		new Random().nextBytes(bytes);
		return bytes;
	}

}
