package jodd.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonObjectTest {
	@Test
	public void testGetInteger() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.put("foo", 123);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getInteger("bar");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123L);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
		jsonObject.put("foo", 123d);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
		jsonObject.put("foo", 123f);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
		jsonObject.put("foo", Long.MAX_VALUE);
		assertEquals(Integer.valueOf(-1), jsonObject.getInteger("foo"));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getInteger("foo"));
		assertNull(jsonObject.getInteger("absent"));
	}

	@Test
	public void testGetIntegerDefault() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", null));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getInteger("bar", 123);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123L);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
		jsonObject.put("foo", 123d);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
		jsonObject.put("foo", 123f);
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo", 321));
		jsonObject.put("foo", Long.MAX_VALUE);
		assertEquals(Integer.valueOf(-1), jsonObject.getInteger("foo", 321));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getInteger("foo", 321));
		assertEquals(Integer.valueOf(321), jsonObject.getInteger("absent", 321));
		assertNull(jsonObject.getInteger("foo", null));
		assertNull(jsonObject.getInteger("absent", null));
	}

	@Test
	public void testGetLong() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123L);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getLong("bar");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
		jsonObject.put("foo", 123d);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
		jsonObject.put("foo", 123f);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
		jsonObject.put("foo", Long.MAX_VALUE);
		assertEquals(Long.valueOf(Long.MAX_VALUE), jsonObject.getLong("foo"));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getLong("foo"));
		assertNull(jsonObject.getLong("absent"));
	}

	@Test
	public void testGetLongDefault() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123L);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
		assertEquals(Long.valueOf(123), jsonObject.getLong("foo", null));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getLong("bar", 123L);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
		jsonObject.put("foo", 123d);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
		jsonObject.put("foo", 123f);
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo", 321L));
		jsonObject.put("foo", Long.MAX_VALUE);
		assertEquals(Long.valueOf(Long.MAX_VALUE), jsonObject.getLong("foo", 321L));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getLong("foo", 321L));
		assertEquals(Long.valueOf(321L), jsonObject.getLong("absent", 321L));
		assertNull(jsonObject.getLong("foo", null));
		assertNull(jsonObject.getLong("absent", null));
	}

	@Test
	public void testGetFloat() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123f);
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo"));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getFloat("bar");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123);
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo"));
		jsonObject.put("foo", 123d);
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo"));
		jsonObject.put("foo", 123f);
		assertEquals(Float.valueOf(123L), jsonObject.getFloat("foo"));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getFloat("foo"));
		assertNull(jsonObject.getFloat("absent"));
	}

	@Test
	public void testGetFloatDefault() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123f);
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo", 321f));
		assertEquals(Float.valueOf(123), jsonObject.getFloat("foo", null));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getFloat("bar", 123f);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123);
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo", 321f));
		jsonObject.put("foo", 123d);
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo", 321f));
		jsonObject.put("foo", 123L);
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo", 321f));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getFloat("foo", 321f));
		assertEquals(Float.valueOf(321f), jsonObject.getFloat("absent", 321f));
		assertNull(jsonObject.getFloat("foo", null));
		assertNull(jsonObject.getFloat("absent", null));
	}

	@Test
	public void testGetDouble() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123d);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo"));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getDouble("bar");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo"));
		jsonObject.put("foo", 123L);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo"));
		jsonObject.put("foo", 123f);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo"));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getDouble("foo"));
		assertNull(jsonObject.getDouble("absent"));
	}

	@Test
	public void testGetDoubleDefault() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123d);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo", 321d));
		assertEquals(Double.valueOf(123), jsonObject.getDouble("foo", null));
		jsonObject.put("bar", "hello");
		try {
			jsonObject.getDouble("bar", 123d);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}
		// Put as different Number types
		jsonObject.put("foo", 123);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo", 321d));
		jsonObject.put("foo", 123f);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo", 321d));
		jsonObject.put("foo", 123L);
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo", 321d));

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getDouble("foo", 321d));
		assertEquals(Double.valueOf(321d), jsonObject.getDouble("absent", 321d));
		assertNull(jsonObject.getDouble("foo", null));
		assertNull(jsonObject.getDouble("absent", null));
	}

	@Test
	public void testGetString() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		assertEquals("bar", jsonObject.getString("foo"));
		jsonObject.put("bar", 123);
		try {
			jsonObject.getString("bar");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getString("foo"));
		assertNull(jsonObject.getString("absent"));
	}

	@Test
	public void testGetStringDefault() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		assertEquals("bar", jsonObject.getString("foo", "wibble"));
		assertEquals("bar", jsonObject.getString("foo", null));
		jsonObject.put("bar", 123);
		try {
			jsonObject.getString("bar", "wibble");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getString("foo", "wibble"));
		assertEquals("wibble", jsonObject.getString("absent", "wibble"));
		assertNull(jsonObject.getString("foo", null));
		assertNull(jsonObject.getString("absent", null));
	}

	@Test
	public void testGetBoolean() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", true);
		assertEquals(true, jsonObject.getBoolean("foo"));
		jsonObject.put("foo", false);
		assertEquals(false, jsonObject.getBoolean("foo"));
		jsonObject.put("bar", 123);
		try {
			jsonObject.getBoolean("bar");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getBoolean("foo"));
		assertNull(jsonObject.getBoolean("absent"));
	}

	@Test
	public void testGetBooleanDefault() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", true);
		assertEquals(true, jsonObject.getBoolean("foo", false));
		assertEquals(true, jsonObject.getBoolean("foo", null));
		jsonObject.put("foo", false);
		assertEquals(false, jsonObject.getBoolean("foo", true));
		assertEquals(false, jsonObject.getBoolean("foo", null));
		jsonObject.put("bar", 123);
		try {
			jsonObject.getBoolean("bar", true);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		// Null and absent values
		jsonObject.putNull("foo");
		assertNull(jsonObject.getBoolean("foo", true));
		assertNull(jsonObject.getBoolean("foo", false));
		assertEquals(true, jsonObject.getBoolean("absent", true));
		assertEquals(false, jsonObject.getBoolean("absent", false));
	}


	@Test
	public void testGetBinary() {
		JsonObject jsonObject = new JsonObject();

		byte[] bytes = randomByteArray(100);
		jsonObject.put("foo", bytes);
		assertArrayEquals(bytes, jsonObject.getBinary("foo"));

		// Can also get as string:
		String val = jsonObject.getString("foo");
		assertNotNull(val);
		byte[] retrieved = Base64.getDecoder().decode(val);
		assertArrayEquals(bytes, retrieved);

		jsonObject.put("foo", 123);
		try {
			jsonObject.getBinary("foo");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		jsonObject.putNull("foo");
		assertNull(jsonObject.getBinary("foo"));
		assertNull(jsonObject.getBinary("absent"));
	}

	@Test
	public void testGetBinaryDefault() {
		JsonObject jsonObject = new JsonObject();

		byte[] bytes = randomByteArray(100);
		byte[] defBytes = randomByteArray(100);
		jsonObject.put("foo", bytes);
		assertArrayEquals(bytes, jsonObject.getBinary("foo", defBytes));
		assertArrayEquals(bytes, jsonObject.getBinary("foo", null));

		jsonObject.put("foo", 123);
		try {
			jsonObject.getBinary("foo", defBytes);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		jsonObject.putNull("foo");
		assertNull(jsonObject.getBinary("foo", defBytes));
		assertArrayEquals(defBytes, jsonObject.getBinary("absent", defBytes));
		assertNull(jsonObject.getBinary("foo", null));
		assertNull(jsonObject.getBinary("absent", null));
	}

	@Test
	public void testGetJsonObject() {
		JsonObject jsonObject = new JsonObject();

		JsonObject obj = new JsonObject().put("blah", "wibble");
		jsonObject.put("foo", obj);
		assertEquals(obj, jsonObject.getJsonObject("foo"));

		jsonObject.put("foo", "hello");
		try {
			jsonObject.getJsonObject("foo");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		jsonObject.putNull("foo");
		assertNull(jsonObject.getJsonObject("foo"));
		assertNull(jsonObject.getJsonObject("absent"));
	}

	@Test
	public void testGetJsonObjectDefault() {
		JsonObject jsonObject = new JsonObject();

		JsonObject obj = new JsonObject().put("blah", "wibble");
		JsonObject def = new JsonObject().put("eek", "quuz");
		jsonObject.put("foo", obj);
		assertEquals(obj, jsonObject.getJsonObject("foo", def));
		assertEquals(obj, jsonObject.getJsonObject("foo", null));

		jsonObject.put("foo", "hello");
		try {
			jsonObject.getJsonObject("foo", def);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		jsonObject.putNull("foo");
		assertNull(jsonObject.getJsonObject("foo", def));
		assertEquals(def, jsonObject.getJsonObject("absent", def));
		assertNull(jsonObject.getJsonObject("foo", null));
		assertNull(jsonObject.getJsonObject("absent", null));
	}

	@Test
	public void testGetJsonArray() {
		JsonObject jsonObject = new JsonObject();

		JsonArray arr = new JsonArray().add("blah").add("wibble");
		jsonObject.put("foo", arr);
		assertEquals(arr, jsonObject.getJsonArray("foo"));

		jsonObject.put("foo", "hello");
		try {
			jsonObject.getJsonArray("foo");
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		jsonObject.putNull("foo");
		assertNull(jsonObject.getJsonArray("foo"));
		assertNull(jsonObject.getJsonArray("absent"));
	}

	@Test
	public void testGetJsonArrayDefault() {
		JsonObject jsonObject = new JsonObject();

		JsonArray arr = new JsonArray().add("blah").add("wibble");
		JsonArray def = new JsonArray().add("quux").add("eek");
		jsonObject.put("foo", arr);
		assertEquals(arr, jsonObject.getJsonArray("foo", def));
		assertEquals(arr, jsonObject.getJsonArray("foo", null));

		jsonObject.put("foo", "hello");
		try {
			jsonObject.getJsonArray("foo", def);
			fail("error");
		}
		catch (ClassCastException e) {
			// Ok
		}

		jsonObject.putNull("foo");
		assertNull(jsonObject.getJsonArray("foo", def));
		assertEquals(def, jsonObject.getJsonArray("absent", def));
		assertNull(jsonObject.getJsonArray("foo", null));
		assertNull(jsonObject.getJsonArray("absent", null));
	}

	@Test
	public void testGetValue() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123);
		assertEquals((Integer) 123, jsonObject.getValue("foo"));
		jsonObject.put("foo", 123L);
		assertEquals((Long) 123L, jsonObject.getValue("foo"));
		jsonObject.put("foo", 123f);
		assertEquals(123f, jsonObject.getValue("foo"), 0.01f);
		jsonObject.put("foo", 123d);
		assertEquals(123d, jsonObject.getValue("foo"), 0.01d);
		jsonObject.put("foo", false);
		assertEquals(false, jsonObject.getValue("foo"));
		jsonObject.put("foo", true);
		assertEquals(true, jsonObject.getValue("foo"));
		jsonObject.put("foo", "bar");
		assertEquals("bar", jsonObject.getValue("foo"));
		JsonObject obj = new JsonObject().put("blah", "wibble");
		jsonObject.put("foo", obj);
		assertEquals(obj, jsonObject.getValue("foo"));
		JsonArray arr = new JsonArray().add("blah").add("wibble");
		jsonObject.put("foo", arr);
		assertEquals(arr, jsonObject.getValue("foo"));
		byte[] bytes = randomByteArray(100);
		jsonObject.put("foo", bytes);
		assertArrayEquals(bytes, Base64.getDecoder().decode((String) jsonObject.getValue("foo")));
		jsonObject.putNull("foo");
		assertNull(jsonObject.getValue("foo"));
		assertNull(jsonObject.getValue("absent"));
		// JsonObject with inner Map
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> innerMap = new HashMap<>();
		innerMap.put("blah", "wibble");
		map.put("foo", innerMap);
		jsonObject = new JsonObject(map);
		obj = jsonObject.getValue("foo");
		assertEquals("wibble", obj.getString("blah"));
		// JsonObject with inner List
		map = new HashMap<>();
		List<Object> innerList = new ArrayList<>();
		innerList.add("blah");
		map.put("foo", innerList);
		jsonObject = new JsonObject(map);
		arr = jsonObject.getValue("foo");
		assertEquals("blah", arr.getString(0));
	}

	@Test
	public void testGetValueDefault() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", 123);
		assertEquals(123, jsonObject.getValue("foo", "blah"));
		assertEquals((Integer)123, jsonObject.getValue("foo", null));
		jsonObject.put("foo", 123L);
		assertEquals(123L, jsonObject.getValue("foo", "blah"));
		assertEquals((Long) 123L, jsonObject.getValue("foo", null));
		jsonObject.put("foo", 123f);
		assertEquals(123f, jsonObject.getValue("foo", "blah"));
		assertEquals((Float)123f, jsonObject.getValue("foo", null));
		jsonObject.put("foo", 123d);
		assertEquals(123d, jsonObject.getValue("foo", "blah"));
		assertEquals((Double)123d, jsonObject.getValue("foo", null));
		jsonObject.put("foo", false);
		assertEquals(false, jsonObject.getValue("foo", "blah"));
		assertEquals(false, jsonObject.getValue("foo", null));
		jsonObject.put("foo", true);
		assertEquals(true, jsonObject.getValue("foo", "blah"));
		assertEquals(true, jsonObject.getValue("foo", null));
		jsonObject.put("foo", "bar");
		assertEquals("bar", jsonObject.getValue("foo", "blah"));
		assertEquals("bar", jsonObject.getValue("foo", null));
		JsonObject obj = new JsonObject().put("blah", "wibble");
		jsonObject.put("foo", obj);
		assertEquals(obj, jsonObject.getValue("foo", "blah"));
		assertEquals(obj, jsonObject.getValue("foo", null));
		JsonArray arr = new JsonArray().add("blah").add("wibble");
		jsonObject.put("foo", arr);
		assertEquals(arr, jsonObject.getValue("foo", "blah"));
		assertEquals(arr, jsonObject.getValue("foo", null));
		byte[] bytes = randomByteArray(100);
		jsonObject.put("foo", bytes);
		assertArrayEquals(bytes, Base64.getDecoder().decode(jsonObject.getValue("foo", "blah")));
		assertArrayEquals(bytes, Base64.getDecoder().decode((String) jsonObject.getValue("foo", null)));
		jsonObject.putNull("foo");
		assertNull(jsonObject.getValue("foo", "blah"));
		assertNull(jsonObject.getValue("foo", null));
		assertEquals("blah", jsonObject.getValue("absent", "blah"));
		assertNull(jsonObject.getValue("absent", null));
	}

	@Test
	public void testContainsKey() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		assertTrue(jsonObject.containsKey("foo"));
		jsonObject.putNull("foo");
		assertTrue(jsonObject.containsKey("foo"));
		assertFalse(jsonObject.containsKey("absent"));
	}

	@Test
	public void testFieldNames() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		jsonObject.put("eek", 123);
		jsonObject.put("flib", new JsonObject());
		Set<String> fieldNames = jsonObject.fieldNames();
		assertEquals(3, fieldNames.size());
		assertTrue(fieldNames.contains("foo"));
		assertTrue(fieldNames.contains("eek"));
		assertTrue(fieldNames.contains("flib"));
		jsonObject.remove("foo");
		assertEquals(2, fieldNames.size());
		assertFalse(fieldNames.contains("foo"));
	}

	@Test
	public void testSize() {
		JsonObject jsonObject = new JsonObject();

		assertEquals(0, jsonObject.size());
		jsonObject.put("foo", "bar");
		assertEquals(1, jsonObject.size());
		jsonObject.put("bar", 123);
		assertEquals(2, jsonObject.size());
		jsonObject.putNull("wibble");
		assertEquals(3, jsonObject.size());
		jsonObject.remove("wibble");
		assertEquals(2, jsonObject.size());
		jsonObject.clear();
		assertEquals(0, jsonObject.size());
	}

	enum SomeEnum {
		FOO, BAR
	}

	@Test
	public void testPutEnum() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", SomeEnum.FOO));
		assertEquals(SomeEnum.FOO.toString(), jsonObject.getString("foo"));
		assertTrue(jsonObject.containsKey("foo"));
		try {
			jsonObject.put(null, SomeEnum.FOO);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutString() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", "bar"));
		assertEquals("bar", jsonObject.getString("foo"));
		jsonObject.put("quux", "wibble");
		assertEquals("wibble", jsonObject.getString("quux"));
		assertEquals("bar", jsonObject.getString("foo"));
		jsonObject.put("foo", "blah");
		assertEquals("blah", jsonObject.getString("foo"));
		jsonObject.put("foo", (String) null);
		assertTrue(jsonObject.containsKey("foo"));
		try {
			jsonObject.put(null, "blah");
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutCharSequence() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", new StringBuilder("bar")));
		assertEquals("bar", jsonObject.getString("foo"));
		assertEquals("bar", jsonObject.getString("foo", "def"));
		jsonObject.put("quux", new StringBuilder("wibble"));
		assertEquals("wibble", jsonObject.getString("quux"));
		assertEquals("bar", jsonObject.getString("foo"));
		jsonObject.put("foo", new StringBuilder("blah"));
		assertEquals("blah", jsonObject.getString("foo"));
		jsonObject.put("foo", (CharSequence) null);
		assertTrue(jsonObject.containsKey("foo"));
		try {
			jsonObject.put(null, (CharSequence) "blah");
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutInteger() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", 123));
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
		jsonObject.put("quux", 321);
		assertEquals(Integer.valueOf(321), jsonObject.getInteger("quux"));
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("foo"));
		jsonObject.put("foo", 456);
		assertEquals(Integer.valueOf(456), jsonObject.getInteger("foo"));
		jsonObject.put("foo", (Integer) null);
		assertTrue(jsonObject.containsKey("foo"));
		try {
			jsonObject.put(null, 123);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutLong() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", 123L));
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
		jsonObject.put("quux", 321L);
		assertEquals(Long.valueOf(321L), jsonObject.getLong("quux"));
		assertEquals(Long.valueOf(123L), jsonObject.getLong("foo"));
		jsonObject.put("foo", 456L);
		assertEquals(Long.valueOf(456L), jsonObject.getLong("foo"));
		jsonObject.put("foo", (Long) null);
		assertTrue(jsonObject.containsKey("foo"));

		try {
			jsonObject.put(null, 123L);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutFloat() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", 123f));
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo"));
		jsonObject.put("quux", 321f);
		assertEquals(Float.valueOf(321f), jsonObject.getFloat("quux"));
		assertEquals(Float.valueOf(123f), jsonObject.getFloat("foo"));
		jsonObject.put("foo", 456f);
		assertEquals(Float.valueOf(456f), jsonObject.getFloat("foo"));
		jsonObject.put("foo", (Float) null);
		assertTrue(jsonObject.containsKey("foo"));

		try {
			jsonObject.put(null, 1.2f);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutDouble() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", 123d));
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo"));
		jsonObject.put("quux", 321d);
		assertEquals(Double.valueOf(321d), jsonObject.getDouble("quux"));
		assertEquals(Double.valueOf(123d), jsonObject.getDouble("foo"));
		jsonObject.put("foo", 456d);
		assertEquals(Double.valueOf(456d), jsonObject.getDouble("foo"));
		jsonObject.put("foo", (Double) null);
		assertTrue(jsonObject.containsKey("foo"));
		try {
			jsonObject.put(null, 1.23d);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutBoolean() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.put("foo", true));
		assertEquals(true, jsonObject.getBoolean("foo"));
		jsonObject.put("quux", true);
		assertEquals(true, jsonObject.getBoolean("quux"));
		assertEquals(true, jsonObject.getBoolean("foo"));
		jsonObject.put("foo", true);
		assertEquals(true, jsonObject.getBoolean("foo"));
		jsonObject.put("foo", (Boolean) null);
		assertTrue(jsonObject.containsKey("foo"));
		try {
			jsonObject.put(null, false);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutJsonObject() {
		JsonObject jsonObject = new JsonObject();

		JsonObject obj1 = new JsonObject().put("blah", "wibble");
		JsonObject obj2 = new JsonObject().put("eeek", "flibb");
		JsonObject obj3 = new JsonObject().put("floob", "plarp");
		assertSame(jsonObject, jsonObject.put("foo", obj1));
		assertEquals(obj1, jsonObject.getJsonObject("foo"));
		jsonObject.put("quux", obj2);
		assertEquals(obj2, jsonObject.getJsonObject("quux"));
		assertEquals(obj1, jsonObject.getJsonObject("foo"));
		jsonObject.put("foo", obj3);
		assertEquals(obj3, jsonObject.getJsonObject("foo"));
		jsonObject.put("foo", (JsonObject) null);
		assertTrue(jsonObject.containsKey("foo"));
		try {
			jsonObject.put(null, new JsonObject());
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutJsonArray() {
		JsonObject jsonObject = new JsonObject();

		JsonArray obj1 = new JsonArray().add("parp");
		JsonArray obj2 = new JsonArray().add("fleep");
		JsonArray obj3 = new JsonArray().add("woob");

		assertSame(jsonObject, jsonObject.put("foo", obj1));
		assertEquals(obj1, jsonObject.getJsonArray("foo"));
		jsonObject.put("quux", obj2);
		assertEquals(obj2, jsonObject.getJsonArray("quux"));
		assertEquals(obj1, jsonObject.getJsonArray("foo"));
		jsonObject.put("foo", obj3);
		assertEquals(obj3, jsonObject.getJsonArray("foo"));

		jsonObject.put("foo", (JsonArray) null);
		assertTrue(jsonObject.containsKey("foo"));


		try {
			jsonObject.put(null, new JsonArray());
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutBinary() {
		JsonObject jsonObject = new JsonObject();

		byte[] bin1 = randomByteArray(100);
		byte[] bin2 = randomByteArray(100);
		byte[] bin3 = randomByteArray(100);

		assertSame(jsonObject, jsonObject.put("foo", bin1));
		assertArrayEquals(bin1, jsonObject.getBinary("foo"));
		jsonObject.put("quux", bin2);
		assertArrayEquals(bin2, jsonObject.getBinary("quux"));
		assertArrayEquals(bin1, jsonObject.getBinary("foo"));
		jsonObject.put("foo", bin3);
		assertArrayEquals(bin3, jsonObject.getBinary("foo"));

		jsonObject.put("foo", (byte[]) null);
		assertTrue(jsonObject.containsKey("foo"));

		try {
			jsonObject.put(null, bin1);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutNull() {
		JsonObject jsonObject = new JsonObject();

		assertSame(jsonObject, jsonObject.putNull("foo"));
		assertTrue(jsonObject.containsKey("foo"));
		assertSame(jsonObject, jsonObject.putNull("bar"));
		assertTrue(jsonObject.containsKey("bar"));
		try {
			jsonObject.putNull(null);
			fail("error");
		}
		catch (NullPointerException e) {
			// OK
		}
	}

	@Test
	public void testPutValue() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("str", (Object) "bar");
		jsonObject.put("int", (Object) (Integer.valueOf(123)));
		jsonObject.put("long", (Object) (Long.valueOf(123L)));
		jsonObject.put("float", (Object) (Float.valueOf(1.23f)));
		jsonObject.put("double", (Object) (Double.valueOf(1.23d)));
		jsonObject.put("boolean", (Object) true);
		byte[] bytes = randomByteArray(10);
		jsonObject.put("binary", (Object) bytes);
		JsonObject obj = new JsonObject().put("foo", "blah");
		JsonArray arr = new JsonArray().add("quux");
		jsonObject.put("obj", (Object) obj);
		jsonObject.put("arr", (Object) arr);
		assertEquals("bar", jsonObject.getString("str"));
		assertEquals(Integer.valueOf(123), jsonObject.getInteger("int"));
		assertEquals(Long.valueOf(123L), jsonObject.getLong("long"));
		assertEquals(Float.valueOf(1.23f), jsonObject.getFloat("float"));
		assertEquals(Double.valueOf(1.23d), jsonObject.getDouble("double"));
		assertArrayEquals(bytes, jsonObject.getBinary("binary"));
		assertEquals(obj, jsonObject.getJsonObject("obj"));
		assertEquals(arr, jsonObject.getJsonArray("arr"));
		try {
			jsonObject.put("inv", new SomeClass());
			fail("error");
		}
		catch (JsonException e) {
			// OK
		}
		jsonObject.put("inv", new BigDecimal(123));
		try {
			jsonObject.put("inv", new Date());
			fail("error");
		}
		catch (JsonException e) {
			// OK
		}

	}

	@Test
	public void testMergeIn1() {
		JsonObject jsonObject = new JsonObject();

		JsonObject obj1 = new JsonObject().put("foo", "bar");
		JsonObject obj2 = new JsonObject().put("eek", "flurb");
		obj1.mergeIn(obj2);
		assertEquals(2, obj1.size());
		assertEquals("bar", obj1.getString("foo"));
		assertEquals("flurb", obj1.getString("eek"));
		assertEquals(1, obj2.size());
		assertEquals("flurb", obj2.getString("eek"));
	}

	@Test
	public void testMergeIn2() {
		JsonObject obj1 = new JsonObject().put("foo", "bar");
		JsonObject obj2 = new JsonObject().put("foo", "flurb");
		obj1.mergeIn(obj2);
		assertEquals(1, obj1.size());
		assertEquals("flurb", obj1.getString("foo"));
		assertEquals(1, obj2.size());
		assertEquals("flurb", obj2.getString("foo"));
	}

	@Test
	public void testMergeInDepth0() {
		JsonObject obj1 = JsonParser.create().parseAsJsonObject("{ \"foo\": { \"bar\": \"flurb\" }}");
		JsonObject obj2 = JsonParser.create().parseAsJsonObject("{ \"foo\": { \"bar\": \"eek\" }}");
		obj1.mergeIn(obj2, 0);
		assertEquals(1, obj1.size());
		assertEquals(1, obj1.getJsonObject("foo").size());
		assertEquals("flurb", obj1.getJsonObject("foo").getString("bar"));
	}

	@Test
	public void testMergeInFlat() {
		JsonObject obj1 = JsonParser.create().parseAsJsonObject("{ \"foo\": { \"bar\": \"flurb\", \"eek\": 32 }}");
		JsonObject obj2 = JsonParser.create().parseAsJsonObject("{ \"foo\": { \"bar\": \"eek\" }}");
		obj1.mergeIn(obj2);
		assertEquals(1, obj1.size());
		assertEquals(1, obj1.getJsonObject("foo").size());
		assertEquals("eek", obj1.getJsonObject("foo").getString("bar"));
	}

	@Test
	public void testMergeInDepth1() {
		JsonObject obj1 = JsonParser.create().parseAsJsonObject("{ \"foo\": \"bar\", \"flurb\": { \"eek\": \"foo\", \"bar\": \"flurb\"}}");
		JsonObject obj2 = JsonParser.create().parseAsJsonObject("{ \"flurb\": { \"bar\": \"flurb1\" }}");
		obj1.mergeIn(obj2, 1);
		assertEquals(2, obj1.size());
		assertEquals(1, obj1.getJsonObject("flurb").size());
		assertEquals("flurb1", obj1.getJsonObject("flurb").getString("bar"));
	}

	@Test
	public void testMergeInDepth2() {
		JsonObject obj1 = new JsonObject(JsonParser.create().parse("{ \"foo\": \"bar\", \"flurb\": { \"eek\": \"foo\", \"bar\": \"flurb\"}}"));
		JsonObject obj2 = new JsonObject(JsonParser.create().parse("{ \"flurb\": { \"bar\": \"flurb1\" }}"));
		obj1.mergeIn(obj2, 2);
		assertEquals(2, obj1.size());
		assertEquals(2, obj1.getJsonObject("flurb").size());
		assertEquals("foo", obj1.getJsonObject("flurb").getString("eek"));
		assertEquals("flurb1", obj1.getJsonObject("flurb").getString("bar"));
	}

	@Test
	public void testEncode() throws Exception {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("mystr", "foo");
		jsonObject.put("mycharsequence", new StringBuilder("oob"));
		jsonObject.put("myint", 123);
		jsonObject.put("mylong", 1234L);
		jsonObject.put("myfloat", 1.23f);
		jsonObject.put("mydouble", 2.34d);
		jsonObject.put("myboolean", true);
		byte[] bytes = randomByteArray(10);
		jsonObject.put("mybinary", bytes);
		jsonObject.putNull("mynull");
		jsonObject.put("myobj", new JsonObject().put("foo", "bar"));
		jsonObject.put("myarr", new JsonArray().add("foo").add(123));

		String json = JsonSerializer.create().serialize(jsonObject);

		JsonObject expectedParsedJsonObject = JsonParser.create().parseAsJsonObject(json);

		// need to replace float with double, as decoding will do so
		jsonObject.put("myfloat", 1.23d);
		assertEquals(expectedParsedJsonObject, jsonObject);
	}

	@Test
	public void testDecode() throws Exception {
		byte[] bytes = randomByteArray(10);
		String strBytes = Base64.getEncoder().encodeToString(bytes);
		String json = "{\"mystr\":\"foo\",\"myint\":123,\"mylong\":1234,\"myfloat\":1.23,\"mydouble\":2.34,\"" +
			"myboolean\":true,\"mybinary\":\"" + strBytes + "\",\"mynull\":null,\"myobj\":{\"foo\":\"bar\"},\"myarr\":[\"foo\",123]}";
		JsonObject obj = new JsonObject(JsonParser.create().parse(json));
		assertEquals("foo", obj.getString("mystr"));
		assertEquals(Integer.valueOf(123), obj.getInteger("myint"));
		assertEquals(Long.valueOf(1234), obj.getLong("mylong"));
		assertEquals(Float.valueOf(1.23f), obj.getFloat("myfloat"));
		assertEquals(Double.valueOf(2.34d), obj.getDouble("mydouble"));
		assertTrue(obj.getBoolean("myboolean"));
		assertArrayEquals(bytes, obj.getBinary("mybinary"));
		assertTrue(obj.containsKey("mynull"));
		JsonObject nestedObj = obj.getJsonObject("myobj");
		assertEquals("bar", nestedObj.getString("foo"));
		JsonArray nestedArr = obj.getJsonArray("myarr");
		assertEquals("foo", nestedArr.getString(0));
		assertEquals(Integer.valueOf(123), Integer.valueOf(nestedArr.getInteger(1)));
	}

	@Test
	public void testToString() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		assertEquals(JsonSerializer.create().serialize(jsonObject), jsonObject.toString());
	}

	@Test
	public void testClear() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		jsonObject.put("quux", 123);
		assertEquals(2, jsonObject.size());
		jsonObject.clear();
		assertEquals(0, jsonObject.size());
		assertNull(jsonObject.getValue("foo"));
		assertNull(jsonObject.getValue("quux"));
	}

	@Test
	public void testIsEmpty() {
		JsonObject jsonObject = new JsonObject();

		assertTrue(jsonObject.isEmpty());
		jsonObject.put("foo", "bar");
		jsonObject.put("quux", 123);
		assertFalse(jsonObject.isEmpty());
		jsonObject.clear();
		assertTrue(jsonObject.isEmpty());
	}

	@Test
	public void testRemove() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("mystr", "bar");
		jsonObject.put("myint", 123);
		assertEquals("bar", jsonObject.remove("mystr"));
		assertNull(jsonObject.getValue("mystr"));
		assertEquals(123, jsonObject.remove("myint"));
		assertNull(jsonObject.getValue("myint"));
		assertTrue(jsonObject.isEmpty());
	}

	@Test
	public void testIterator() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		jsonObject.put("quux", 123);
		JsonObject obj = createJsonObject();
		jsonObject.put("wibble", obj);
		Iterator<Map.Entry<String, Object>> iter = jsonObject.iterator();
		assertTrue(iter.hasNext());
		Map.Entry<String, Object> entry = iter.next();
		assertEquals("foo", entry.getKey());
		assertEquals("bar", entry.getValue());
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("quux", entry.getKey());
		assertEquals(123, entry.getValue());
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("wibble", entry.getKey());
		assertEquals(obj, entry.getValue());
		assertFalse(iter.hasNext());
		iter.remove();
		assertFalse(obj.containsKey("wibble"));
		assertEquals(2, jsonObject.size());
	}

	@Test
	public void testIteratorDoesntChangeObject() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("nestedMap", new HashMap<>());
		map.put("nestedList", new ArrayList<>());
		JsonObject obj = new JsonObject(map);
		Iterator<Map.Entry<String, Object>> iter = obj.iterator();
		Map.Entry<String, Object> entry1 = iter.next();
		assertEquals("nestedMap", entry1.getKey());
		Object val1 = entry1.getValue();
		assertTrue(val1 instanceof JsonObject);
		Map.Entry<String, Object> entry2 = iter.next();
		assertEquals("nestedList", entry2.getKey());
		Object val2 = entry2.getValue();
		assertTrue(val2 instanceof JsonArray);
		assertTrue(map.get("nestedMap") instanceof HashMap);
		assertTrue(map.get("nestedList") instanceof ArrayList);
	}

	@Test
	public void testStream() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		jsonObject.put("quux", 123);
		JsonObject obj = createJsonObject();
		jsonObject.put("wibble", obj);
		List<Map.Entry<String, Object>> list = jsonObject.stream().collect(Collectors.toList());
		Iterator<Map.Entry<String, Object>> iter = list.iterator();
		assertTrue(iter.hasNext());
		Map.Entry<String, Object> entry = iter.next();
		assertEquals("foo", entry.getKey());
		assertEquals("bar", entry.getValue());
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("quux", entry.getKey());
		assertEquals(123, entry.getValue());
		assertTrue(iter.hasNext());
		entry = iter.next();
		assertEquals("wibble", entry.getKey());
		assertEquals(obj, entry.getValue());
		assertFalse(iter.hasNext());
	}

	class SomeClass {
	}

	@Test
	public void testGetMap() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("foo", "bar");
		jsonObject.put("quux", 123);
		JsonObject obj = createJsonObject();
		jsonObject.put("wibble", obj);
		Map<String, Object> map = jsonObject.map();
		map.remove("foo");
		assertFalse(jsonObject.containsKey("foo"));
		map.put("bleep", "flarp");
		assertTrue(jsonObject.containsKey("bleep"));
		jsonObject.remove("quux");
		assertFalse(map.containsKey("quux"));
		jsonObject.put("wooble", "plink");
		assertTrue(map.containsKey("wooble"));
		assertSame(obj, map.get("wibble"));
	}

	@Test
	public void testCreateFromMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("quux", 123);
		JsonObject obj = new JsonObject(map);
		assertEquals("bar", obj.getString("foo"));
		assertEquals(Integer.valueOf(123), obj.getInteger("quux"));
		assertSame(map, obj.map());
	}

	@Test
	public void testCreateFromMapCharSequence() {
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("quux", 123);
		map.put("eeek", new StringBuilder("blah"));
		JsonObject obj = new JsonObject(map);
		assertEquals("bar", obj.getString("foo"));
		assertEquals(Integer.valueOf(123), obj.getInteger("quux"));
		assertEquals("blah", obj.getString("eeek"));
		assertSame(map, obj.map());
	}

	@Test
	public void testCreateFromMapNestedJsonObject() {
		Map<String, Object> map = new HashMap<>();
		JsonObject nestedObj = new JsonObject().put("foo", "bar");
		map.put("nested", nestedObj);
		JsonObject obj = new JsonObject(map);
		JsonObject nestedRetrieved = obj.getJsonObject("nested");
		assertEquals("bar", nestedRetrieved.getString("foo"));
	}

	@Test
	public void testCreateFromMapNestedMap() {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> nestedMap = new HashMap<>();
		nestedMap.put("foo", "bar");
		map.put("nested", nestedMap);
		JsonObject obj = new JsonObject(map);
		JsonObject nestedRetrieved = obj.getJsonObject("nested");
		assertEquals("bar", nestedRetrieved.getString("foo"));
	}

	@Test
	public void testCreateFromMapNestedJsonArray() {
		Map<String, Object> map = new HashMap<>();
		JsonArray nestedArr = new JsonArray().add("foo");
		map.put("nested", nestedArr);
		JsonObject obj = new JsonObject(map);
		JsonArray nestedRetrieved = obj.getJsonArray("nested");
		assertEquals("foo", nestedRetrieved.getString(0));
	}

	@Test
	public void testCreateFromMapNestedList() {
		Map<String, Object> map = new HashMap<>();
		List<String> nestedArr = Arrays.asList("foo");
		map.put("nested", nestedArr);
		JsonObject obj = new JsonObject(map);
		JsonArray nestedRetrieved = obj.getJsonArray("nested");
		assertEquals("foo", nestedRetrieved.getString(0));
	}

	@Test
	public void testNumberEquality() {
		assertNumberEquals(4, 4);
		assertNumberEquals(4, (long) 4);
		assertNumberEquals(4, 4f);
		assertNumberEquals(4, 4D);
		assertNumberEquals((long) 4, (long) 4);
		assertNumberEquals((long) 4, 4f);
		assertNumberEquals((long) 4, 4D);
		assertNumberEquals(4f, 4f);
		assertNumberEquals(4f, 4D);
		assertNumberEquals(4D, 4D);
		assertNumberEquals(4.1D, 4.1D);
		assertNumberEquals(4.1f, 4.1f);
		assertNumberNotEquals(4.1f, 4.1D);
		assertNumberEquals(4.5D, 4.5D);
		assertNumberEquals(4.5f, 4.5f);
		assertNumberEquals(4.5f, 4.5D);
		assertNumberNotEquals(4, 5);
		assertNumberNotEquals(4, (long) 5);
		assertNumberNotEquals(4, 5D);
		assertNumberNotEquals(4, 5f);
		assertNumberNotEquals((long) 4, (long) 5);
		assertNumberNotEquals((long) 4, 5D);
		assertNumberNotEquals((long) 4, 5f);
		assertNumberNotEquals(4f, 5f);
		assertNumberNotEquals(4f, 5D);
		assertNumberNotEquals(4D, 5D);
	}

	private void assertNumberEquals(Number value1, Number value2) {
		JsonObject o1 = new JsonObject().put("key", value1);
		JsonObject o2 = new JsonObject().put("key", value2);
		if (!o1.equals(o2)) {
			fail("Was expecting " + value1.getClass().getSimpleName() + ":" + value1 + " == " +
				value2.getClass().getSimpleName() + ":" + value2);
		}
		JsonArray a1 = new JsonArray().add(value1);
		JsonArray a2 = new JsonArray().add(value2);
		if (!a1.equals(a2)) {
			fail("Was expecting " + value1.getClass().getSimpleName() + ":" + value1 + " == " +
				value2.getClass().getSimpleName() + ":" + value2);
		}
	}

	private void assertNumberNotEquals(Number value1, Number value2) {
		JsonObject o1 = new JsonObject().put("key", value1);
		JsonObject o2 = new JsonObject().put("key", value2);
		if (o1.equals(o2)) {
			fail("Was expecting " + value1.getClass().getSimpleName() + ":" + value1 + " != " +
				value2.getClass().getSimpleName() + ":" + value2);
		}
	}

	@Test
	public void testJsonObjectEquality() {
		JsonObject obj = new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 3)));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 3))));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 3L))));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonObject().put("def", 3))));
		assertEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonObject().put("def", 3L))));
		assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", Collections.singletonMap("def", 4))));
		assertNotEquals(obj, new JsonObject(Collections.singletonMap("abc", new JsonObject().put("def", 4))));
		JsonArray array = new JsonArray(Collections.singletonList(Collections.singletonMap("def", 3)));
		assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonMap("def", 3))));
		assertEquals(array, new JsonArray(Collections.singletonList(Collections.singletonMap("def", 3L))));
		assertEquals(array, new JsonArray(Collections.singletonList(new JsonObject().put("def", 3))));
		assertEquals(array, new JsonArray(Collections.singletonList(new JsonObject().put("def", 3L))));
		assertNotEquals(array, new JsonArray(Collections.singletonList(Collections.singletonMap("def", 4))));
		assertNotEquals(array, new JsonArray(Collections.singletonList(new JsonObject().put("def", 4))));
	}

	@Test
	public void testJsonObjectEquality2() {
		JsonObject obj1 = new JsonObject().put("arr", new JsonArray().add("x"));
		List<Object> list = new ArrayList<>();
		list.add("x");
		Map<String, Object> map = new HashMap<>();
		map.put("arr", list);
		JsonObject obj2 = new JsonObject(map);
		Iterator<Map.Entry<String, Object>> iter = obj2.iterator();
		// There was a bug where iteration of entries caused the underlying object to change resulting in a
		// subsequent equals changing
		while (iter.hasNext()) {
			Map.Entry<String, Object> entry = iter.next();
		}
		assertEquals(obj2, obj1);
	}

	@Test
	public void testRemoveMethodReturnedObject() {
		JsonObject obj = new JsonObject();
		obj.put("simple", "bar")
			.put("object", new JsonObject().put("name", "vert.x").put("count", 2))
			.put("array", new JsonArray().add(1.0).add(2.0));

		Object removed = obj.remove("missing");
		assertNull(removed);

		removed = obj.remove("simple");
		assertTrue(removed instanceof String);

		removed = obj.remove("object");
		assertTrue(removed instanceof JsonObject);
		assertEquals(((JsonObject) removed).getString("name"), "vert.x");

		removed = obj.remove("array");
		assertTrue(removed instanceof JsonArray);
		assertEquals(((JsonArray) removed).getDouble(0), 1.0, 0.1);
	}

	private void testStreamCorrectTypes(JsonObject object) {
		object.stream().forEach(entry -> {
			String key = entry.getKey();
			Object val = entry.getValue();
			assertEquals("object1", key);
			assertTrue(val instanceof JsonObject, "Expecting JsonObject, found: " + val.getClass().getCanonicalName());
		});
	}

	private JsonObject createJsonObject() {
		JsonObject obj = new JsonObject();
		obj.put("mystr", "bar");
		obj.put("myint", Integer.MAX_VALUE);
		obj.put("mylong", Long.MAX_VALUE);
		obj.put("myfloat", Float.MAX_VALUE);
		obj.put("mydouble", Double.MAX_VALUE);
		obj.put("myboolean", true);
		obj.put("mybinary", randomByteArray(100));
		return obj;
	}

	private byte[] randomByteArray(int size) {
		byte[] bytes = new byte[size];
		new Random().nextBytes(bytes);
		return bytes;
	}

}