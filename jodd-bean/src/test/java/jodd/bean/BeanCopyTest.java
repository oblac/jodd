// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.bean.data.FooBean;
import jodd.bean.data.FooBeanString;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BeanCopyTest {

	@Test
	public void testCopy() {
		FooBean fb = createFooBean();
		FooBean dest = new FooBean();
		BeanCopy.beans(fb, dest).copy();

		Integer v = (Integer) BeanUtil.getProperty(dest, "fooInteger");
		assertEquals(201, v.intValue());
		v = (Integer) BeanUtil.getProperty(dest, "fooint");
		assertEquals(202, v.intValue());
		Long vl = (Long) BeanUtil.getProperty(dest, "fooLong");
		assertEquals(203, vl.longValue());
		vl = (Long) BeanUtil.getProperty(dest, "foolong");
		assertEquals(204, vl.longValue());
		Byte vb = (Byte) BeanUtil.getProperty(dest, "fooByte");
		assertEquals(95, vb.intValue());
		vb = (Byte) BeanUtil.getProperty(dest, "foobyte");
		assertEquals(96, vb.intValue());
		Character c = (Character) BeanUtil.getProperty(dest, "fooCharacter");
		assertEquals('7', c.charValue());
		c = (Character) BeanUtil.getProperty(dest, "foochar");
		assertEquals('8', c.charValue());
		Boolean b = (Boolean) BeanUtil.getProperty(dest, "fooBoolean");
		assertTrue(b.booleanValue());
		b = (Boolean) BeanUtil.getProperty(dest, "fooboolean");
		assertFalse(b.booleanValue());
		Float f = (Float) BeanUtil.getProperty(dest, "fooFloat");
		assertEquals(209.0, f.floatValue(), 0.005);
		f = (Float) BeanUtil.getProperty(dest, "foofloat");
		assertEquals(210.0, f.floatValue(), 0.005);
		Double d = (Double) BeanUtil.getProperty(dest, "fooDouble");
		assertEquals(211.0, d.doubleValue(), 0.005);
		d = (Double) BeanUtil.getProperty(dest, "foodouble");
		assertEquals(212.0, d.doubleValue(), 0.005);
		String s = (String) BeanUtil.getProperty(dest, "fooString");
		assertEquals("213", s);
		String[] sa = (String[]) BeanUtil.getProperty(dest, "fooStringA");
		assertEquals(2, sa.length);
		assertEquals("214", sa[0]);
		assertEquals("215", sa[1]);
		assertSame(dest.getFooStringA(), sa);


		FooBean empty = new FooBean();
		BeanCopy.beans(empty, dest).copy();

		v = (Integer) BeanUtil.getProperty(dest, "fooInteger");
		assertNull(v);
		v = (Integer) BeanUtil.getProperty(dest, "fooint");
		assertEquals(0, v.intValue());
		vl = (Long) BeanUtil.getProperty(dest, "fooLong");
		assertNull(vl);
		vl = (Long) BeanUtil.getProperty(dest, "foolong");
		assertEquals(0, vl.longValue());
		vb = (Byte) BeanUtil.getProperty(dest, "fooByte");
		assertNull(vb);
		vb = (Byte) BeanUtil.getProperty(dest, "foobyte");
		assertEquals(0, vb.byteValue());
		c = (Character) BeanUtil.getProperty(dest, "fooCharacter");
		assertNull(c);
		c = (Character) BeanUtil.getProperty(dest, "foochar");
		assertEquals(0, c.charValue());
		b = (Boolean) BeanUtil.getProperty(dest, "fooBoolean");
		assertNull(b);
		b = (Boolean) BeanUtil.getProperty(dest, "fooboolean");
		assertFalse(b);
		f = (Float) BeanUtil.getProperty(dest, "fooFloat");
		assertNull(f);
		f = (Float) BeanUtil.getProperty(dest, "foofloat");
		assertEquals(0, f, 0.005);
		d = (Double) BeanUtil.getProperty(dest, "fooDouble");
		assertNull(d);
		d = (Double) BeanUtil.getProperty(dest, "foodouble");
		assertEquals(0, d, 0.005);
		s = (String) BeanUtil.getProperty(dest, "fooString");
		assertNull(s);
		sa = (String[]) BeanUtil.getProperty(dest, "fooStringA");
		assertNull(sa);
	}

	@Test
	public void testCopyIncludes() {
		FooBean fb = createFooBean();
		FooBean dest = new FooBean();
		BeanCopy.beans(fb, dest).include("fooInteger", "fooLong").copy();

		Integer v = (Integer) BeanUtil.getProperty(dest, "fooInteger");
		assertEquals(201, v.intValue());
		Long vl = (Long) BeanUtil.getProperty(dest, "fooLong");
		assertEquals(203, vl.longValue());
		v = (Integer) BeanUtil.getProperty(dest, "fooint");
		assertEquals(0, v.intValue());
		vl = (Long) BeanUtil.getProperty(dest, "foolong");
		assertEquals(0, vl.longValue());
		Byte vb = (Byte) BeanUtil.getProperty(dest, "fooByte");
		assertNull(vb);
		vb = (Byte) BeanUtil.getProperty(dest, "foobyte");
		assertEquals(0, vb.byteValue());
		Character c = (Character) BeanUtil.getProperty(dest, "fooCharacter");
		assertNull(c);
		c = (Character) BeanUtil.getProperty(dest, "foochar");
		assertEquals(0, c.charValue());
		Boolean b = (Boolean) BeanUtil.getProperty(dest, "fooBoolean");
		assertNull(b);
		b = (Boolean) BeanUtil.getProperty(dest, "fooboolean");
		assertFalse(b);
		Float f = (Float) BeanUtil.getProperty(dest, "fooFloat");
		assertNull(f);
		f = (Float) BeanUtil.getProperty(dest, "foofloat");
		assertEquals(0, f, 0.005);
		Double d = (Double) BeanUtil.getProperty(dest, "fooDouble");
		assertNull(d);
		d = (Double) BeanUtil.getProperty(dest, "foodouble");
		assertEquals(0, d, 0.005);
		String s = (String) BeanUtil.getProperty(dest, "fooString");
		assertNull(s);
		String[] sa = (String[]) BeanUtil.getProperty(dest, "fooStringA");
		assertNull(sa);
	}

	@Test
	public void testCopyExcludes() {
		FooBean fb = createFooBean();
		FooBean dest = new FooBean();
		BeanCopy.beans(fb, dest).exclude("fooInteger", "fooLong").copy();

		Integer v = (Integer) BeanUtil.getProperty(dest, "fooInteger");
		assertNull(v);
		v = (Integer) BeanUtil.getProperty(dest, "fooint");
		assertEquals(202, v.intValue());
		Long vl = (Long) BeanUtil.getProperty(dest, "fooLong");
		assertNull(vl);
		vl = (Long) BeanUtil.getProperty(dest, "foolong");
		assertEquals(204, vl.longValue());
		Byte vb = (Byte) BeanUtil.getProperty(dest, "fooByte");
		assertEquals(95, vb.intValue());
		vb = (Byte) BeanUtil.getProperty(dest, "foobyte");
		assertEquals(96, vb.intValue());
		Character c = (Character) BeanUtil.getProperty(dest, "fooCharacter");
		assertEquals('7', c.charValue());
		c = (Character) BeanUtil.getProperty(dest, "foochar");
		assertEquals('8', c.charValue());
		Boolean b = (Boolean) BeanUtil.getProperty(dest, "fooBoolean");
		assertTrue(b.booleanValue());
		b = (Boolean) BeanUtil.getProperty(dest, "fooboolean");
		assertFalse(b.booleanValue());
		Float f = (Float) BeanUtil.getProperty(dest, "fooFloat");
		assertEquals(209.0, f.floatValue(), 0.005);
		f = (Float) BeanUtil.getProperty(dest, "foofloat");
		assertEquals(210.0, f.floatValue(), 0.005);
		Double d = (Double) BeanUtil.getProperty(dest, "fooDouble");
		assertEquals(211.0, d.doubleValue(), 0.005);
		d = (Double) BeanUtil.getProperty(dest, "foodouble");
		assertEquals(212.0, d.doubleValue(), 0.005);
		String s = (String) BeanUtil.getProperty(dest, "fooString");
		assertEquals("213", s);
		String[] sa = (String[]) BeanUtil.getProperty(dest, "fooStringA");
		assertEquals(2, sa.length);
		assertEquals("214", sa[0]);
		assertEquals("215", sa[1]);
		assertSame(dest.getFooStringA(), sa);
	}

	@Test
	public void testCopyTemplate() {
		FooBean fooBean = createFooBean();
		FooBean dest = new FooBean();

		BeanCopy.beans(fooBean, dest).includeByTemplate(FooBeanString.class).copy();

		Integer v = (Integer) BeanUtil.getProperty(dest, "fooInteger");
		assertNull(v);
		v = (Integer) BeanUtil.getProperty(dest, "fooint");
		assertEquals(0, v.intValue());
		Long vl = (Long) BeanUtil.getProperty(dest, "fooLong");
		assertNull(vl);
		vl = (Long) BeanUtil.getProperty(dest, "foolong");
		assertEquals(0, vl.longValue());
		Byte vb = (Byte) BeanUtil.getProperty(dest, "fooByte");
		assertNull(vb);
		vb = (Byte) BeanUtil.getProperty(dest, "foobyte");
		assertEquals(0, vb.byteValue());
		Character c = (Character) BeanUtil.getProperty(dest, "fooCharacter");
		assertNull(c);
		c = (Character) BeanUtil.getProperty(dest, "foochar");
		assertEquals(0, c.charValue());
		Boolean b = (Boolean) BeanUtil.getProperty(dest, "fooBoolean");
		assertNull(b);
		b = (Boolean) BeanUtil.getProperty(dest, "fooboolean");
		assertFalse(b.booleanValue());
		Float f = (Float) BeanUtil.getProperty(dest, "fooFloat");
		assertNull(f);
		f = (Float) BeanUtil.getProperty(dest, "foofloat");
		assertEquals(0, f.floatValue(), 0.005);
		Double d = (Double) BeanUtil.getProperty(dest, "fooDouble");
		assertNull(d);
		d = (Double) BeanUtil.getProperty(dest, "foodouble");
		assertEquals(0, d.doubleValue(), 0.005);
		String s = (String) BeanUtil.getProperty(dest, "fooString");
		assertEquals("213", s);
		String[] sa = (String[]) BeanUtil.getProperty(dest, "fooStringA");
		assertNull(sa);
	}




	static class Less {
		String data;

		Integer number;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public Integer getNumber() {
			return number;
		}

		public void setNumber(Integer number) {
			this.number = number;
		}
	}

	static class More {
		String data;

		String number;

		String boo;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}


		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getBoo() {
			return boo;
		}

		public void setBoo(String boo) {
			this.boo = boo;
		}
	}

	@Test
	public void testLessToMore() {
		Less less = new Less();
		less.data = "data";
		less.number = new Integer(2);
		More more = new More();
		BeanCopy.beans(less, more).declared(true).copy();
		assertEquals("data", more.data);
		assertEquals("2", more.number);

		more.data = "tij";
		more.number = "17";
		BeanCopy.beans(more, less).declared(true).copy();
		assertEquals("tij", less.data);
		assertEquals(17, less.number.intValue());
	}

	@Test
	public void testCopyMap() {
		Map map = new HashMap();
		map.put("fooint", Integer.valueOf(102));
		map.put("fooString", "mao");

		FooBean dest = new FooBean();
		BeanCopy.beans(map, dest).copy();
		assertEquals(102, dest.getFooint());
		assertEquals("mao", dest.getFooString());

		Map destMap = new HashMap();
		BeanCopy.beans(map, destMap).copy();
		assertEquals(2, destMap.size());
		assertEquals(Integer.valueOf(102), destMap.get("fooint"));
		assertEquals("mao", destMap.get("fooString"));
	}

	@Test
	public void testCopyIgnoreNulls() {
		FooBean fb = createFooBean();
		FooBean dest = new FooBean();

		dest.setFooInteger(Integer.valueOf(999));
		fb.setFooInteger(null);
		BeanCopy.beans(fb, dest).ignoreNulls(true).copy();

		Integer v = (Integer) BeanUtil.getProperty(dest, "fooInteger");
		assertEquals(999, v.intValue());
		v = (Integer) BeanUtil.getProperty(dest, "fooint");
		assertEquals(202, v.intValue());
		Long vl = (Long) BeanUtil.getProperty(dest, "fooLong");
		assertEquals(203, vl.longValue());
		vl = (Long) BeanUtil.getProperty(dest, "foolong");
		assertEquals(204, vl.longValue());
		Byte vb = (Byte) BeanUtil.getProperty(dest, "fooByte");
		assertEquals(95, vb.intValue());
		vb = (Byte) BeanUtil.getProperty(dest, "foobyte");
		assertEquals(96, vb.intValue());
		Character c = (Character) BeanUtil.getProperty(dest, "fooCharacter");
		assertEquals('7', c.charValue());
		c = (Character) BeanUtil.getProperty(dest, "foochar");
		assertEquals('8', c.charValue());
		Boolean b = (Boolean) BeanUtil.getProperty(dest, "fooBoolean");
		assertTrue(b.booleanValue());
		b = (Boolean) BeanUtil.getProperty(dest, "fooboolean");
		assertFalse(b.booleanValue());
		Float f = (Float) BeanUtil.getProperty(dest, "fooFloat");
		assertEquals(209.0, f.floatValue(), 0.005);
		f = (Float) BeanUtil.getProperty(dest, "foofloat");
		assertEquals(210.0, f.floatValue(), 0.005);
		Double d = (Double) BeanUtil.getProperty(dest, "fooDouble");
		assertEquals(211.0, d.doubleValue(), 0.005);
		d = (Double) BeanUtil.getProperty(dest, "foodouble");
		assertEquals(212.0, d.doubleValue(), 0.005);
		String s = (String) BeanUtil.getProperty(dest, "fooString");
		assertEquals("213", s);
		String[] sa = (String[]) BeanUtil.getProperty(dest, "fooStringA");
		assertEquals(2, sa.length);
		assertEquals("214", sa[0]);
		assertEquals("215", sa[1]);
		assertSame(dest.getFooStringA(), sa);
	}

	private FooBean createFooBean() {
		FooBean fb = new FooBean();
		fb.setFooInteger(new Integer(201));
		fb.setFooint(202);
		fb.setFooLong(new Long(203));
		fb.setFoolong(204);
		fb.setFooByte(new Byte((byte) 95));
		fb.setFoobyte((byte) 96);
		fb.setFooCharacter(new Character('7'));
		fb.setFoochar('8');
		fb.setFooBoolean(Boolean.TRUE);
		fb.setFooboolean(false);
		fb.setFooFloat(new Float(209.0));
		fb.setFoofloat((float) 210.0);
		fb.setFooDouble(new Double(211.0));
		fb.setFoodouble(212.0);
		fb.setFooString("213");
		fb.setFooStringA(new String[]{"214", "215"});
		return fb;
	}
}
