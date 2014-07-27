// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.io.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonParserTest {

	protected String dataRoot;

	@Before
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = JsonParserTest.class.getResource(".");
		dataRoot = data.getFile();
	}

	@Test
	public void testSimpleJson() {
		JsonParser jsonParser = new JsonParser();

		Object value = jsonParser.parse("true");
		assertNotNull(value);
		assertEquals(Boolean.TRUE, value);

		value = jsonParser.parse("  true  ");
		assertEquals(Boolean.TRUE, value);

		value = jsonParser.parse("\t\tfalse\n");
		assertEquals(Boolean.FALSE, value);

		value = jsonParser.parse("\t\"foo\"\n");
		assertEquals("foo", value);

		value = jsonParser.parse("1");
		assertTrue(value instanceof Integer);
		assertEquals(Integer.valueOf(1), value);

		value = jsonParser.parse("-1234");
		assertTrue(value instanceof Integer);
		assertEquals(Integer.valueOf(-1234), value);

		value = jsonParser.parse("" + Integer.MAX_VALUE);
		assertTrue(value instanceof Integer);
		assertEquals(Integer.valueOf(Integer.MAX_VALUE), value);

		value = jsonParser.parse("" + Integer.MIN_VALUE);
		assertTrue(value instanceof Integer);
		assertEquals(Integer.valueOf(Integer.MIN_VALUE), value);

		long l = Integer.MAX_VALUE + 1l;
		value = jsonParser.parse("" + l);
		assertTrue(value instanceof Long);
		assertEquals(Long.valueOf(l), value);

		l = Integer.MIN_VALUE - 1l;
		value = jsonParser.parse("" + l);
		assertTrue(value instanceof Long);
		assertEquals(Long.valueOf(l), value);

		value = jsonParser.parse("1.2");
		assertTrue(value instanceof Double);
		assertEquals(Double.valueOf(1.2), value);

		value = jsonParser.parse("-12.34");
		assertTrue(value instanceof Double);
		assertEquals(Double.valueOf(-12.34), value);

		value = jsonParser.parse("1e3");
		assertTrue(value instanceof Integer);
		assertEquals(Integer.valueOf(1000), value);
	}

	@Test
	public void testSimpleConversions() {
		JsonParser jsonParser = new JsonParser();

		assertEquals(173, jsonParser.parse("\"173\"", Integer.class).intValue());
		assertEquals("123", jsonParser.parse("123", String.class));
		assertEquals(1, jsonParser.parse("true", Integer.class).intValue());
		assertEquals(0, jsonParser.parse("false", Integer.class).intValue());
	}

	@Test
	public void testStringEscapes() {
		JsonParser jsonParser = new JsonParser();

		assertEquals("\n4", jsonParser.parse("\"\\n\\u0034\""));

		try {
			jsonParser.parse("\"\\u034\"");
			fail();
		} catch (Exception ex) {
		}
	}

	@Test
	public void testSimpleMap() {
		JsonParser jsonParser = new JsonParser();

		Object value = jsonParser.parse("{ \"one\" : true}");

		assertNotNull(value);
		assertTrue(value instanceof Map);
		Map map = (Map) value;
		assertEquals(1, map.size());
		assertEquals(Boolean.TRUE, map.get("one"));

		value = jsonParser.parse("{ \"one\" : { \"two\" : false }, \"three\" : true}");

		assertNotNull(value);
		assertTrue(value instanceof Map);
		map = (Map) value;
		assertEquals(2, map.size());
		assertEquals(Boolean.TRUE, map.get("three"));
		map = (Map) map.get("one");
		assertEquals(1, map.size());
		assertEquals(Boolean.FALSE, map.get("two"));
	}

	@Test
	public void testSimpleArray() {
		JsonParser jsonParser = new JsonParser();

		Object value = jsonParser.parse("[true, false, true, [\"A\", \"BB\"]]");

		assertNotNull(value);

		List list = (List) value;

		assertEquals(4, list.size());
		assertEquals(Boolean.TRUE, list.get(0));
		assertEquals(Boolean.FALSE, list.get(1));
		assertEquals(Boolean.TRUE, list.get(2));

		list = (List) list.get(3);
		assertEquals(2, list.size());
		assertEquals("A", list.get(0));
		assertEquals("BB", list.get(1));
	}

	// ---------------------------------------------------------------- object tree

	public static class Bar {
		Integer amount;

		public Integer getAmount() {
			return amount;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}
	}

	public static interface Inter {
		public char getSign();
	}

	public static class InterImpl implements Inter {

		protected char sign;

		public char getSign() {
			return sign;
		}

		public void setSign(char sign) {
			this.sign = sign;
		}
	}

	public static class Foo {
		String name;
		long id;
		Bar bar;
		Inter inter;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public Bar getBar() {
			return bar;
		}

		public void setBar(Bar bar) {
			this.bar = bar;
		}

		public Inter getInter() {
			return inter;
		}

		public void setInter(Inter inter) {
			this.inter = inter;
		}
	}

	@Test
	public void testSimpleObject() {
		JsonParser jsonParser = new JsonParser();

		Foo foo = jsonParser.use("inter", InterImpl.class)
				.parse(
						"{" +
							"\"name\": \"jodd\"," +
							"\"id\": \"173\"," +
							"\"bar\": {" +
								"\"amount\" : \"-23\"" +
							"}," +
							"\"inter\": {" +
								"\"sign\" : \"W\"" +
							"}" +
						"}", Foo.class);

		assertNotNull(foo);
		assertEquals("jodd", foo.name);
		assertEquals(173, foo.getId());

		assertNotNull(foo.bar);
		assertEquals(-23, foo.bar.getAmount().intValue());
	}

	// ---------------------------------------------------------------- complex

	public static class Aaa {
		private Bar bar;
		private List<Byte> numbers;
		private List<Bar> bars;
		private List<Inter> inters;

		public Bar getBar() {
			return bar;
		}

		public void setBar(Bar bar) {
			this.bar = bar;
		}

		public List<Byte> getNumbers() {
			return numbers;
		}

		public void setNumbers(List<Byte> numbers) {
			this.numbers = numbers;
		}

		public List<Bar> getBars() {
			return bars;
		}

		public void setBars(List<Bar> bars) {
			this.bars = bars;
		}

		public List<Inter> getInters() {
			return inters;
		}

		public void setInters(List<Inter> inters) {
			this.inters = inters;
		}
	}

	@Test
	public void testComplexObject() throws IOException {
		JsonParser jsonParser = new JsonParser();
		String json = FileUtil.readString(new File(dataRoot, "complex.json"));

		Aaa aaa = jsonParser
				.use("inters.values", InterImpl.class)
				.parse(json, Aaa.class);

		assertNotNull(aaa);

		assertEquals(84, aaa.getBar().getAmount().intValue());
		List<Byte> numbers = aaa.getNumbers();
		assertEquals(2, numbers.size());
		assertEquals(19, numbers.get(0).byteValue());
		assertEquals(21, numbers.get(1).byteValue());

		List<Bar> bars = aaa.getBars();
		assertEquals(2, bars.size());
		assertEquals(5, bars.get(0).getAmount().intValue());
		assertEquals(305, bars.get(1).getAmount().intValue());

		List<Inter> inters = aaa.getInters();
		assertEquals(3, inters.size());
		assertEquals('a', inters.get(0).getSign());
		assertEquals('b', inters.get(1).getSign());
		assertEquals('c', inters.get(2).getSign());
	}

}