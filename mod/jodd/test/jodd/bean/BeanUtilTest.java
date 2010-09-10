// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import jodd.bean.data.GetIsBool;
import jodd.bean.data.IsGetBool;
import jodd.bean.modifier.TrimStringsBeanModifier;
import jodd.bean.data.Abean;
import jodd.bean.data.Bbean;
import jodd.bean.data.Cbean;
import jodd.bean.data.FooBean;
import jodd.bean.data.FooBean2;
import jodd.bean.data.FooBean3;
import jodd.bean.data.FooBean4;
import jodd.bean.data.FooBeanSlim;
import jodd.bean.data.Gig;
import jodd.bean.data.XBean;
import jodd.bean.data.ZBean;
import jodd.mutable.MutableInteger;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import junit.framework.TestCase;

public class BeanUtilTest extends TestCase {

	public void testSimpleProperty() {
		FooBean fb = new FooBean();

		// read non initialized property (null)
		assertNull(BeanUtil.getSimpleProperty(fb, "fooInteger", false));
		assertTrue(BeanUtil.hasProperty(fb, "fooInteger"));
		assertEquals(Integer.class, BeanUtil.getPropertyType(fb, "fooInteger"));

		// set property
		BeanUtil.setSimpleProperty(fb, "fooInteger", new Integer(173), false);
		// read initialized property
		assertEquals(new Integer(173), BeanUtil.getSimpleProperty(fb, "fooInteger", false));

		// read non-initialized simple property (zero)
		assertEquals(new Integer(0), BeanUtil.getSimpleProperty(fb, "fooint", false));
		assertTrue(BeanUtil.hasProperty(fb, "fooint"));
		assertEquals(int.class, BeanUtil.getPropertyType(fb, "fooint"));
		assertFalse(BeanUtil.hasProperty(fb, "fooint-xxx"));
		assertNull(BeanUtil.getPropertyType(fb, "fooint-xxx"));

		// read forced non-initialized property (not null)
		assertTrue(BeanUtil.hasProperty(fb, "fooByte"));
		assertEquals(Byte.class, BeanUtil.getPropertyType(fb, "fooByte"));
		assertEquals(new Byte((byte)0), BeanUtil.getSimplePropertyForced(fb, "fooByte", false));

		Map m = new HashMap();
		// set property in map
		BeanUtil.setSimpleProperty(m, "foo", new Integer(173), false);
		// read property from map
		assertTrue(BeanUtil.hasProperty(m, "foo"));
		assertEquals(new Integer(173), BeanUtil.getSimpleProperty(m, "foo", false));

		// read non-initialized map property
		assertTrue(BeanUtil.hasProperty(fb, "fooMap"));
		assertEquals(Map.class, BeanUtil.getPropertyType(fb, "fooMap"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooMap", false));
		// read forced non-initialized map property
		assertNotNull(BeanUtil.getSimplePropertyForced(fb, "fooMap", false));

		// read non-initialized list property
		assertTrue(BeanUtil.hasProperty(fb, "fooList"));
		assertEquals(List.class, BeanUtil.getPropertyType(fb, "fooList"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooList", false));
		// read forced non-initialized list property
		assertNotNull(BeanUtil.getSimplePropertyForced(fb, "fooList", false));

        // read non-initialized array (null)
		assertTrue(BeanUtil.hasProperty(fb, "fooStringA"));
		assertEquals(String[].class, BeanUtil.getPropertyType(fb, "fooStringA"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooStringA", false));
		String[] tmp = new String[10];
		tmp[2] = "foo";
		// set array property
		BeanUtil.setSimpleProperty(fb, "fooStringA", tmp, false);
		// read array property
		tmp = (String[]) BeanUtil.getSimpleProperty(fb, "fooStringA", false);
		assertEquals("foo", tmp[2]);

		fb.setFooStringA(null);
		// read non-initialized array property
		assertTrue(BeanUtil.hasProperty(fb, "fooStringA"));
		assertEquals(String[].class, BeanUtil.getPropertyType(fb, "fooStringA"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooStringA", false));
		// read forced non-initialized array property
		assertNotNull(BeanUtil.getSimplePropertyForced(fb, "fooStringA", false));
	}

	public void testSimplePropertySlimPrivate() {
		FooBeanSlim fb = new FooBeanSlim();

		// read non initialized property (null)
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooInteger"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooInteger", true));
		assertNull(BeanUtil.getPropertyType(fb, "fooInteger"));
		assertEquals(Integer.class, BeanUtil.getDeclaredPropertyType(fb, "fooInteger"));

		// set property
		BeanUtil.setSimpleProperty(fb, "fooInteger", new Integer(173), true);
		// read initialized property
		assertEquals(new Integer(173), BeanUtil.getSimpleProperty(fb, "fooInteger", true));

		// read non-initialized simple property (zero)
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooint"));
		assertEquals(new Integer(0), BeanUtil.getSimpleProperty(fb, "fooint", true));

		// read forced non-initialized property (not null)
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooByte"));
		assertEquals(new Byte((byte)0), BeanUtil.getSimplePropertyForced(fb, "fooByte", true));

		Map m = new HashMap();
		// set property in map
		assertFalse(BeanUtil.hasDeclaredProperty(m, "foo"));
		BeanUtil.setSimpleProperty(m, "foo", new Integer(173), true);
		// read property from map
		assertTrue(BeanUtil.hasDeclaredProperty(m, "foo"));
		assertEquals(new Integer(173), BeanUtil.getSimpleProperty(m, "foo", true));

		// read non-initialized map property
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooMap"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooMap", true));
		// read forced non-initialized map property
		assertNotNull(BeanUtil.getSimplePropertyForced(fb, "fooMap", true));

		// read non-initialized list property
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooList"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooList", true));
		// read forced non-initialized list property
		assertNotNull(BeanUtil.getSimplePropertyForced(fb, "fooList", true));

        // read non-initialized array (null)
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooStringA"));
		assertNull(BeanUtil.getSimpleProperty(fb, "fooStringA", true));
		String[] tmp = new String[10];
		tmp[2] = "foo";
		// set array property
		BeanUtil.setSimpleProperty(fb, "fooStringA", tmp, true);
		// read array property
		tmp = (String[]) BeanUtil.getSimpleProperty(fb, "fooStringA", true);
		assertEquals("foo", tmp[2]);

		fb = new FooBeanSlim();
		// read non-initialized array property
		assertNull(BeanUtil.getSimpleProperty(fb, "fooStringA", true));
		// read forced non-initialized array property
		assertNotNull(BeanUtil.getSimplePropertyForced(fb, "fooStringA", true));
	}

	public void testIndexProperty() {
		FooBean fb = new FooBean();

		// read forced non-initialized array property
		assertNull(fb.getFooStringA());
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooStringA[0]"));
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA[0]"));
		try {
			BeanUtil.getIndexProperty(fb, "fooStringA[0]", false, true);
			fail();
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}
		assertNotNull(fb.getFooStringA());
		assertEquals(0, fb.getFooStringA().length);

		// set array property (non-forced)
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooStringA[7]"));
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA[7]"));
		try {
			BeanUtil.setIndexProperty(fb, "fooStringA[7]", "xxx", false, false);
			fail();
		} catch(ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}

		// set forced array property
		BeanUtil.setIndexProperty(fb, "fooStringA[40]", "zzz", false, true);
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooStringA[40]"));
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA[40]"));
		assertEquals(String[].class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA"));
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA[40]"));
		assertEquals("zzz", fb.getFooStringA()[40]);
		assertEquals(41, fb.getFooStringA().length);

		// set null
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooStringA[43]"));
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA[43]"));
		BeanUtil.setIndexProperty(fb, "fooStringA[43]", null, false, true);
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooStringA[43]"));
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA[43]"));
		assertNull(fb.getFooStringA()[43]);
		assertEquals(44, fb.getFooStringA().length);

		// get forced
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooStringA[15]"));
		assertNotNull(BeanUtil.getIndexProperty(fb, "fooStringA[15]", false, true));
		assertNull(fb.getFooStringA()[0]);
		assertNotNull(fb.getFooStringA()[15]);


		// set uninitialized array property
		fb.setFooStringA(null);
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(fb, "fooStringA[43]"));
		BeanUtil.setIndexProperty(fb, "fooStringA[7]", "ccc", false, true);
		assertEquals("ccc", fb.getFooStringA()[7]);




		
		// read forced non-initialized list property
		assertNull(fb.getFooList());
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooList[1]"));
		assertEquals(Object.class, BeanUtil.getDeclaredPropertyType(fb, "fooList[1]"));
		try {
			BeanUtil.getIndexProperty(fb, "fooList[1]", false, true);
			fail();
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}
		assertNotNull(fb.getFooList());

		// set list property (non-forced)
		try {
			BeanUtil.setIndexProperty(fb, "fooList[1]", "xxx", false, false);
			fail();
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}

		// set forced list property
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooList[40]"));
		BeanUtil.setIndexProperty(fb, "fooList[40]", "zzz", false, true);
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooList[40]"));
		assertEquals(Object.class, BeanUtil.getDeclaredPropertyType(fb, "fooList[40]"));        // method type, not values type
		assertEquals(Object.class, BeanUtil.getDeclaredPropertyType(fb, "fooList[39]"));
		assertEquals("zzz", fb.getFooList().get(40));
		assertEquals(41, fb.getFooList().size());

		// set forced unitialized list property
		fb.setFooList(null);
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooList[1]"));
		BeanUtil.setIndexProperty(fb, "fooList[1]", "xxx", false, true);
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooList[1]"));
		assertEquals("xxx", fb.getFooList().get(1));
		assertEquals(2, fb.getFooList().size());


        // read forced non-initialized map property
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooMap[foo]"));
		assertEquals(Object.class, BeanUtil.getDeclaredPropertyType(fb, "fooMap[foo]"));
		assertNull(BeanUtil.getIndexProperty(fb, "fooMap[foo]", false, true));
		assertNotNull(fb.getFooMap());
		// set non-initialized map property
		fb.setFooMap(null);
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "fooMap[foo]"));
		BeanUtil.setIndexProperty(fb, "fooMap[foo]", "xxx", false, true);
		assertTrue(BeanUtil.hasDeclaredProperty(fb, "fooMap[foo]"));
		assertEquals("xxx", fb.getFooMap().get("foo"));
		assertEquals(1, fb.getFooMap().size());
	}

