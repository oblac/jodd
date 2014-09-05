// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.json.mock.Surfer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonValueContextTest {

	@Test
	public void testJsonValueContextBean() {
		Surfer surfer = new Surfer();

		surfer.setName("Igor");
		surfer.setSplit("long wave");

		JsonSerializer jsonSerializer = new JsonSerializer()
				.use(String.class, new MyTypeJsonSerializer());

		String json = jsonSerializer.serialize(surfer);

		Map<String, Object> map = new JsonParser().parse(json);

		assertEquals("IGOR", map.get("name"));

		assertEquals("{\"id\":0,\"name\":\"IGOR\",\"pipe\":null,\"skill\":null,\"split\":\"long wave\"}", json);
	}

	@Test
	public void testJsonValueContextList() {
		List<String> list = new ArrayList<String>();

		list.add("one");
		list.add("two");
		list.add("three");

		JsonSerializer jsonSerializer = new JsonSerializer()
				.use(String.class, new MyTypeJsonSerializer2());

		String json = jsonSerializer.serialize(list);

		list = new JsonParser().parse(json);

		assertEquals(3, list.size());

		assertEquals("TWO", list.get(1));

		assertEquals("[\"one\",\"TWO\",\"three\"]", json);
	}

	@Test
	public void testJsonValueContextArray() {
		String[] array = new String[] {"one", "two", "three"};

		JsonSerializer jsonSerializer = new JsonSerializer()
				.use(String.class, new MyTypeJsonSerializer2());

		String json = jsonSerializer.serialize(array);

		List<String> list = new JsonParser().parse(json);

		assertEquals(3, list.size());

		assertEquals("TWO", list.get(1));

		assertEquals("[\"one\",\"TWO\",\"three\"]", json);
	}

	@Test
	public void testJsonValueContextArray2() {
		Object[] array = new Object[] {new Surfer(), "two", "three"};

		JsonSerializer jsonSerializer = new JsonSerializer()
				.use(String.class, new MyTypeJsonSerializer2());

		String json = jsonSerializer.serialize(array);

		List<String> list = new JsonParser().parse(json);

		assertEquals(3, list.size());

		assertEquals("TWO", list.get(1));
	}

	// ---------------------------------------------------------------- mys

	public static class MyTypeJsonSerializer implements TypeJsonSerializer<String> {

		public void serialize(JsonContext jsonContext, String value) {
			JsonValueContext jsonValueContext = jsonContext.peekValueContext();

			String propertyName = jsonValueContext.getPropertyName();

			if (propertyName != null && propertyName.equals("name")) {
				value = value.toUpperCase();
			}

			jsonContext.writeString(value);
		}
	}

	public static class MyTypeJsonSerializer2 implements TypeJsonSerializer<String> {

		public void serialize(JsonContext jsonContext, String value) {
			JsonValueContext jsonValueContext = jsonContext.peekValueContext();

			if (jsonValueContext.getIndex() == 1) {
				value = value.toUpperCase();
			}

			jsonContext.writeString(value);
		}
	}

}