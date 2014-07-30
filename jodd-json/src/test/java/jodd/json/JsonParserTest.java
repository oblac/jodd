// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.io.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonParserTest {

	protected String dataRoot;

	@Before
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = JsonParserTest.class.getResource("data");
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

		l = Long.MAX_VALUE;
		value = jsonParser.parse("" + l);
		assertTrue(value instanceof Long);
		assertEquals(Long.valueOf(l), value);

		l = Long.MIN_VALUE;
		value = jsonParser.parse("" + l);
		assertTrue(value instanceof Long);
		assertEquals(Long.valueOf(l), value);

		BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE);
		bi = bi.add(BigInteger.ONE);
		value = jsonParser.parse(bi.toString());
		assertTrue(value instanceof BigInteger);
		assertEquals(bi, value);
		bi = bi.subtract(BigInteger.ONE);
		value = jsonParser.parse(bi.toString());
		assertTrue(value instanceof Long);
		assertEquals(Long.MAX_VALUE, ((Long) value).longValue());

		bi = BigInteger.valueOf(Long.MIN_VALUE);
		bi = bi.subtract(BigInteger.ONE);
		value = jsonParser.parse(bi.toString());
		assertTrue(value instanceof BigInteger);
		assertEquals(bi, value);

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
		} catch (Exception ignore) {
		}
	}

	@Test
	public void testFeatures() {
		JsonParser jsonParser = new JsonParser();

		Map map = (Map) jsonParser.parse("{}");
		assertTrue(map.isEmpty());

		map = (Map) jsonParser.parse("{ \"v\":\"1\"}");
		assertEquals(1, map.size());
		assertEquals("1", map.get("v"));

		map = (Map) jsonParser.parse("{ \"v\":\"1\"\r\n}");
		assertEquals(1, map.size());
		assertEquals("1", map.get("v"));

		map = (Map) jsonParser.parse("{ \"v\":1}");
		assertEquals(1, map.size());
		assertEquals("1", map.get("v").toString());

		map = (Map) jsonParser.parse("{ \"v\":\"ab'c\"}");
		assertEquals(1, map.size());
		assertEquals("ab'c", map.get("v").toString());

		map = (Map) jsonParser.parse("{ \"PI\":3.141E-10}");
		assertEquals(1, map.size());
		assertEquals(3.141E-10, ((Double)map.get("PI")).doubleValue(), 0.001E-10);

		map = (Map) jsonParser.parse("{ \"PI\":3.141e-10}");
		assertEquals(1, map.size());
		assertEquals(3.141e-10, ((Double)map.get("PI")).doubleValue(), 0.001E-10);

		map = (Map) jsonParser.parse("{ \"v\":12345123456789}");
		assertEquals(1, map.size());
		assertEquals(12345123456789L, ((Long)map.get("v")).longValue());

		map = (Map) jsonParser.parse("{ \"v\":123456789123456789123456789}");
		assertEquals(1, map.size());
		assertEquals("123456789123456789123456789", map.get("v").toString());

		List list = (List) jsonParser.parse("[ 1,2,3,4]");
		assertEquals(4, list.size());
		assertEquals(1, ((Integer)list.get(0)).intValue());
		assertEquals(2, ((Integer)list.get(1)).intValue());
		assertEquals(3, ((Integer)list.get(2)).intValue());
		assertEquals(4, ((Integer)list.get(3)).intValue());

		list = (List) jsonParser.parse("[ \"1\",\"2\",\"3\",\"4\"]");
		assertEquals(4, list.size());
		assertEquals("1", list.get(0).toString());
		assertEquals("2", list.get(1).toString());
		assertEquals("3", list.get(2).toString());
		assertEquals("4", list.get(3).toString());

		list = (List) jsonParser.parse("[ { }, { },[]]");
		assertEquals(3, list.size());
		assertEquals(0, ((Map)list.get(0)).size());
		assertEquals(0, ((Map)list.get(1)).size());
		assertEquals(0, ((List)list.get(2)).size());

		map = (Map) jsonParser.parse("{ \"v\":\"\\u2000\\u20ff\"}");
		assertEquals(1, map.size());
		assertEquals("\u2000\u20ff", map.get("v"));

		map = (Map) jsonParser.parse("{ \"v\":\"\\u2000\\u20FF\"}");
		assertEquals(1, map.size());
		assertEquals("\u2000\u20FF", map.get("v"));

		map = (Map) jsonParser.parse("{ \"a\":\"hp://foo\"}");
		assertEquals(1, map.size());
		assertEquals("hp://foo", map.get("a"));

		map = (Map) jsonParser.parse("{ \"a\":null}");
		assertEquals(1, map.size());
		assertNull(map.get("a"));

		map = (Map) jsonParser.parse("{ \"a\" : true }");
		assertEquals(1, map.size());
		assertEquals(Boolean.TRUE, map.get("a"));

		map = (Map) jsonParser.parse("{ \"v\":1.7976931348623157E308}");
		assertEquals(1, map.size());
		assertEquals(1.7976931348623157E308, ((Double)map.get("v")).doubleValue(), 1e300);

	}

	@Test
	public void testSimpleMap() {
		JsonParser jsonParser = new JsonParser();

		Object value = jsonParser.parse("   { \"one\" : true}   ");

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

		Foo foo = jsonParser.map("inter", InterImpl.class)
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
		private int[] years;
		private Bar[] bongos;

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

		public int[] getYears() {
			return years;
		}

		public void setYears(int[] years) {
			this.years = years;
		}

		public Bar[] getBongos() {
			return bongos;
		}

		public void setBongos(Bar[] bongos) {
			this.bongos = bongos;
		}
	}

	@Test
	public void testComplexObject() throws IOException {
		JsonParser jsonParser = new JsonParser();
		String json = FileUtil.readString(new File(dataRoot, "complex.json"));

		Aaa aaa = jsonParser
				.map("inters.values", InterImpl.class)
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

		int[] years = aaa.getYears();
		assertEquals(3, years.length);
		assertEquals(1975, years[0]);
		assertEquals(2001, years[1]);
		assertEquals(2013, years[2]);

		Bar[] bongos = aaa.getBongos();
		assertEquals(3, bongos.length);
		assertEquals(15, bongos[0].getAmount().intValue());
		assertEquals(35, bongos[1].getAmount().intValue());
		assertEquals(95, bongos[2].getAmount().intValue());
	}

	// ---------------------------------------------------------------- complex maps

	public static class User {
		private String name;
		private Map<String, Bar> bars;
		private Map<String, Inter> inters;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, Bar> getBars() {
			return bars;
		}

		public void setBars(Map<String, Bar> bars) {
			this.bars = bars;
		}

		public Map<String, Inter> getInters() {
			return inters;
		}

		public void setInters(Map<String, Inter> inters) {
			this.inters = inters;
		}
	}

	@Test
	public void testComplexMaps() throws IOException {
		JsonParser jsonParser = new JsonParser();
		String json = FileUtil.readString(new File(dataRoot, "complexMaps.json"));

		User user = jsonParser
				.map("inters.values", InterImpl.class)
				.parse(json, User.class);

		assertNotNull(user);

		assertEquals("Mak", user.getName());

		Map<String, Bar> bars = user.getBars();
		assertEquals(2, bars.size());

		assertEquals(12300, bars.get("123").getAmount().intValue());
		assertEquals(45600, bars.get("456").getAmount().intValue());

		Map<String, Inter> inters = user.getInters();
		assertEquals(3, inters.size());
	}

	@Test
	public void testActionLabel() throws Exception {
		JsonParser jsonParser = new JsonParser();
		String json = FileUtil.readString(new File(dataRoot, "actionLabel.json"));
		Map<String, Object> map;
		try {
			map = (Map<String, Object>) jsonParser.parse(json);
		}
		catch (Exception ex) {
			fail(ex.toString());
			throw ex;
		}

		map = (Map<String, Object>) map.get("menu");
		assertEquals("SVG Viewer", map.get("header"));
		List<String> items = (List<String>) map.get("items");

		assertEquals(22, items.size());
		assertNull(items.get(2));
		assertNotNull(items.get(3));
	}

}