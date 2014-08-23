// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.JoddJson;
import jodd.json.meta.JSON;
import jodd.json.meta.JsonAnnotationManager;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static jodd.util.ArraysUtil.bytes;
import static jodd.util.ArraysUtil.ints;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonSerializerTest {

	public static class Foo {

		protected String name;
		protected Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Bar {
		private Foo foo;
		private int number;

		public Foo getFoo() {
			return foo;
		}

		public void setFoo(Foo foo) {
			this.foo = foo;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}
	}

	// ---------------------------------------------------------------- tests

	@Test
	public void testSimpleMap() {
		Map map = new LinkedHashMap();

		map.put("one", "uno");
		map.put("two", "duo");

		JsonSerializer jsonSerializer = new JsonSerializer();
		String json = jsonSerializer.serialize(map);

		assertEquals("{\"one\":\"uno\",\"two\":\"duo\"}", json);

		map = new LinkedHashMap();
		map.put("one", Long.valueOf(173));
		map.put("two", Double.valueOf(7.89));
		map.put("three", Boolean.TRUE);
		map.put("four", null);
		map.put("five", "new\nline");

		jsonSerializer = new JsonSerializer();
		json = jsonSerializer.serialize(map);

		assertEquals("{\"one\":173,\"two\":7.89,\"three\":true,\"four\":null,\"five\":\"new\\nline\"}", json);
	}

	@Test
	public void testSimpleObjects() {
		Foo foo = new Foo();
		foo.setName("jodd");
		foo.setId(Long.valueOf(976));

		Bar bar = new Bar();
		bar.setFoo(foo);
		bar.setNumber(575);

		JsonSerializer jsonSerializer = new JsonSerializer();
		String json = jsonSerializer.serialize(bar);

		assertEquals("{\"foo\":{\"id\":976,\"name\":\"jodd\"},\"number\":575}", json);
	}

	@Test
	public void testSimpleList() {
		List list = new LinkedList();

		list.add("one");
		list.add(new Bar());
		list.add(Double.valueOf(31E302));

		JsonSerializer jsonSerializer = new JsonSerializer();
		String json = jsonSerializer.serialize(list);

		assertEquals("[\"one\",{\"foo\":null,\"number\":0},3.1E303]", json);
	}

	@Test
	public void testSimpleArray() {
		int[] numbers = ints(1, 2, 3, 4, 5);

		JsonSerializer jsonSerializer = new JsonSerializer();
		String json = jsonSerializer.serialize(numbers);

		assertEquals("[1,2,3,4,5]", json);


		byte[] numbers2 = bytes((byte)1, (byte)2, (byte)3, (byte)4, (byte)5);

		json = jsonSerializer.serialize(numbers2);

		assertEquals("[1,2,3,4,5]", json);


		int[][] matrix = new int[][] {
				ints(1,2,3),
				ints(7,8,9)
		};

		json = jsonSerializer.serialize(matrix);

		assertEquals("[[1,2,3],[7,8,9]]", json);
	}

	@Test
	public void testEscapeChars() {
		String json = "\"1\\\" 2\\\\ 3\\/ 4\\b 5\\f 6\\n 7\\r 8\\t\"";

		String str = new JsonParser().parse(json);

		assertEquals("1\" 2\\ 3/ 4\b 5\f 6\n 7\r 8\t", str);

		String jsonStr = new JsonSerializer().serialize(str);

		assertEquals(json, jsonStr);
	}

	@Test
	public void testStrings() {
		String text = "Hello";

		String json = new JsonSerializer().serialize(new StringBuilder(text));
		assertEquals("\"Hello\"", json);

		json = new JsonSerializer().serialize(new StringBuffer(text));
		assertEquals("\"Hello\"", json);
	}

	@Test
	public void testChar() {
		Character character = Character.valueOf('J');

		String json = new JsonSerializer().serialize(character);

		assertEquals("\"J\"", json);
	}

	@Test
	public void testClass() {
		String json = new JsonSerializer().serialize(JoddJson.class);

		assertEquals("\"" + JoddJson.class.getName() + "\"", json);
	}

	@JSON(strict = false)
	public static class Cook {
		// no annotation
		private String aaa = "AAA";
		private String bbb = "BBB";
		private String ccc = "CCC";

		public String getAaa() {
			return aaa;
		}

		public void setAaa(String aaa) {
			this.aaa = aaa;
		}

		@JSON(include = false)
		public String getBbb() {
			return bbb;
		}

		public void setBbb(String bbb) {
			this.bbb = bbb;
		}

		@JSON(include = true)
		public String getCcc() {
			return ccc;
		}

		public void setCcc(String ccc) {
			this.ccc = ccc;
		}
	}

	@JSON(strict = true)
	public static class MasterCook extends Cook {
	}

	@Test
	public void testStrictMode() {
		Cook cook = new Cook();
		JsonAnnotationManager jam = JoddJson.annotationManager;

		JsonAnnotationManager.TypeData typeData = jam.lookupTypeData(Cook.class);

		assertEquals(1, typeData.rules.totalIncludeRules());
		assertEquals(1, typeData.rules.totalExcludeRules());

		assertEquals("ccc", typeData.rules.getRule(0));
		assertEquals("bbb", typeData.rules.getRule(1));

		JsonSerializer jsonSerializer = new JsonSerializer();

		String json = jsonSerializer.serialize(cook);

		assertTrue(json.contains("\"aaa\""));
		assertFalse(json.contains("\"bbb\""));
		assertTrue(json.contains("\"ccc\""));

		// now, strict = true, serialize only annotated properties!

		MasterCook masterCook = new MasterCook();

		typeData = jam.lookupTypeData(MasterCook.class);

		assertEquals(1, typeData.rules.totalIncludeRules());
		assertEquals(1, typeData.rules.totalExcludeRules());

		assertEquals("ccc", typeData.rules.getRule(0));
		assertEquals("bbb", typeData.rules.getRule(1));

		json = jsonSerializer.serialize(masterCook);

		assertFalse(json.contains("\"aaa\""));
		assertFalse(json.contains("\"bbb\""));
		assertTrue(json.contains("\"ccc\""));
	}

}