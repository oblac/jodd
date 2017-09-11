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

import jodd.Jodd;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.json.fixtures.model.FooBar;
import jodd.json.fixtures.model.HitList;
import jodd.json.meta.JSON;
import jodd.util.RandomString;
import jodd.util.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static jodd.util.ArraysUtil.ints;
import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

	protected String dataRoot;

	@BeforeEach
	public void setUp() throws Exception {
		if (dataRoot != null) {
			return;
		}
		URL data = JsonParserTest.class.getResource("data");
		if (data != null) {
			dataRoot = data.getFile();
		}
	}

	@AfterEach
	public void tearDown() {
		JoddJson.classMetadataName = null;
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
	public void testConversionsToObject() {
		JsonParser jsonParser = new JsonParser();
		assertEquals("173", jsonParser.parse("\"173\"", Object.class));
		assertEquals(123, jsonParser.parse("123", Object.class));
		assertEquals(true, jsonParser.parse("true", Object.class));
		assertTrue(jsonParser.parse("[]", Object.class) instanceof List);
		assertTrue(jsonParser.parse("{}", Object.class) instanceof Map);
	}

	@Test
	public void testStringEscapes() {
		JsonParser jsonParser = new JsonParser();

		assertEquals("\n4", jsonParser.parse("\"\\n\\u0034\""));

		try {
			jsonParser.parse("\"\\u034\"");
			fail("error");
		} catch (Exception ignore) {
		}
	}

	@Test
	public void testFeatures() {
		JsonParser jsonParser = new JsonParser();

		Map map = jsonParser.parse("{}");
		assertTrue(map.isEmpty());

		map = jsonParser.parse("{ \"v\":\"1\"}");
		assertEquals(1, map.size());
		assertEquals("1", map.get("v"));

		map = jsonParser.parse("{ \"v\":\"1\"\r\n}");
		assertEquals(1, map.size());
		assertEquals("1", map.get("v"));

		map = jsonParser.parse("{ \"v\":1}");
		assertEquals(1, map.size());
		assertEquals("1", map.get("v").toString());

		map = jsonParser.parse("{ \"v\":\"ab'c\"}");
		assertEquals(1, map.size());
		assertEquals("ab'c", map.get("v").toString());

		map = jsonParser.parse("{ \"PI\":3.141E-10}");
		assertEquals(1, map.size());
		assertEquals(3.141E-10, ((Double)map.get("PI")).doubleValue(), 0.001E-10);

		map = jsonParser.parse("{ \"PI\":3.141e-10}");
		assertEquals(1, map.size());
		assertEquals(3.141e-10, ((Double)map.get("PI")).doubleValue(), 0.001E-10);

		map = jsonParser.parse("{ \"v\":12345123456789}");
		assertEquals(1, map.size());
		assertEquals(12345123456789L, ((Long)map.get("v")).longValue());

		map = jsonParser.parse("{ \"v\":123456789123456789123456789}");
		assertEquals(1, map.size());
		assertEquals("123456789123456789123456789", map.get("v").toString());

		List list = jsonParser.parse("[ 1,2,3,4]");
		assertEquals(4, list.size());
		assertEquals(1, ((Integer)list.get(0)).intValue());
		assertEquals(2, ((Integer)list.get(1)).intValue());
		assertEquals(3, ((Integer)list.get(2)).intValue());
		assertEquals(4, ((Integer)list.get(3)).intValue());

		list = jsonParser.parse("[ \"1\",\"2\",\"3\",\"4\"]");
		assertEquals(4, list.size());
		assertEquals("1", list.get(0).toString());
		assertEquals("2", list.get(1).toString());
		assertEquals("3", list.get(2).toString());
		assertEquals("4", list.get(3).toString());

		list = jsonParser.parse("[ { }, { },[]]");
		assertEquals(3, list.size());
		assertEquals(0, ((Map)list.get(0)).size());
		assertEquals(0, ((Map)list.get(1)).size());
		assertEquals(0, ((List)list.get(2)).size());

		map = jsonParser.parse("{ \"v\":\"\\u2000\\u20ff\"}");
		assertEquals(1, map.size());
		assertEquals("\u2000\u20ff", map.get("v"));

		map = jsonParser.parse("{ \"v\":\"\\u2000\\u20FF\"}");
		assertEquals(1, map.size());
		assertEquals("\u2000\u20FF", map.get("v"));

		map = jsonParser.parse("{ \"a\":\"hp://foo\"}");
		assertEquals(1, map.size());
		assertEquals("hp://foo", map.get("a"));

		map = jsonParser.parse("{ \"a\":null}");
		assertEquals(1, map.size());
		assertNull(map.get("a"));

		map = jsonParser.parse("{ \"a\" : true }");
		assertEquals(1, map.size());
		assertEquals(Boolean.TRUE, map.get("a"));

		map = jsonParser.parse("{ \"v\":1.7976931348623157E308}");
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

	static class ArrHolder {
		int[] pos;
	}

	@Test
	public void testSimpleMatrix() {
		JsonParser jsonParser = new JsonParser();

		ArrHolder arrHolder = jsonParser.parse("{\"pos\":[1,2,3,4,5,6,7,8]}", ArrHolder.class);

		assertArrayEquals(ints(1,2,3,4,5,6,7,8), arrHolder.pos);

		JsonParser jsonParser2 = new JsonParser();

		int[] ints = jsonParser2.parse("[1,2,3,4]", int[].class);

		assertEquals(4, ints.length);
		assertEquals(1, ints[0]);
		assertEquals(4, ints[3]);

		JsonParser jsonParser3 = new JsonParser();

		int[][] matrix  = jsonParser3.parse("[[1,2,3],[7,8,9]]", int[][].class);

		assertEquals(2, matrix.length);

		assertArrayEquals(ints(1,2,3), matrix[0]);
		assertArrayEquals(ints(7,8,9), matrix[1]);
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

		@Override
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

	public static class Wildcard {
		Object value;

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}

	@Test
	public void testSimpleObject() {
		JsonParser jsonParser = new JsonParser();

		Foo foo = jsonParser
				.map("inter", InterImpl.class)
				.parse(
						"{" +
							"\"aaa\": 123," +
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

	@Test
	public void testObjectAttribute() {
		JsonParser jsonParser = new JsonParser();
		jsonParser.map(Wildcard.class);

		Wildcard wildcard = jsonParser.parse("{\"value\":1}");
		assertNotNull(wildcard);
		assertEquals(1, wildcard.getValue());

		wildcard = jsonParser.parse("{\"value\":\"str\"}");
		assertNotNull(wildcard);
		assertEquals("str", wildcard.getValue());

		wildcard = jsonParser.parse("{\"value\":[1,2,3]}");
		assertNotNull(wildcard);
		assertTrue(wildcard.getValue() instanceof List);
		assertArrayEquals(new Object[] {1, 2, 3}, ((List) wildcard.getValue()).toArray());

		wildcard = jsonParser.parse(
			"{" +
				"\"value\": {" +
					"\"key\": \"value\"," +
					"\"inner\": {" +
						"\"key\": \"value\"" +
					"}" +
				"}" +
			"}");

		assertNotNull(wildcard);
		assertTrue(wildcard.getValue() instanceof Map);
		Map mapValue = (Map) wildcard.getValue();
		assertEquals(2, mapValue.size());
		assertEquals("value", mapValue.get("key"));
		assertTrue(mapValue.get("inner") instanceof Map);
		Map innerValue = (Map) mapValue.get("inner");
		assertEquals(1, innerValue.size());
		assertEquals("value", innerValue.get("key"));
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
		JoddJson.classMetadataName = "class";

		JsonParser jsonParser = new JsonParser();
		String json = FileUtil.readString(new File(dataRoot, "complex.json"));

		Aaa aaa = jsonParser
				.map("numbers.values", Byte.class)
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
			map = jsonParser.parse(json);
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

	@Test
	public void testCitmCatalog() throws Exception {
		FileInputStream fis = new FileInputStream(new File(dataRoot, "citm_catalog.json.gz"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		StreamUtil.copy(new GZIPInputStream(fis), out);

		String json = out.toString("UTF-8");

		fis.close();

		JsonParser jsonParser = new JsonParser();

		Map<String, Object> map;
		try {
			jsonParser.parse(json);
			map = jsonParser.parse(json);
		}
		catch (Exception ex) {
			fail(ex.toString());
			throw ex;
		}

		assertNotNull(map);
	}

	@Test
	public void testString() {
		assertEquals("123", new JsonParser().parse("\"" + "123" + "\""));

		assertEquals("12\n3", new JsonParser().parse("\"" + "12\\n3" + "\""));

		String big = RandomString.getInstance().randomAlpha(510);

		String jbig = big + "\\n";
		String rbig = big + "\n";

		assertEquals(512, jbig.length());

		assertEquals(rbig, new JsonParser().parse("\"" + jbig + "\""));

		jbig += "x";
		rbig += "x";

		assertEquals(rbig, new JsonParser().parse("\"" + jbig + "\""));

		jbig = "12" + jbig;
		rbig = "12" + rbig;

		assertEquals(rbig, new JsonParser().parse("\"" + jbig + "\""));
	}

	@Test
	public void testJsonModule() {
		assertTrue(Jodd.isModuleLoaded(Jodd.JSON));
	}

	@Test
	public void testInvalidJson() {
		try {
			new JsonParser().parse("\"" + "123" + "\",");
			fail("error");
		} catch (JsonException ignore) {
		}
		try {
			new JsonParser().parse("{\"aa\":\"" + "123" + "\",}");
			fail("error");
		} catch (JsonException ignore) {
		}
	}

	@Test
	public void testKeys() {
		String json = "{\"123\" : \"name\"}";

		Map<Long, String> map = new JsonParser().map("keys", Long.class).parse(json);

		assertEquals(1, map.size());
		assertEquals("name", map.get(Long.valueOf(123)));

		json = "{\"eee\" : {\"123\" : \"name\"}}";

		Map<String, Map<Long, String>> map2 = new JsonParser().map("values.keys", Long.class).parse(json);

		assertEquals(1, map2.size());

		map = map2.get("eee");
		assertEquals(1, map.size());
		assertEquals("name", map.get(Long.valueOf(123)));
	}

	public static class MapHolder {

		Map<Long, Long[]> data;

		public Map<Long, Long[]> getData() {
			return data;
		}

		public void setData(Map<Long, Long[]> data) {
			this.data = data;
		}
	}

	@Test
	public void testMapOfListArrays() {
		String json = "{\"data\" : {\"123\" : [1,2,3]}}";

		MapHolder mapHolder = new JsonParser().parse(json, MapHolder.class);

		Map<Long, Long[]> data = mapHolder.getData();

		assertEquals(1, data.size());

		Long[] longs = data.get(Long.valueOf(123));

		assertNotNull(longs);
	}


	public static class Glista {

		@JSON(name = "first_name")
		private String firstName;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
	}

	@Test
	public void testAnnotationNameChangeFirstTime() {
		String json = "{\"first_name\":\"Djordje\"}";

		Glista jsonGlista = new JsonParser().parse(json, Glista.class);

		assertEquals("Djordje", jsonGlista.getFirstName());
	}

	@Test
	public void testEscapeAtTheEndOfLongString() {
		String s = StringUtil.repeat('A', 800);
		String json = "\"" + s + "\\n\"";

		try {
			new JsonParser().parse(json);
		} catch (Exception ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testNamesWithDots() {
		String json = "{\"foo.bar\":123}";

		FooBar fooBar = new JsonParser().parse(json, FooBar.class);

		assertEquals(123, fooBar.getValue().intValue());

	}

	@Test
	public void testSets() {
		String json = "{\"names\":[\"Pig\",\"Joe\"],\"numbers\":[173,22]}";

		HitList hitList = JsonParser.create().parse(json, HitList.class);

		assertNotNull(hitList);

		assertEquals(2, hitList.getNames().size());
		assertTrue(hitList.getNames().contains("Pig"));
		assertTrue(hitList.getNames().contains("Joe"));

		assertEquals(2, hitList.getNumbers().size());
		assertTrue(hitList.getNumbers().contains(Integer.valueOf(173)));
		assertTrue(hitList.getNumbers().contains(Integer.valueOf(22)));
	}

}