	public void testIndexPropertySlimPrivate() {
		FooBeanSlim fb = new FooBeanSlim();

		// read forced non-initialized array property
		assertNull(fb.getStringA());
		try {
			BeanUtil.getIndexProperty(fb, "fooStringA[0]", true, true);
			fail();
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}
		assertNotNull(fb.getStringA());
		assertEquals(0, fb.getStringA().length);

		// set array property (non-forced)
		try {
			BeanUtil.setIndexProperty(fb, "fooStringA[7]", "xxx", true, false);
			fail();
		} catch(ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}

		// set forced array property
		BeanUtil.setIndexProperty(fb, "fooStringA[40]", "zzz", true, true);
		assertEquals("zzz", fb.getStringA()[40]);
		assertEquals(41, fb.getStringA().length);

		BeanUtil.setIndexProperty(fb, "fooStringA[43]", null, true, true);
		assertNull(fb.getStringA()[43]);
		assertEquals(44, fb.getStringA().length);


		// set uninitialized array property
		fb = new FooBeanSlim();
		assertNull(fb.getStringA());
		BeanUtil.setIndexProperty(fb, "fooStringA[7]", "ccc", true, true);
		assertNotNull(fb.getStringA());
		assertEquals("ccc", fb.getStringA()[7]);


		// read forced non-initialized list property
		assertNull(fb.getList());
		try {
			BeanUtil.getIndexProperty(fb, "fooList[1]", true, true);
			fail();
		} catch(IndexOutOfBoundsException ioobex) {
			// ignore
		}
		assertNotNull(fb.getList());

		// set list property (non-forced)
		try {
			BeanUtil.setIndexProperty(fb, "fooList[1]", "xxx", true, false);
			fail();
		} catch(IndexOutOfBoundsException ioobex) {
			// ignore
		}
		
		// set forced list property
		BeanUtil.setIndexProperty(fb, "fooList[40]", "zzz", true, true);
		assertEquals("zzz", fb.getList().get(40));
		assertEquals(41, fb.getList().size());

		// set forced unitialized list property
		fb = new FooBeanSlim();
		BeanUtil.setIndexProperty(fb, "fooList[1]", "xxx", true, true);
		assertEquals("xxx", fb.getList().get(1));

		// read forced non-initialized map property
		assertNull(fb.getMap());
		assertNull(BeanUtil.getIndexProperty(fb, "fooMap[foo]", true, true));
		assertNotNull(fb.getMap());

		// set non-initialized map property
		fb = new FooBeanSlim();
		assertNull(fb.getMap());
		BeanUtil.setIndexProperty(fb, "fooMap[foo]", "xxx", true, true);
		assertNotNull(fb.getMap());
		assertEquals("xxx", fb.getMap().get("foo"));
	}


	// ---------------------------------------------------------------- types

	public void testSetPropertyNumbers() {
		FooBean fb = new FooBean();

		// Integer
		String propName = "fooInteger";
		BeanUtil.setProperty(fb, propName, new Integer(1));
		assertEquals(1, fb.getFooInteger().intValue());
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooInteger());
		BeanUtil.setProperty(fb, propName, "2");			// valid string
		assertEquals(2, fb.getFooInteger().intValue());
		try {
			BeanUtil.setProperty(fb, propName, "x");		// invalid string - value stays the same
			fail();
		} catch(Exception ex) {}
		assertEquals(2, fb.getFooInteger().intValue());

		// int
		propName = "fooint";
		BeanUtil.setProperty(fb, propName, new Integer(1));
		assertEquals(1, fb.getFooint());

		try {
			BeanUtil.setProperty(fb, propName, null); 	// null is not an int
			fail();
		} catch(Exception ex) {}
		assertEquals(1, fb.getFooint());

		BeanUtil.setProperty(fb, propName, "2");
		assertEquals(2, fb.getFooint());

		try {
			BeanUtil.setProperty(fb, propName, "w");	// invalid string
			fail();
		} catch (Exception ex) {}
		assertEquals(2, fb.getFooint());


		// Long
		propName = "fooLong";
		BeanUtil.setProperty(fb, propName, new Long(1));
		assertEquals(1L, fb.getFooLong().longValue());
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3L, fb.getFooLong().longValue());
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooLong());
		BeanUtil.setProperty(fb, propName, "2");			// valid string
		assertEquals(2L, fb.getFooLong().longValue());

		try {
			BeanUtil.setProperty(fb, propName, "x");		// invalid string - value stays the same
			fail();
		} catch (Exception ex) {}
		assertEquals(2L, fb.getFooLong().longValue());

		// long
		propName = "foolong";
		BeanUtil.setProperty(fb, propName, new Long(1));
		assertEquals(1L, fb.getFoolong());
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3L, fb.getFoolong());

		try {
			BeanUtil.setProperty(fb, propName, null); 			// null is not a long
			fail();
		} catch (Exception ex) {}

		assertEquals(3L, fb.getFoolong());
		BeanUtil.setProperty(fb, propName, "2");			// valid string
		assertEquals(2L, fb.getFoolong());
		try {
			BeanUtil.setProperty(fb, propName, "w");		// invalid string
			fail();
		} catch (Exception ex) {}
		assertEquals(2L, fb.getFoolong());

		// Byte
		propName = "fooByte";
		BeanUtil.setProperty(fb, propName, new Byte((byte) 1));
		assertEquals(1, fb.getFooByte().byteValue());
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3, fb.getFooByte().byteValue());
		BeanUtil.setProperty(fb, propName, new Integer(257));
		assertEquals(1, fb.getFooByte().byteValue());				// lower byte of 257
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooByte());
		BeanUtil.setProperty(fb, propName, "2");					// valid string
		assertEquals(2, fb.getFooByte().byteValue());

		try {
			BeanUtil.setProperty(fb, propName, "x");			// invalid string - value stays the same
			fail();
		} catch (Exception ex) {}
		assertEquals(2, fb.getFooByte().byteValue());

		// byte
		propName = "foobyte";
		BeanUtil.setProperty(fb, propName, new Byte((byte) 1));
		assertEquals(1, fb.getFoobyte());
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3, fb.getFoobyte());
		BeanUtil.setProperty(fb, propName, new Integer(257));
		assertEquals(1, fb.getFoobyte());
		try {
			BeanUtil.setProperty(fb, propName, null);		// null is not a byte
			fail();
		} catch (Exception ex) {}
		assertEquals(1, fb.getFoobyte());
		BeanUtil.setProperty(fb, propName, "2");			// valid string
		assertEquals(2, fb.getFoobyte());
		try {
			BeanUtil.setProperty(fb, propName, "x");		// invalid string - value stays the same
			fail();
		} catch(Exception ex) {}
		assertEquals(2, fb.getFoobyte());

		// Boolean
		propName = "fooBoolean";
		BeanUtil.setProperty(fb, propName, Boolean.TRUE);
		assertTrue(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, Boolean.FALSE);
		assertFalse(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "yes");
		assertTrue(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "y");
		assertTrue(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "true");
		assertTrue(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "on");
		assertTrue(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "1");
		assertTrue(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "no");
		assertFalse(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "n");
		assertFalse(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "false");
		assertFalse(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "off");
		assertFalse(fb.getFooBoolean().booleanValue());
		BeanUtil.setProperty(fb, propName, "0");
		assertFalse(fb.getFooBoolean().booleanValue());

		// boolean
		propName = "fooboolean";
		BeanUtil.setProperty(fb, propName, Boolean.TRUE);
		assertTrue(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, Boolean.FALSE);
		assertFalse(fb.getFooboolean());

		try {
			BeanUtil.setProperty(fb, propName, null);
			fail();
		} catch(Exception ex) {}

		assertFalse(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "yes");
		assertTrue(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "y");
		assertTrue(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "true");
		assertTrue(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "on");
		assertTrue(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "1");
		assertTrue(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "no");
		assertFalse(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "n");
		assertFalse(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "false");
		assertFalse(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "off");
		assertFalse(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, "0");
		assertFalse(fb.getFooboolean());

		// Float
		propName = "fooFloat";
		BeanUtil.setProperty(fb, propName, new Float(1.1));
		assertEquals(1.1, fb.getFooFloat().floatValue(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFooFloat().floatValue(), 0.0005);
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooFloat());
		BeanUtil.setProperty(fb, propName, "2.2");			// valid string
		assertEquals(2.2, fb.getFooFloat().floatValue(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "x");		// invalid string - value stays the same
			fail();
		} catch (Exception ex) {}
		assertEquals(2.2, fb.getFooFloat().floatValue(), 0.0005);

		// float
		propName = "foofloat";
		BeanUtil.setProperty(fb, propName, new Float(1.1));
		assertEquals(1.1, fb.getFoofloat(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFoofloat(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, null);			// null is not a long
			fail();
		} catch(Exception ex) {}
		assertEquals(3.0, fb.getFoofloat(), 0.0005);
		BeanUtil.setProperty(fb, propName, "2.2");				// valid string

		assertEquals(2.2, fb.getFoofloat(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "w");			// invalid string
			fail();
		} catch(Exception ex) {}
		assertEquals(2.2, fb.getFoofloat(), 0.0005);

		// Double
		propName = "fooDouble";
		BeanUtil.setProperty(fb, propName, new Double(1.1));
		assertEquals(1.1, fb.getFooDouble().doubleValue(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFooDouble().doubleValue(), 0.0005);
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooDouble());
		BeanUtil.setProperty(fb, propName, "2.2");			// valid string
		assertEquals(2.2, fb.getFooDouble().doubleValue(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "x");		// invalid string - value stays the same
			fail();
		} catch (Exception ex) {}
		assertEquals(2.2, fb.getFooDouble().doubleValue(), 0.0005);

		// double
		propName = "foodouble";
		BeanUtil.setProperty(fb, propName, new Double(1.1));
		assertEquals(1.1, fb.getFoodouble(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFoodouble(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, null);		// null is not a long
			fail();
		} catch(Exception ex) {}
		assertEquals(3.0, fb.getFoodouble(), 0.0005);
		BeanUtil.setProperty(fb, propName, "2.2");				// valid string

		assertEquals(2.2, fb.getFoodouble(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "w");					// invalid string
			fail();
		} catch(Exception ex) {}
		assertEquals(2.2, fb.getFoodouble(), 0.0005);
	}


	public void testSetPropertySql() {
		FooBean2 fb = new FooBean2();

		String propName = "fooTimestamp";
		//noinspection deprecation
		Timestamp ts = new Timestamp(101, 0, 17, 1, 2, 3, 4);
		assertEquals(Timestamp.class, BeanUtil.getPropertyType(fb, propName));
		BeanUtil.setProperty(fb, propName, ts);
		assertEquals("2001-01-17 01:02:03.000000004", fb.getFooTimestamp().toString());

		propName = "fooTime";
		//noinspection deprecation
		Time t = new Time(17, 13, 15);
		BeanUtil.setProperty(fb, propName, t);

		assertEquals("17:13:15", fb.getFooTime().toString());

		propName = "fooDate";
		//noinspection deprecation
		Date d = new Date(101, 1, 17);
		assertEquals(Date.class, BeanUtil.getPropertyType(fb, propName));
		BeanUtil.setProperty(fb, propName, d);
		assertEquals("2001-02-17", fb.getFooDate().toString());
	}


	public void testSetPropertyMath() {
		FooBean2 fb = new FooBean2();
		String propName = "fooBigDecimal";
		assertEquals(BigDecimal.class, BeanUtil.getPropertyType(fb, propName));
		BeanUtil.setProperty(fb, propName, new BigDecimal(1.2));
		assertEquals(1.2, fb.getFooBigDecimal().doubleValue(), 0.0005);
	}

	public void testSetPropertyString() {
		FooBean fb = new FooBean();

		// String
		String propName = "fooString";
		BeanUtil.setProperty(fb, propName, "string");
		assertEquals("string", fb.getFooString());
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooString());

		// String array
		propName = "fooStringA";
		String[] sa = new String[] {"one", "two", "three"};
		BeanUtil.setProperty(fb, propName, sa);
		assertEquals("one", fb.getFooStringA()[0]);
		assertEquals("two", fb.getFooStringA()[1]);
		assertEquals("three", fb.getFooStringA()[2]);
		BeanUtil.setProperty(fb, propName, "just a string");
		sa = (String[]) BeanUtil.getProperty(fb, propName);
		assertEquals(1, sa.length);
		assertEquals("just a string", sa[0]);

		// Character
		propName = "fooCharacter";
		BeanUtil.setProperty(fb, propName, new Character('a'));
		assertEquals('a', fb.getFooCharacter().charValue());
		BeanUtil.setProperty(fb, propName, "1");
		assertEquals('1', fb.getFooCharacter().charValue());
		BeanUtil.setProperty(fb, propName, new Integer(789));
		assertEquals(789, fb.getFooCharacter().charValue());

		// char
		propName = "foochar";
		BeanUtil.setProperty(fb, propName, new Character('a'));
		assertEquals('a', fb.getFoochar());
		BeanUtil.setProperty(fb, propName, "1");
		assertEquals('1', fb.getFoochar());
		BeanUtil.setProperty(fb, propName, new Integer(789));
		assertEquals(789, fb.getFoochar());
	}


	public void	testLoaders() {
		HashMap map = new HashMap();

		map.put("fooInteger", new Integer(1));
		map.put("fooint", new Integer(2));
		map.put("fooLong", new Long(3));
		map.put("foolong", new Long(4));
		map.put("fooByte", new Byte((byte)5));
		map.put("foobyte", new Byte((byte)6));
		map.put("fooCharacter", new Character('7'));
		map.put("foochar", new Character('8'));
		map.put("fooBoolean", Boolean.TRUE);
		map.put("fooboolean", Boolean.FALSE);
		map.put("fooFloat", new Float(9.0));
		map.put("foofloat", new Float(10.0));
		map.put("fooDouble", new Double(11.0));
		map.put("foodouble", new Double(12.0));
		map.put("fooString", "13");
		map.put("fooStringA", new String[]{"14", "15"});

		FooBean fb = new FooBean();
		BeanTool.load(fb, map);

		assertEquals(1, fb.getFooInteger().intValue());
		assertEquals(2, fb.getFooint());
		assertEquals(3, fb.getFooLong().longValue());
		assertEquals(4, fb.getFoolong());
		assertEquals(5, fb.getFooByte().byteValue());
		assertEquals(6, fb.getFoobyte());
		assertEquals('7', fb.getFooCharacter().charValue());
		assertEquals('8', fb.getFoochar());
		assertTrue(fb.getFooBoolean().booleanValue());
		assertFalse(fb.getFooboolean());
		assertEquals(9.0, fb.getFooFloat().floatValue(), 0.005);
		assertEquals(10.0, fb.getFoofloat(), 0.005);
		assertEquals(11.0, fb.getFooDouble().doubleValue(), 0.005);
		assertEquals(12.0, fb.getFoodouble(), 0.005);
		assertEquals("13", fb.getFooString());
		assertEquals("14", fb.getFooStringA()[0]);
		assertEquals("15", fb.getFooStringA()[1]);

		map.put("FooInteger", new Integer(1));
		map.put("Fooint", new Integer(2));
		map.put("FooLong", new Long(3));
		map.put("Foolong", new Long(4));
		map.put("FooByte", new Byte((byte)5));
		map.put("Foobyte", new Byte((byte)6));
		map.put("FooCharacter", new Character('7'));
		map.put("Foochar", new Character('8'));
		map.put("FooBoolean", Boolean.TRUE);
		map.put("Fooboolean", Boolean.FALSE);
		map.put("FooFloat", new Float(9.0));
		map.put("Foofloat", new Float(10.0));
		map.put("FooDouble", new Double(11.0));
		map.put("Foodouble", new Double(12.0));
		map.put("FooString", "13");
		map.put("FooStringA", new String[]{"14", "15"});

		fb = new FooBean();
		BeanTool.load(fb, map);

		assertEquals(1, fb.getFooInteger().intValue());
		assertEquals(2, fb.getFooint());
		assertEquals(3, fb.getFooLong().longValue());
		assertEquals(4, fb.getFoolong());
		assertEquals(5, fb.getFooByte().byteValue());
		assertEquals(6, fb.getFoobyte());
		assertEquals('7', fb.getFooCharacter().charValue());
		assertEquals('8', fb.getFoochar());
		assertTrue(fb.getFooBoolean().booleanValue());
		assertFalse(fb.getFooboolean());
		assertEquals(9.0, fb.getFooFloat().floatValue(), 0.005);
		assertEquals(10.0, fb.getFoofloat(), 0.005);
		assertEquals(11.0, fb.getFooDouble().doubleValue(), 0.005);
		assertEquals(12.0, fb.getFoodouble(), 0.005);
		assertEquals("13", fb.getFooString());
		assertEquals("14", fb.getFooStringA()[0]);
		assertEquals("15", fb.getFooStringA()[1]);

	}

	public void	testForEach() {
		FooBean fb = new FooBean();
		fb.setFooString("   xxx   ");
		fb.setFooStringA(new String[] {"   xxx   ", "  yy  ", " z "});
		new TrimStringsBeanModifier().modify(fb);
		assertEquals("xxx", fb.getFooString());
		assertEquals("xxx", fb.getFooStringA()[0]);
		assertEquals("yy", fb.getFooStringA()[1]);
		assertEquals("z", fb.getFooStringA()[2]);
	}


	public void testGet() {
		FooBean fb = new FooBean();
		fb.setFooInteger(new Integer(101));
		fb.setFooint(102);
		fb.setFooLong(new Long(103));
		fb.setFoolong(104);
		fb.setFooByte(new Byte((byte) 105));
		fb.setFoobyte((byte) 106);
		fb.setFooCharacter(new Character('7'));
		fb.setFoochar('8');
		fb.setFooBoolean(Boolean.TRUE);
		fb.setFooboolean(false);
		fb.setFooFloat(new Float(109.0));
		fb.setFoofloat((float)110.0);
		fb.setFooDouble(new Double(111.0));
		fb.setFoodouble(112.0);
		fb.setFooString("113");
		fb.setFooStringA(new String[] {"114", "115"} );

		Integer v =	(Integer) BeanUtil.getProperty(fb, "fooInteger");
		assertEquals(101, v.intValue());
		v = (Integer) BeanUtil.getProperty(fb, "fooint");
		assertEquals(102, v.intValue());
		Long vl = (Long) BeanUtil.getProperty(fb, "fooLong");
		assertEquals(103, vl.longValue());
		vl = (Long) BeanUtil.getProperty(fb, "foolong");
		assertEquals(104, vl.longValue());
		Byte vb = (Byte) BeanUtil.getProperty(fb, "fooByte");
		assertEquals(105, vb.intValue());
		vb = (Byte) BeanUtil.getProperty(fb, "foobyte");
		assertEquals(106, vb.intValue());
		Character c = (Character) BeanUtil.getProperty(fb, "fooCharacter");
		assertEquals('7', c.charValue());
		c = (Character) BeanUtil.getProperty(fb, "foochar");
		assertEquals('8', c.charValue());
		Boolean b = (Boolean) BeanUtil.getProperty(fb, "fooBoolean");
		assertTrue(b.booleanValue());
		b = (Boolean) BeanUtil.getProperty(fb, "fooboolean");
		assertFalse(b.booleanValue());
		Float f = (Float) BeanUtil.getProperty(fb, "fooFloat");
		assertEquals(109.0, f.floatValue(), 0.005);
		f = (Float) BeanUtil.getProperty(fb, "foofloat");
		assertEquals(110.0, f.floatValue(), 0.005);
		Double d = (Double) BeanUtil.getProperty(fb, "fooDouble");
		assertEquals(111.0, d.doubleValue(), 0.005);
		d = (Double) BeanUtil.getProperty(fb, "foodouble");
		assertEquals(112.0, d.doubleValue(), 0.005);
		String s = (String) BeanUtil.getProperty(fb, "fooString");
		assertEquals("113", s);
		String[] sa = (String[]) BeanUtil.getProperty(fb, "fooStringA");
		assertEquals(2, sa.length);
		assertEquals("114", sa[0]);
		assertEquals("115", sa[1]);
	}


	public void testNested() {
		Cbean cbean = new Cbean();
		String value = "testnest";
		String value2 = "nesttest";
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(cbean, "bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredProperty(cbean, "bbean.abean.fooProp"));
		BeanUtil.setProperty(cbean, "bbean.abean.fooProp", value);
		assertEquals(value, (String) BeanUtil.getProperty(cbean, "bbean.abean.fooProp"));
		Bbean bbean = (Bbean) BeanUtil.getProperty(cbean, "bbean");
		assertTrue(BeanUtil.hasDeclaredProperty(bbean, "abean.fooProp"));
		assertEquals(value, (String) BeanUtil.getProperty(bbean, "abean.fooProp"));
		Abean abean = (Abean) BeanUtil.getProperty(bbean, "abean");
		assertEquals(value, (String) BeanUtil.getProperty(abean, "fooProp"));
		BeanUtil.setProperty(bbean, "abean.fooProp", value2);
		assertEquals(value2, (String) BeanUtil.getProperty(bbean, "abean.fooProp"));
	}

	public void testIster() {
		Abean abean = new Abean();
		Boolean b = (Boolean) BeanUtil.getProperty(abean, "something");
		assertTrue(b.booleanValue());
		try {
			BeanUtil.getProperty(abean, "Something");
			fail();
		} catch (BeanException bex) {
			// ignore
		}
	}

	public void testMap() {
		Cbean cbean = new Cbean();
		Abean abean = cbean.getBbean().getAbean();
		assertNull(BeanUtil.getDeclaredPropertyType(abean, "mval"));
		BeanUtil.setProperty(abean, "mval", new Integer(173));
		BeanUtil.setProperty(abean, "mval2", new Integer(1));
		assertEquals(173, (abean.get("mval")).intValue());
		assertEquals(173, ((Integer) BeanUtil.getProperty(abean, "mval")).intValue());
		assertEquals(1, ((Integer) BeanUtil.getProperty(abean, "mval2")).intValue());
		assertTrue(BeanUtil.hasDeclaredProperty(cbean, "bbean.abean.mval"));
		BeanUtil.setProperty(cbean, "bbean.abean.mval", new Integer(3));
		assertEquals(3, ((Integer) BeanUtil.getProperty(abean, "mval")).intValue());
		assertEquals(3, ((Integer) BeanUtil.getProperty(cbean, "bbean.abean.mval")).intValue());
		HashMap map = new HashMap();
		BeanUtil.setProperty(map, "val1", new Integer(173));
		assertEquals(173, ((Integer)map.get("val1")).intValue());
		Integer i = (Integer) BeanUtil.getProperty(map, "val1");
		assertEquals(173, i.intValue());
	}

	public void testMap2() {
		Map<String, String> m = new HashMap<String, String>();
		m.put("dd.dd", "value");
		m.put("dd", "value2");
		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("map", m);
		FooBean fb = new FooBean();
		fb.setFooMap(m);

		assertEquals(Object.class, BeanUtil.getDeclaredPropertyType(fb, "fooMap[dd]"));
		assertEquals("value2", BeanUtil.getProperty(fb, "fooMap[dd]"));
		assertEquals("value2", BeanUtil.getProperty(m2, "map[dd]"));

		assertEquals("value", BeanUtil.getProperty(fb, "fooMap[dd.dd]"));
		assertEquals(Object.class, BeanUtil.getDeclaredPropertyType(fb, "fooMap[dd.dd]"));
		assertEquals("value", BeanUtil.getProperty(m2, "map[dd.dd]"));
	}

	public void testMap3() {
		Map m = new HashMap();
		BeanUtil.setProperty(m, "Foo", "John");
		assertEquals("John", (String) m.get("Foo"));
		assertNull(m.get("foo"));
		assertFalse(BeanUtil.hasDeclaredProperty(m, "foo"));
		BeanUtil.setProperty(m, "foo", new HashMap());
		assertTrue(BeanUtil.hasDeclaredProperty(m, "foo"));
		assertFalse(BeanUtil.hasDeclaredProperty(m, "foo.Name"));
		BeanUtil.setProperty(m, "foo.Name", "Doe");
		assertEquals("John", (String) m.get("Foo"));
		assertEquals("Doe", ((HashMap) m.get("foo")).get("Name"));
		assertNull("Doe", ((HashMap) m.get("foo")).get("name"));
		assertEquals("John", (String) BeanUtil.getProperty(m, "Foo"));
		assertEquals("Doe", (String) BeanUtil.getProperty(m, "foo.Name"));
		try {
			assertNull(BeanUtil.getProperty(m, "foo.name"));
			fail();
		} catch (Exception e) {
		}
	}

	public void testNotDeclared() {
		FooBean3 fb = new FooBean3();

		try {
			BeanUtil.setProperty(fb, "pprotected", new Integer(1));
			fail();
		} catch(Exception ex) {}
		try {
			BeanUtil.getProperty(fb, "pprotected");
			fail();
		} catch(Exception ex) {}

		try {
			BeanUtil.setProperty(fb, "ppackage", new Integer(2));
			fail();
		} catch(Exception ex) {}
		try {
			BeanUtil.getProperty(fb, "ppackage");
			fail();
		} catch(Exception ex) {}

		try {
			BeanUtil.setProperty(fb, "pprivate", new Integer(3));
			fail();
		} catch(Exception ex) {}
		try {
			BeanUtil.getProperty(fb, "pprivate");
			fail();
		} catch(Exception ex) {}
	}

	public void testDeclared() {
		FooBean3 fb = new FooBean3();

		BeanUtil.setDeclaredProperty(fb, "pprotected", new Integer(1));
		Integer value = (Integer) BeanUtil.getDeclaredProperty(fb, "pprotected");
		assertNotNull(value);
		assertEquals(1, value.intValue());

		BeanUtil.setDeclaredProperty(fb, "ppackage", new Integer(2));
		value = (Integer) BeanUtil.getDeclaredProperty(fb, "ppackage");
		assertNotNull(value);
		assertEquals(2, value.intValue());

		BeanUtil.setDeclaredProperty(fb, "pprivate", new Integer(3));
		value = (Integer) BeanUtil.getDeclaredProperty(fb, "pprivate");
		assertNotNull(value);
		assertEquals(3, value.intValue());
	}


	static class Dummy {
		private FooBean4 fb = new FooBean4();

		public FooBean4 getFb() {
			return fb;
		}

		public void setFb(FooBean4 fb) {
			this.fb = fb;
		}

		/** @noinspection UnnecessaryBoxing*/
		private Integer[] data = new Integer[] {Integer.valueOf("173"), Integer.valueOf("2")};

		public Integer[] getData() {
			return data;
		}
	}

	public void testArrays() {
		FooBean4 fb4 = new FooBean4();
		Dummy dummy = new Dummy();
		assertTrue(BeanUtil.hasDeclaredProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertEquals("xxx", BeanUtil.getProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredProperty(fb4, "data[1].bbean.abean.fooProp"));
		assertEquals("yyy", BeanUtil.getProperty(fb4, "data[1].bbean.abean.fooProp"));

		assertTrue(BeanUtil.hasDeclaredProperty(fb4, "data[2].bbean.abean.fooProp"));
		assertEquals("zzz", BeanUtil.getProperty(fb4, "data[2].bbean.abean.fooProp"));
		BeanUtil.setProperty(fb4, "data[2].bbean.abean.fooProp", "ZZZ");
		assertEquals("ZZZ", BeanUtil.getProperty(fb4, "data[2].bbean.abean.fooProp"));

		assertTrue(BeanUtil.hasDeclaredProperty(fb4, "list[0].bbean.abean.fooProp"));
		assertEquals("LLL", BeanUtil.getProperty(fb4, "list[0].bbean.abean.fooProp"));
		BeanUtil.setProperty(fb4, "list[0].bbean.abean.fooProp", "EEE");
		assertEquals("EEE", BeanUtil.getProperty(fb4, "list[0].bbean.abean.fooProp"));
		assertEquals("lll", BeanUtil.getProperty(fb4, "list[1]"));
		BeanUtil.setProperty(fb4, "list[1]", "eee");

		assertFalse(BeanUtil.hasDeclaredProperty(fb4, "list[1].bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredProperty(fb4, "list[1]"));
		assertEquals("eee", BeanUtil.getProperty(fb4, "list[1]"));
		assertTrue(BeanUtil.hasDeclaredProperty(dummy, "fb.data[0].bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredProperty(dummy, "fb.data[1].bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredProperty(dummy, "fb.data[2].bbean.abean.fooProp"));
		assertEquals("xxx", BeanUtil.getProperty(dummy, "fb.data[0].bbean.abean.fooProp"));
		assertEquals("yyy", BeanUtil.getProperty(dummy, "fb.data[1].bbean.abean.fooProp"));
		assertEquals("zzz", BeanUtil.getProperty(dummy, "fb.data[2].bbean.abean.fooProp"));

		BeanUtil.setProperty(dummy, "fb.data[2].bbean.abean.fooProp", "ZZZ");
		assertEquals("ZZZ", BeanUtil.getProperty(dummy, "fb.data[2].bbean.abean.fooProp"));
		assertEquals(new Integer(173), BeanUtil.getProperty(dummy, "data[0]"));

		BeanUtil.setProperty(dummy, "data[0]", new Integer(-173));
		assertEquals(new Integer(-173), BeanUtil.getProperty(dummy, "data[0]"));
	}


	public void testForced() {
		XBean x = new XBean();
		assertTrue(BeanUtil.hasDeclaredProperty(x, "y"));
		assertFalse(BeanUtil.hasDeclaredProperty(x, "y.foo"));
		assertFalse(BeanUtil.hasDeclaredProperty(x, "y[23].foo"));
		try{
			BeanUtil.setProperty(x, "y.foo", "yyy");
			fail();
		} catch(Exception ex) {}
		assertNull(x.getY());

		BeanUtil.setPropertyForced(x, "y.foo", "yyy");
		assertTrue(BeanUtil.hasDeclaredProperty(x, "y.foo"));
		assertEquals("yyy", x.getY().getFoo());

		assertNotNull(x.getYy());
		assertFalse(BeanUtil.hasDeclaredProperty(x, "yy[2].foo"));
		try {
			BeanUtil.setProperty(x, "yy[2].foo", "yyy");
			fail();
		} catch(Exception ex) {
		}
		assertNull(x.getYy()[2]);

		BeanUtil.setPropertyForced(x, "yy[2].foo", "xxx");
		assertTrue(BeanUtil.hasDeclaredProperty(x, "yy[2].foo"));
		assertEquals("xxx", x.getYy()[2].getFoo());

		assertFalse(BeanUtil.hasDeclaredProperty(x, "yy[20].foo"));
		BeanUtil.setPropertyForced(x, "yy[20].foo", "zzz");
		assertTrue(BeanUtil.hasDeclaredProperty(x, "yy[20].foo"));
		assertEquals("zzz", x.getYy()[20].getFoo());
	}


	public void testSilent() {
		FooBean fb = new FooBean();
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "notexisting"));
		try {
			BeanUtil.setProperty(fb, "notexisting", null);
			fail();
		} catch(Exception ex) {}
		assertFalse(BeanUtil.setPropertySilent(fb, "notexisting", null));
		
	}


	public void testGenerics() {
		Gig gig = new Gig();

		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(gig, "listOfStrings[1]"));
		BeanUtil.setPropertyForced(gig, "listOfStrings[1]", "string");
		assertNull(gig.getListOfStrings().get(0));
		assertEquals("string", gig.getListOfStrings().get(1));
		assertEquals(2, gig.getListOfStrings().size());

		assertEquals(MutableInteger.class, BeanUtil.getDeclaredPropertyType(gig, "listOfIntegers[1]"));
		BeanUtil.setPropertyForced(gig, "listOfIntegers[1]", Integer.valueOf(1));
		assertNull(gig.getListOfIntegers().get(0));
		assertEquals(1, gig.getListOfIntegers().get(1).intValue());
		assertEquals(2, gig.getListOfStrings().size());

		assertEquals(MutableInteger.class, BeanUtil.getDeclaredPropertyType(gig, "listOfIntegers[3]"));
		BeanUtil.setPropertyForced(gig, "listOfIntegers[3]", "3");
		assertNull(gig.getListOfIntegers().get(0));
		assertEquals(1, gig.getListOfIntegers().get(1).intValue());
		assertNull(gig.getListOfIntegers().get(2));
		assertEquals(3, gig.getListOfIntegers().get(3).intValue());
		assertEquals(4, gig.getListOfIntegers().size());

		assertNull(BeanUtil.getDeclaredPropertyType(gig, "listOfAbeans[1].fooProp"));   // [1] doesnt exist yet
		BeanUtil.setPropertyForced(gig, "listOfAbeans[1].fooProp", "xxx");
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(gig, "listOfAbeans[1].fooProp"));
		assertNull(gig.getListOfAbeans().get(0));
		assertEquals("xxx", gig.getListOfAbeans().get(1).getFooProp());
		assertEquals(2, gig.getListOfAbeans().size());

		assertEquals(Integer.class, BeanUtil.getDeclaredPropertyType(gig, "mapOfIntegers[kkk]"));
		BeanUtil.setPropertyForced(gig, "mapOfIntegers[kkk]", "173");
		assertEquals(173, gig.getMapOfIntegers().get("kkk").intValue());
		assertEquals(1, gig.getMapOfIntegers().size());

		assertEquals(Abean.class, BeanUtil.getDeclaredPropertyType(gig, "mapOfAbeans[kkk]"));
		BeanUtil.setPropertyForced(gig, "mapOfAbeans[kkk].fooProp", "zzz");
		assertEquals("zzz", gig.getMapOfAbeans().get("kkk").getFooProp());
		assertEquals(1, gig.getMapOfAbeans().size());

	}

	public void testNoGenerics() {
		Gig gig = new Gig();

		BeanUtil.setPropertyForced(gig, "listOfStrings2[1]", "string");
		assertNull(gig.getListOfStrings2().get(0));
		assertEquals("string", gig.getListOfStrings2().get(1));
		assertEquals(2, gig.getListOfStrings2().size());


		BeanUtil.setPropertyForced(gig, "listOfIntegers2[1]", Integer.valueOf(1));
		assertNull(gig.getListOfIntegers2().get(0));
		assertEquals(1, ((Integer)gig.getListOfIntegers2().get(1)).intValue());
		assertEquals(2, gig.getListOfStrings2().size());

		BeanUtil.setPropertyForced(gig, "listOfIntegers2[3]", "3");
		assertNull(gig.getListOfIntegers2().get(0));
		assertEquals(1, ((Integer)gig.getListOfIntegers2().get(1)).intValue());
		assertNull(gig.getListOfIntegers2().get(2));
		assertEquals("3", gig.getListOfIntegers2().get(3));
		assertEquals(4, gig.getListOfIntegers2().size());

		BeanUtil.setPropertyForced(gig, "listOfAbeans2[1].fooProp", "xxx");
		assertNull(gig.getListOfAbeans2().get(0));
		assertEquals("xxx", ((Map)gig.getListOfAbeans2().get(1)).get("fooProp"));
		assertEquals(2, gig.getListOfAbeans2().size());

		BeanUtil.setPropertyForced(gig, "mapOfIntegers2[kkk]", "173");
		assertEquals("173", gig.getMapOfIntegers2().get("kkk"));
		assertEquals(1, gig.getMapOfIntegers2().size());

		BeanUtil.setPropertyForced(gig, "mapOfAbeans2[kkk].fooProp", "zzz");
		assertEquals("zzz", ((Map)gig.getMapOfAbeans2().get("kkk")).get("fooProp"));
		assertEquals(1, gig.getMapOfAbeans2().size());

	}


	/**
	 * All exceptions.
	 */
	public void testExceptions() {
		Map map = new HashMap();
		Gig gig = new Gig();

		try {
			BeanUtil.getProperty(map, "xxx");
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.getProperty(gig, "doo");
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.setProperty(gig, "xxx", "value");
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.getProperty(gig, "listOfAbeans[1].fooProp");
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.setPropertyForced(gig, "listOfAbeans[xxx].fooProp", "123");
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		try {
			gig.setZoro("zoro");
			BeanUtil.getProperty(gig, "zoro[1].fooProp");
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.setProperty(gig, "zoro[1]", "foo");
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.setPropertySilent(gig, "zoro[1].doo", "foo");
		} catch (Exception e) {
			fail();
			System.out.println(e.getMessage());
		}
	}


	public void testGeneralMapOnly() {
		Map map = new HashMap();
		BeanUtil.setPropertyForced(map, "foo.lll", "value");
		assertNotNull(map.get("foo"));
		assertEquals("value", ((Map) map.get("foo")).get("lll"));

		map = new HashMap();
		BeanUtil.setPropertyForced(map, "foo.lll[2]", "value");
		assertNotNull(map.get("foo"));
		assertNotNull(((Map)map.get("foo")).get("lll"));
		assertEquals("value", ((Map) ((Map) map.get("foo")).get("lll")).get("2"));
	}


	public void testInheritance() {
		ZBean zb = new ZBean();

		assertEquals("public", BeanUtil.getProperty(zb, "ppublic"));
		try {
			assertEquals("protected", BeanUtil.getProperty(zb, "pprotected"));
			fail();
		} catch (BeanException bex) {
		}

		assertEquals("protected", BeanUtil.getDeclaredProperty(zb, "pprotected"));
		try {
			assertEquals("private", BeanUtil.getDeclaredProperty(zb, "pprivate"));
			fail();
		} catch (BeanException bex) {
		}

		// top

		assertEquals("public", BeanUtil.getProperty(zb, "tpublic"));
		try {
			assertEquals("protected", BeanUtil.getProperty(zb, "tprotected"));
			fail();
		} catch (BeanException bex) {
		}

		assertEquals("protected", BeanUtil.getDeclaredProperty(zb, "tprotected"));
		assertEquals("private", BeanUtil.getDeclaredProperty(zb, "tprivate"));
	}


	public void testSimpleThis() {
		FooBean fb = new FooBean();
		BeanUtil.setSimpleProperty(fb, "fooString", "test", false);
		assertEquals("test", fb.getFooString());
		assertEquals("test", BeanUtil.getSimpleProperty(fb, "fooString", false));
		assertEquals("test", BeanUtil.getProperty(fb, "*this.fooString"));

		FooBean4 fb4 = new FooBean4();
		assertEquals("xxx", BeanUtil.getProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertEquals("xxx", BeanUtil.getProperty(fb4, "*this.data.*this[0].*this.bbean.abean.fooProp"));


		assertEquals("foo", BeanUtil.extractThisReference("foo.aaa"));
		assertEquals("foo", BeanUtil.extractThisReference("foo[1].aaa"));
		assertEquals("foo", BeanUtil.extractThisReference("foo"));
	}

	public void testIsGetBoolean() {
		IsGetBool i = new IsGetBool();
		Object value = BeanUtil.getProperty(i, "flag");
		assertNotNull(value);
		assertTrue(((Boolean) value).booleanValue());

		ClassDescriptor cd = ClassIntrospector.lookup(IsGetBool.class);
		assertEquals(1, cd.getAllBeanGetterNames().length);
		assertEquals(1, cd.getAllBeanGetters().length);
		assertEquals("isFlag", cd.getAllBeanGetters()[0].getName());
		assertEquals("isFlag", cd.getBeanGetter("flag").getName());
		assertEquals(1, cd.getAllBeanSetterNames().length);
		assertEquals(1, cd.getAllBeanSetters().length);
		assertEquals(3, cd.getAllMethods().length);

		GetIsBool i2 = new GetIsBool();
		value = BeanUtil.getProperty(i2, "flag");
		assertNotNull(value);
		assertTrue(((Boolean) value).booleanValue());

		cd = ClassIntrospector.lookup(GetIsBool.class);
		assertEquals(1, cd.getAllBeanGetterNames().length);
		assertEquals(1, cd.getAllBeanGetters().length);
		assertEquals("isFlag", cd.getAllBeanGetters()[0].getName());
		assertEquals("isFlag", cd.getBeanGetter("flag").getName());
		assertEquals(1, cd.getAllBeanSetterNames().length);
		assertEquals(1, cd.getAllBeanSetters().length);
		assertEquals(3, cd.getAllMethods().length);
	}

}