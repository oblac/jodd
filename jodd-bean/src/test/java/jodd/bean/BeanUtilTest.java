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

package jodd.bean;

import jodd.bean.data.*;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.mutable.MutableInteger;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

public class BeanUtilTest {

	@Test
	public void testSimpleProperty() {
		FooBean fb = new FooBean();

		BeanUtilBean beanUtilBean = new BeanUtilBean();

		// read non initialized property (null)
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooInteger", false));
		assertTrue(beanUtilBean.hasProperty(fb, "fooInteger"));
		assertEquals(Integer.class, beanUtilBean.getPropertyType(fb, "fooInteger"));

		// set property
		beanUtilBean.setSimpleProperty(fb, "fooInteger", new Integer(173), false);
		// read initialized property
		assertEquals(new Integer(173), beanUtilBean.getSimpleProperty(fb, "fooInteger", false));

		// read non-initialized simple property (zero)
		assertEquals(new Integer(0), beanUtilBean.getSimpleProperty(fb, "fooint", false));
		assertTrue(beanUtilBean.hasProperty(fb, "fooint"));
		assertEquals(int.class, beanUtilBean.getPropertyType(fb, "fooint"));
		assertFalse(beanUtilBean.hasProperty(fb, "fooint-xxx"));
		assertNull(beanUtilBean.getPropertyType(fb, "fooint-xxx"));

		// read forced non-initialized property (not null)
		assertTrue(beanUtilBean.hasProperty(fb, "fooByte"));
		assertEquals(Byte.class, beanUtilBean.getPropertyType(fb, "fooByte"));
		assertEquals(new Byte((byte) 0), beanUtilBean.getSimplePropertyForced(fb, "fooByte", false));

		Map m = new HashMap();
		// set property in map
		beanUtilBean.setSimpleProperty(m, "foo", new Integer(173), false);
		// read property from map
		assertTrue(beanUtilBean.hasProperty(m, "foo"));
		assertEquals(new Integer(173), beanUtilBean.getSimpleProperty(m, "foo", false));

		// read non-initialized map property
		assertTrue(beanUtilBean.hasProperty(fb, "fooMap"));
		assertEquals(Map.class, beanUtilBean.getPropertyType(fb, "fooMap"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooMap", false));
		// read forced non-initialized map property
		assertNotNull(beanUtilBean.getSimplePropertyForced(fb, "fooMap", false));

		// read non-initialized list property
		assertTrue(beanUtilBean.hasProperty(fb, "fooList"));
		assertEquals(List.class, beanUtilBean.getPropertyType(fb, "fooList"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooList", false));
		// read forced non-initialized list property
		assertNotNull(beanUtilBean.getSimplePropertyForced(fb, "fooList", false));

		// read non-initialized array (null)
		assertTrue(beanUtilBean.hasProperty(fb, "fooStringA"));
		assertEquals(String[].class, beanUtilBean.getPropertyType(fb, "fooStringA"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooStringA", false));
		String[] tmp = new String[10];
		tmp[2] = "foo";
		// set array property
		beanUtilBean.setSimpleProperty(fb, "fooStringA", tmp, false);
		// read array property
		tmp = (String[]) beanUtilBean.getSimpleProperty(fb, "fooStringA", false);
		assertEquals("foo", tmp[2]);

		fb.setFooStringA(null);
		// read non-initialized array property
		assertTrue(beanUtilBean.hasProperty(fb, "fooStringA"));
		assertEquals(String[].class, beanUtilBean.getPropertyType(fb, "fooStringA"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooStringA", false));
		// read forced non-initialized array property
		assertNotNull(beanUtilBean.getSimplePropertyForced(fb, "fooStringA", false));
	}

	@Test
	public void testSimplePropertySlimPrivate() {
		FooBeanSlim fb = new FooBeanSlim();

		BeanUtilBean beanUtilBean = new BeanUtilBean();

		// read non initialized property (null)
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooInteger"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooInteger", true));
		assertNull(beanUtilBean.getPropertyType(fb, "fooInteger"));
		assertEquals(Integer.class, beanUtilBean.getDeclaredPropertyType(fb, "fooInteger"));

		// set property
		beanUtilBean.setSimpleProperty(fb, "fooInteger", new Integer(173), true);
		// read initialized property
		assertEquals(new Integer(173), beanUtilBean.getSimpleProperty(fb, "fooInteger", true));

		// read non-initialized simple property (zero)
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooint"));
		assertEquals(new Integer(0), beanUtilBean.getSimpleProperty(fb, "fooint", true));

		// read forced non-initialized property (not null)
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooByte"));
		assertEquals(new Byte((byte) 0), beanUtilBean.getSimplePropertyForced(fb, "fooByte", true));

		Map m = new HashMap();
		// set property in map
		assertFalse(beanUtilBean.hasDeclaredProperty(m, "foo"));
		beanUtilBean.setSimpleProperty(m, "foo", new Integer(173), true);
		// read property from map
		assertTrue(beanUtilBean.hasDeclaredProperty(m, "foo"));
		assertEquals(new Integer(173), beanUtilBean.getSimpleProperty(m, "foo", true));

		// read non-initialized map property
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooMap"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooMap", true));
		// read forced non-initialized map property
		assertNotNull(beanUtilBean.getSimplePropertyForced(fb, "fooMap", true));

		// read non-initialized list property
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooList"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooList", true));
		// read forced non-initialized list property
		assertNotNull(beanUtilBean.getSimplePropertyForced(fb, "fooList", true));

		// read non-initialized array (null)
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooStringA"));
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooStringA", true));
		String[] tmp = new String[10];
		tmp[2] = "foo";
		// set array property
		beanUtilBean.setSimpleProperty(fb, "fooStringA", tmp, true);
		// read array property
		tmp = (String[]) beanUtilBean.getSimpleProperty(fb, "fooStringA", true);
		assertEquals("foo", tmp[2]);

		fb = new FooBeanSlim();
		// read non-initialized array property
		assertNull(beanUtilBean.getSimpleProperty(fb, "fooStringA", true));
		// read forced non-initialized array property
		assertNotNull(beanUtilBean.getSimplePropertyForced(fb, "fooStringA", true));
	}

	@Test
	public void testIndexProperty() {
		FooBean fb = new FooBean();

		BeanUtilBean beanUtilBean = new BeanUtilBean();

		// read forced non-initialized array property
		assertNull(fb.getFooStringA());
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooStringA[0]"));
		assertEquals(String.class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA[0]"));
		try {
			beanUtilBean.getIndexProperty(fb, "fooStringA[0]", false, true);
			fail();
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}
		assertNotNull(fb.getFooStringA());
		assertEquals(0, fb.getFooStringA().length);

		// set array property (non-forced)
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooStringA[7]"));
		assertEquals(String.class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA[7]"));
		try {
			beanUtilBean.setIndexProperty(fb, "fooStringA[7]", "xxx", false, false);
			fail();
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}

		// set forced array property
		beanUtilBean.setIndexProperty(fb, "fooStringA[40]", "zzz", false, true);
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooStringA[40]"));
		assertEquals(String.class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA[40]"));
		assertEquals(String[].class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA"));
		assertEquals(String.class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA[40]"));
		assertEquals("zzz", fb.getFooStringA()[40]);
		assertEquals(41, fb.getFooStringA().length);

		// set null
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooStringA[43]"));
		assertEquals(String.class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA[43]"));
		beanUtilBean.setIndexProperty(fb, "fooStringA[43]", null, false, true);
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooStringA[43]"));
		assertEquals(String.class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA[43]"));
		assertNull(fb.getFooStringA()[43]);
		assertEquals(44, fb.getFooStringA().length);

		// get forced
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooStringA[15]"));
		assertNotNull(beanUtilBean.getIndexProperty(fb, "fooStringA[15]", false, true));
		assertNull(fb.getFooStringA()[0]);
		assertNotNull(fb.getFooStringA()[15]);


		// set uninitialized array property
		fb.setFooStringA(null);
		assertEquals(String.class, beanUtilBean.getDeclaredPropertyType(fb, "fooStringA[43]"));
		beanUtilBean.setIndexProperty(fb, "fooStringA[7]", "ccc", false, true);
		assertEquals("ccc", fb.getFooStringA()[7]);


		// read forced non-initialized list property
		assertNull(fb.getFooList());
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooList[1]"));
		assertEquals(Object.class, beanUtilBean.getDeclaredPropertyType(fb, "fooList[1]"));
		try {
			beanUtilBean.getIndexProperty(fb, "fooList[1]", false, true);
			fail();
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}
		assertNotNull(fb.getFooList());

		// set list property (non-forced)
		try {
			beanUtilBean.setIndexProperty(fb, "fooList[1]", "xxx", false, false);
			fail();
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}

		// set forced list property
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooList[40]"));
		beanUtilBean.setIndexProperty(fb, "fooList[40]", "zzz", false, true);
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooList[40]"));
		assertEquals(Object.class, beanUtilBean.getDeclaredPropertyType(fb, "fooList[40]"));        // method type, not values type
		assertEquals(Object.class, beanUtilBean.getDeclaredPropertyType(fb, "fooList[39]"));
		assertEquals("zzz", fb.getFooList().get(40));
		assertEquals(41, fb.getFooList().size());

		// set forced unitialized list property
		fb.setFooList(null);
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooList[1]"));
		beanUtilBean.setIndexProperty(fb, "fooList[1]", "xxx", false, true);
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooList[1]"));
		assertEquals("xxx", fb.getFooList().get(1));
		assertEquals(2, fb.getFooList().size());


		// read forced non-initialized map property
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooMap[foo]"));
		assertEquals(Object.class, beanUtilBean.getDeclaredPropertyType(fb, "fooMap[foo]"));
		assertNull(beanUtilBean.getIndexProperty(fb, "fooMap[foo]", false, true));
		assertNotNull(fb.getFooMap());
		// set non-initialized map property
		fb.setFooMap(null);
		assertFalse(beanUtilBean.hasDeclaredProperty(fb, "fooMap[foo]"));
		beanUtilBean.setIndexProperty(fb, "fooMap[foo]", "xxx", false, true);
		assertTrue(beanUtilBean.hasDeclaredProperty(fb, "fooMap[foo]"));
		assertEquals("xxx", fb.getFooMap().get("foo"));
		assertEquals(1, fb.getFooMap().size());
	}

	@Test
	public void testIndexPropertySlimPrivate() {
		FooBeanSlim fb = new FooBeanSlim();

		BeanUtilBean beanUtilBean = new BeanUtilBean();

		// read forced non-initialized array property
		assertNull(fb.getStringA());
		try {
			beanUtilBean.getIndexProperty(fb, "fooStringA[0]", true, true);
			fail();
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}
		assertNotNull(fb.getStringA());
		assertEquals(0, fb.getStringA().length);

		// set array property (non-forced)
		try {
			beanUtilBean.setIndexProperty(fb, "fooStringA[7]", "xxx", true, false);
			fail();
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}

		// set forced array property
		beanUtilBean.setIndexProperty(fb, "fooStringA[40]", "zzz", true, true);
		assertEquals("zzz", fb.getStringA()[40]);
		assertEquals(41, fb.getStringA().length);

		beanUtilBean.setIndexProperty(fb, "fooStringA[43]", null, true, true);
		assertNull(fb.getStringA()[43]);
		assertEquals(44, fb.getStringA().length);


		// set uninitialized array property
		fb = new FooBeanSlim();
		assertNull(fb.getStringA());
		beanUtilBean.setIndexProperty(fb, "fooStringA[7]", "ccc", true, true);
		assertNotNull(fb.getStringA());
		assertEquals("ccc", fb.getStringA()[7]);


		// read forced non-initialized list property
		assertNull(fb.getList());
		try {
			beanUtilBean.getIndexProperty(fb, "fooList[1]", true, true);
			fail();
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}
		assertNotNull(fb.getList());

		// set list property (non-forced)
		try {
			beanUtilBean.setIndexProperty(fb, "fooList[1]", "xxx", true, false);
			fail();
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}

		// set forced list property
		beanUtilBean.setIndexProperty(fb, "fooList[40]", "zzz", true, true);
		assertEquals("zzz", fb.getList().get(40));
		assertEquals(41, fb.getList().size());

		// set forced unitialized list property
		fb = new FooBeanSlim();
		beanUtilBean.setIndexProperty(fb, "fooList[1]", "xxx", true, true);
		assertEquals("xxx", fb.getList().get(1));

		// read forced non-initialized map property
		assertNull(fb.getMap());
		assertNull(beanUtilBean.getIndexProperty(fb, "fooMap[foo]", true, true));
		assertNotNull(fb.getMap());

		// set non-initialized map property
		fb = new FooBeanSlim();
		assertNull(fb.getMap());
		beanUtilBean.setIndexProperty(fb, "fooMap[foo]", "xxx", true, true);
		assertNotNull(fb.getMap());
		assertEquals("xxx", fb.getMap().get("foo"));
	}


	// ---------------------------------------------------------------- types

	@Test
	public void testSetPropertyNumbers() {
		FooBean fb = new FooBean();

		// Integer
		String propName = "fooInteger";
		BeanUtil.setProperty(fb, propName, new Integer(1));
		assertEquals(1, fb.getFooInteger().intValue());
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooInteger());
		BeanUtil.setProperty(fb, propName, "2");            // valid string
		assertEquals(2, fb.getFooInteger().intValue());
		try {
			BeanUtil.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2, fb.getFooInteger().intValue());

		// int
		propName = "fooint";
		BeanUtil.setProperty(fb, propName, new Integer(1));
		assertEquals(1, fb.getFooint());

		try {
			BeanUtil.setProperty(fb, propName, null);     // null is not an int
			fail();
		} catch (Exception ex) {
		}
		assertEquals(1, fb.getFooint());

		BeanUtil.setProperty(fb, propName, "2");
		assertEquals(2, fb.getFooint());

		try {
			BeanUtil.setProperty(fb, propName, "w");    // invalid string
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2, fb.getFooint());


		// Long
		propName = "fooLong";
		BeanUtil.setProperty(fb, propName, new Long(1));
		assertEquals(1L, fb.getFooLong().longValue());
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3L, fb.getFooLong().longValue());
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooLong());
		BeanUtil.setProperty(fb, propName, "2");            // valid string
		assertEquals(2L, fb.getFooLong().longValue());

		try {
			BeanUtil.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2L, fb.getFooLong().longValue());

		// long
		propName = "foolong";
		BeanUtil.setProperty(fb, propName, new Long(1));
		assertEquals(1L, fb.getFoolong());
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3L, fb.getFoolong());

		try {
			BeanUtil.setProperty(fb, propName, null);             // null is not a long
			fail();
		} catch (Exception ex) {
		}

		assertEquals(3L, fb.getFoolong());
		BeanUtil.setProperty(fb, propName, "2");            // valid string
		assertEquals(2L, fb.getFoolong());
		try {
			BeanUtil.setProperty(fb, propName, "w");        // invalid string
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2L, fb.getFoolong());

		// Byte
		propName = "fooByte";
		BeanUtil.setProperty(fb, propName, new Byte((byte) 1));
		assertEquals(1, fb.getFooByte().byteValue());
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3, fb.getFooByte().byteValue());
		BeanUtil.setProperty(fb, propName, new Integer(257));
		assertEquals(1, fb.getFooByte().byteValue());                // lower byte of 257
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooByte());
		BeanUtil.setProperty(fb, propName, "2");                    // valid string
		assertEquals(2, fb.getFooByte().byteValue());

		try {
			BeanUtil.setProperty(fb, propName, "x");            // invalid string - value stays the same
			fail();
		} catch (Exception ex) {
		}
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
			BeanUtil.setProperty(fb, propName, null);        // null is not a byte
			fail();
		} catch (Exception ex) {
		}
		assertEquals(1, fb.getFoobyte());
		BeanUtil.setProperty(fb, propName, "2");            // valid string
		assertEquals(2, fb.getFoobyte());
		try {
			BeanUtil.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2, fb.getFoobyte());

		// Boolean
		propName = "fooBoolean";
		BeanUtil.setProperty(fb, propName, Boolean.TRUE);
		assertTrue(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, Boolean.FALSE);
		assertFalse(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "yes");
		assertTrue(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "y");
		assertTrue(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "true");
		assertTrue(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "on");
		assertTrue(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "1");
		assertTrue(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "no");
		assertFalse(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "n");
		assertFalse(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "false");
		assertFalse(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "off");
		assertFalse(fb.getFooBoolean());
		BeanUtil.setProperty(fb, propName, "0");
		assertFalse(fb.getFooBoolean());

		// boolean
		propName = "fooboolean";
		BeanUtil.setProperty(fb, propName, Boolean.TRUE);
		assertTrue(fb.getFooboolean());
		BeanUtil.setProperty(fb, propName, Boolean.FALSE);
		assertFalse(fb.getFooboolean());

		try {
			BeanUtil.setProperty(fb, propName, null);
			fail();
		} catch (Exception ex) {
		}

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
		assertEquals(1.1, fb.getFooFloat(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFooFloat(), 0.0005);
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooFloat());
		BeanUtil.setProperty(fb, propName, "2.2");            // valid string
		assertEquals(2.2, fb.getFooFloat(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2.2, fb.getFooFloat(), 0.0005);

		// float
		propName = "foofloat";
		BeanUtil.setProperty(fb, propName, new Float(1.1));
		assertEquals(1.1, fb.getFoofloat(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFoofloat(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, null);            // null is not a long
			fail();
		} catch (Exception ex) {
		}
		assertEquals(3.0, fb.getFoofloat(), 0.0005);
		BeanUtil.setProperty(fb, propName, "2.2");                // valid string

		assertEquals(2.2, fb.getFoofloat(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "w");            // invalid string
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2.2, fb.getFoofloat(), 0.0005);

		// Double
		propName = "fooDouble";
		BeanUtil.setProperty(fb, propName, new Double(1.1));
		assertEquals(1.1, fb.getFooDouble(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFooDouble(), 0.0005);
		BeanUtil.setProperty(fb, propName, null);
		assertNull(fb.getFooDouble());
		BeanUtil.setProperty(fb, propName, "2.2");            // valid string
		assertEquals(2.2, fb.getFooDouble(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2.2, fb.getFooDouble(), 0.0005);

		// double
		propName = "foodouble";
		BeanUtil.setProperty(fb, propName, new Double(1.1));
		assertEquals(1.1, fb.getFoodouble(), 0.0005);
		BeanUtil.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFoodouble(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, null);        // null is not a long
			fail();
		} catch (Exception ex) {
		}
		assertEquals(3.0, fb.getFoodouble(), 0.0005);
		BeanUtil.setProperty(fb, propName, "2.2");                // valid string

		assertEquals(2.2, fb.getFoodouble(), 0.0005);
		try {
			BeanUtil.setProperty(fb, propName, "w");                    // invalid string
			fail();
		} catch (Exception ex) {
		}
		assertEquals(2.2, fb.getFoodouble(), 0.0005);
	}


	@Test
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


	@Test
	public void testSetPropertyMath() {
		FooBean2 fb = new FooBean2();
		String propName = "fooBigDecimal";
		assertEquals(BigDecimal.class, BeanUtil.getPropertyType(fb, propName));
		BeanUtil.setProperty(fb, propName, new BigDecimal("1.2"));
		assertEquals(1.2, fb.getFooBigDecimal().doubleValue(), 0.0005);
	}

	@Test
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
		String[] sa = new String[]{"one", "two", "three"};
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

	@Test
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
		fb.setFoofloat((float) 110.0);
		fb.setFooDouble(new Double(111.0));
		fb.setFoodouble(112.0);
		fb.setFooString("113");
		fb.setFooStringA(new String[]{"114", "115"});

		Integer v = (Integer) BeanUtil.getProperty(fb, "fooInteger");
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
		assertTrue(b);
		b = (Boolean) BeanUtil.getProperty(fb, "fooboolean");
		assertFalse(b);
		Float f = (Float) BeanUtil.getProperty(fb, "fooFloat");
		assertEquals(109.0, f, 0.005);
		f = (Float) BeanUtil.getProperty(fb, "foofloat");
		assertEquals(110.0, f, 0.005);
		Double d = (Double) BeanUtil.getProperty(fb, "fooDouble");
		assertEquals(111.0, d, 0.005);
		d = (Double) BeanUtil.getProperty(fb, "foodouble");
		assertEquals(112.0, d, 0.005);
		String s = (String) BeanUtil.getProperty(fb, "fooString");
		assertEquals("113", s);
		String[] sa = (String[]) BeanUtil.getProperty(fb, "fooStringA");
		assertEquals(2, sa.length);
		assertEquals("114", sa[0]);
		assertEquals("115", sa[1]);
	}


	@Test
	public void testNested() {
		Cbean cbean = new Cbean();
		String value = "testnest";
		String value2 = "nesttest";
		assertEquals(String.class, BeanUtil.getDeclaredPropertyType(cbean, "bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredProperty(cbean, "bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(cbean, "bbean.abean.fooProp"));
		BeanUtil.setProperty(cbean, "bbean.abean.fooProp", value);
		assertEquals(value, BeanUtil.getProperty(cbean, "bbean.abean.fooProp"));
		Bbean bbean = (Bbean) BeanUtil.getProperty(cbean, "bbean");
		assertTrue(BeanUtil.hasDeclaredProperty(bbean, "abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(bbean, "abean.fooProp"));
		assertEquals(value, BeanUtil.getProperty(bbean, "abean.fooProp"));
		Abean abean = (Abean) BeanUtil.getProperty(bbean, "abean");
		assertEquals(value,  BeanUtil.getProperty(abean, "fooProp"));
		BeanUtil.setProperty(bbean, "abean.fooProp", value2);
		assertEquals(value2, BeanUtil.getProperty(bbean, "abean.fooProp"));
	}

	@Test
	public void testIster() {
		Abean abean = new Abean();
		Boolean b = (Boolean) BeanUtil.getProperty(abean, "something");
		assertTrue(b);
		try {
			BeanUtil.getProperty(abean, "Something");
			fail();
		} catch (BeanException bex) {
			// ignore
		}
	}

	@Test
	public void testMap() {
		Cbean cbean = new Cbean();
		Abean abean = cbean.getBbean().getAbean();
		assertNull(BeanUtil.getDeclaredPropertyType(abean, "mval"));
		BeanUtil.setProperty(abean, "mval", new Integer(173));
		BeanUtil.setProperty(abean, "mval2", new Integer(1));
		assertEquals((abean.get("mval")).intValue(), 173);
		assertEquals(173, ((Integer) BeanUtil.getProperty(abean, "mval")).intValue());
		assertEquals(1, ((Integer) BeanUtil.getProperty(abean, "mval2")).intValue());
		assertTrue(BeanUtil.hasDeclaredProperty(cbean, "bbean.abean.mval"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(cbean, "bbean.abean.mval"));
		BeanUtil.setProperty(cbean, "bbean.abean.mval", new Integer(3));
		assertEquals(3, ((Integer) BeanUtil.getProperty(abean, "mval")).intValue());
		assertEquals(3, ((Integer) BeanUtil.getProperty(cbean, "bbean.abean.mval")).intValue());
		HashMap map = new HashMap();
		BeanUtil.setProperty(map, "val1", new Integer(173));
		assertEquals(173, ((Integer) map.get("val1")).intValue());
		Integer i = (Integer) BeanUtil.getProperty(map, "val1");
		assertEquals(173, i.intValue());
	}

	@Test
	public void testMap2() {
		Map<String, String> m = new HashMap<>();
		m.put("dd.dd", "value");
		m.put("dd", "value2");
		Map<String, Object> m2 = new HashMap<>();
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

	@Test
	public void testMap3() {
		Map m = new HashMap();
		BeanUtil.setProperty(m, "Foo", "John");
		assertEquals("John", (String) m.get("Foo"));
		assertNull(m.get("foo"));
		assertFalse(BeanUtil.hasDeclaredProperty(m, "foo"));
		assertFalse(BeanUtil.hasDeclaredRootProperty(m, "foo"));
		BeanUtil.setProperty(m, "foo", new HashMap());
		assertTrue(BeanUtil.hasDeclaredProperty(m, "foo"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(m, "foo"));
		assertFalse(BeanUtil.hasDeclaredProperty(m, "foo.Name"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(m, "foo.Name"));
		BeanUtil.setProperty(m, "foo.Name", "Doe");
		assertEquals("John", m.get("Foo"));
		assertEquals("Doe", ((HashMap) m.get("foo")).get("Name"));
		assertNull("Doe", ((HashMap) m.get("foo")).get("name"));
		assertEquals("John", BeanUtil.getProperty(m, "Foo"));
		assertEquals("Doe", BeanUtil.getProperty(m, "foo.Name"));
		try {
			assertNull(BeanUtil.getProperty(m, "foo.name"));
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testNotDeclared() {
		FooBean3 fb = new FooBean3();

		try {
			BeanUtil.setProperty(fb, "pprotected", new Integer(1));
			fail();
		} catch (Exception ex) {
		}
		try {
			BeanUtil.getProperty(fb, "pprotected");
			fail();
		} catch (Exception ex) {
		}

		try {
			BeanUtil.setProperty(fb, "ppackage", new Integer(2));
			fail();
		} catch (Exception ex) {
		}
		try {
			BeanUtil.getProperty(fb, "ppackage");
			fail();
		} catch (Exception ex) {
		}

		try {
			BeanUtil.setProperty(fb, "pprivate", new Integer(3));
			fail();
		} catch (Exception ex) {
		}
		try {
			BeanUtil.getProperty(fb, "pprivate");
			fail();
		} catch (Exception ex) {
		}
	}

	@Test
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

		/**
		 * @noinspection UnnecessaryBoxing
		 */
		private Integer[] data = new Integer[]{Integer.valueOf("173"), Integer.valueOf("2")};

		public Integer[] getData() {
			return data;
		}
	}

	@Test
	public void testArrays() {
		FooBean4 fb4 = new FooBean4();
		Dummy dummy = new Dummy();
		assertTrue(BeanUtil.hasDeclaredProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertEquals("xxx", BeanUtil.getProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredProperty(fb4, "data[1].bbean.abean.fooProp"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(fb4, "data[1].bbean.abean.fooProp"));
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
		assertTrue(BeanUtil.hasDeclaredRootProperty(fb4, "list[1].bbean.abean.fooProp"));
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


	@Test
	public void testForced() {
		XBean x = new XBean();
		assertTrue(BeanUtil.hasDeclaredProperty(x, "y"));
		assertFalse(BeanUtil.hasDeclaredProperty(x, "y.foo"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(x, "y.foo"));
		assertFalse(BeanUtil.hasDeclaredProperty(x, "y[23].foo"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(x, "y[23].foo"));
		try {
			BeanUtil.setProperty(x, "y.foo", "yyy");
			fail();
		} catch (Exception ex) {
		}
		assertNull(x.getY());

		BeanUtil.setPropertyForced(x, "y.foo", "yyy");
		assertTrue(BeanUtil.hasDeclaredProperty(x, "y.foo"));
		assertEquals("yyy", x.getY().getFoo());

		assertNotNull(x.getYy());
		assertFalse(BeanUtil.hasDeclaredProperty(x, "yy[2].foo"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(x, "yy[2].foo"));
		try {
			BeanUtil.setProperty(x, "yy[2].foo", "yyy");
			fail();
		} catch (Exception ex) {
		}
		assertNull(x.getYy()[2]);

		BeanUtil.setPropertyForced(x, "yy[2].foo", "xxx");
		assertTrue(BeanUtil.hasDeclaredProperty(x, "yy[2].foo"));
		assertEquals("xxx", x.getYy()[2].getFoo());

		assertFalse(BeanUtil.hasDeclaredProperty(x, "yy[20].foo"));
		assertTrue(BeanUtil.hasDeclaredRootProperty(x, "yy[20].foo"));
		BeanUtil.setPropertyForced(x, "yy[20].foo", "zzz");
		assertTrue(BeanUtil.hasDeclaredProperty(x, "yy[20].foo"));
		assertEquals("zzz", x.getYy()[20].getFoo());
	}


	@Test
	public void testSilent() {
		FooBean fb = new FooBean();
		assertFalse(BeanUtil.hasDeclaredProperty(fb, "notexisting"));
		assertFalse(BeanUtil.hasDeclaredRootProperty(fb, "notexisting"));
		try {
			BeanUtil.setProperty(fb, "notexisting", null);
			fail();
		} catch (Exception ex) {
		}

		try {
			BeanUtil.setPropertySilent(fb, "notexisting", null);
		} catch (Exception ex) {
			fail();
		}
	}


	@Test
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

	@Test
	public void testNoGenerics() {
		Gig gig = new Gig();

		BeanUtil.setPropertyForced(gig, "listOfStrings2[1]", "string");
		assertNull(gig.getListOfStrings2().get(0));
		assertEquals("string", gig.getListOfStrings2().get(1));
		assertEquals(2, gig.getListOfStrings2().size());


		BeanUtil.setPropertyForced(gig, "listOfIntegers2[1]", Integer.valueOf(1));
		assertNull(gig.getListOfIntegers2().get(0));
		assertEquals(1, ((Integer) gig.getListOfIntegers2().get(1)).intValue());
		assertEquals(2, gig.getListOfStrings2().size());

		BeanUtil.setPropertyForced(gig, "listOfIntegers2[3]", "3");
		assertNull(gig.getListOfIntegers2().get(0));
		assertEquals(1, ((Integer) gig.getListOfIntegers2().get(1)).intValue());
		assertNull(gig.getListOfIntegers2().get(2));
		assertEquals("3", gig.getListOfIntegers2().get(3));
		assertEquals(4, gig.getListOfIntegers2().size());

		BeanUtil.setPropertyForced(gig, "listOfAbeans2[1].fooProp", "xxx");
		assertNull(gig.getListOfAbeans2().get(0));
		assertEquals("xxx", ((Map) gig.getListOfAbeans2().get(1)).get("fooProp"));
		assertEquals(2, gig.getListOfAbeans2().size());

		BeanUtil.setPropertyForced(gig, "mapOfIntegers2[kkk]", "173");
		assertEquals("173", gig.getMapOfIntegers2().get("kkk"));
		assertEquals(1, gig.getMapOfIntegers2().size());

		BeanUtil.setPropertyForced(gig, "mapOfAbeans2[kkk].fooProp", "zzz");
		assertEquals("zzz", ((Map) gig.getMapOfAbeans2().get("kkk")).get("fooProp"));
		assertEquals(1, gig.getMapOfAbeans2().size());

	}


	/**
	 * All exceptions.
	 */
	@Test
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


	@Test
	public void testGeneralMapOnly() {
		Map map = new HashMap();
		BeanUtil.setPropertyForced(map, "foo.lll", "value");
		assertNotNull(map.get("foo"));
		assertEquals("value", ((Map) map.get("foo")).get("lll"));

		map = new HashMap();
		BeanUtil.setPropertyForced(map, "foo.lll[2]", "value");
		assertNotNull(map.get("foo"));
		assertNotNull(((Map) map.get("foo")).get("lll"));
		assertEquals("value", ((Map) ((Map) map.get("foo")).get("lll")).get("2"));
	}


	@Test
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


	@Test
	public void testSimpleThis() {
		FooBean fb = new FooBean();
		BeanUtilBean beanUtilBean = new BeanUtilBean();

		beanUtilBean.setSimpleProperty(fb, "fooString", "test", false);
		assertEquals("test", fb.getFooString());
		assertEquals("test", beanUtilBean.getSimpleProperty(fb, "fooString", false));
		assertEquals("test", beanUtilBean.getProperty(fb, "fooString"));

		FooBean4 fb4 = new FooBean4();
		assertEquals("xxx", beanUtilBean.getProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertEquals("xxx", beanUtilBean.getProperty(fb4, "data.[0].bbean.abean.fooProp"));


		assertEquals("foo", beanUtilBean.extractThisReference("foo.aaa"));
		assertEquals("foo", beanUtilBean.extractThisReference("foo[1].aaa"));
		assertEquals("foo", beanUtilBean.extractThisReference("foo"));
	}

	@Test
	public void testIsGetBoolean() {
		IsGetBool i = new IsGetBool();
		Object value = BeanUtil.getProperty(i, "flag");
		assertNotNull(value);
		assertTrue((Boolean) value);

		ClassDescriptor cd = ClassIntrospector.lookup(IsGetBool.class);

		PropertyDescriptor[] propertyDescriptors = cd.getAllPropertyDescriptors();

		assertEquals(1, propertyDescriptors.length);
		assertEquals("flag", propertyDescriptors[0].getName());
		assertEquals("isFlag", propertyDescriptors[0].getReadMethodDescriptor().getMethod().getName());
		MethodDescriptor[] mds = cd.getAllMethodDescriptors();
		int c = 0;
		for (MethodDescriptor md : mds) {
			if (md.isPublic()) c++;
		}
		assertEquals(3, c);

		GetIsBool i2 = new GetIsBool();
		value = BeanUtil.getProperty(i2, "flag");
		assertNotNull(value);
		assertTrue((Boolean) value);

		cd = ClassIntrospector.lookup(GetIsBool.class);
		assertEquals("flag", propertyDescriptors[0].getName());
		assertEquals("isFlag", propertyDescriptors[0].getReadMethodDescriptor().getMethod().getName());
		mds = cd.getAllMethodDescriptors();
		c = 0;
		for (MethodDescriptor md : mds) {
			if (md.isPublic()) c++;
		}
		assertEquals(3, c);
	}

	@Test
	public void testUppercase() {
		UppercaseBean ub = new UppercaseBean();
		try {
			BeanUtil.getProperty(ub, "URLaddress");
		} catch (Exception ex) {
			fail();
		}
	}

	@Test
	public void testPropertiesWithDot() {
		Properties props = new Properties();
		BeanUtil.setProperty(props, "ldap", "data");

		assertEquals("data", props.getProperty("ldap"));

		BeanUtil.setProperty(props, "[ldap.auth.enabled]", "data2");

		assertEquals("data", props.getProperty("ldap"));
		assertEquals("data2", props.getProperty("ldap.auth.enabled"));


		Map map = new HashMap();
		FooBean fb = new FooBean();

		BeanUtil.setProperty(map, "[aaa.bbb]", fb);
		BeanUtil.setPropertyForced(map, "[aaa.bbb].fooMap[xxx.ccc]", "zzzz");
		assertEquals("zzzz", ((FooBean) map.get("aaa.bbb")).getFooMap().get("xxx.ccc"));

		BeanUtil.setPropertyForced(fb, "fooint", "123");
		assertEquals(123, fb.getFooint());

		try {
			BeanUtil.setProperty(map, ".[aaa.bbb]", "zzzz");
			fail();
		} catch (Exception ex) {
		}

		try {
			BeanUtil.setPropertyForced(fb, "..fooint", "123");
			fail();
		} catch (Exception ex) {
		}

		BeanUtil.setPropertyForced(map, "[aaa.bbb].fooMap.[eee.ccc]", "zzzz");
		// forced works because *this is a map!
		assertEquals("zzzz", BeanUtil.getProperty(map, "[aaa.bbb].fooMap.[eee.ccc]"));
	}

	@Test
	public void testEnums() {
		EnumBean enumBean = new EnumBean();

		BeanUtil.setProperty(enumBean, "id", Integer.valueOf(123));
		BeanUtil.setProperty(enumBean, "color", "RED");
		BeanUtil.setProperty(enumBean, "status", "STARTED");

		assertEquals(123, enumBean.getId());
		assertEquals(Color.RED, enumBean.getColor());
		assertEquals(Status.STARTED, enumBean.getStatus());
	}

	@Test
	public void testSubSup1() {
		SupBean supBean = new SupBean();
		//BeanUtil.setProperty(supBean, "v1", "V1");
		String v = (String) BeanUtil.getProperty(supBean, "v1");

		assertEquals("v1sup", v);

		supBean = new SubBean();
		BeanUtil.setProperty(supBean, "v1", "V1");
		v = (String) BeanUtil.getProperty(supBean, "v1");

		assertEquals("V1sup", v);
	}

	@Test
	public void testSubSup2() {
		SupBean supBean = new SubBean();
		BeanUtil.setProperty(supBean, "v2", "V2");
		//String v = (String) BeanUtil.getProperty(supBean, "v2");

		String v = (String) BeanUtil.getProperty(supBean, "v2");

		assertEquals("V2sub", v);
	}

	@Test
	public void testCollections() {
		MixBean mixBean = new MixBean();
		BeanUtil.setProperty(mixBean, "data", "1,2,3");

		assertNotNull(mixBean.data);
		assertEquals(3, mixBean.data.size());
		assertEquals(1, mixBean.data.get(0).intValue());

		BeanUtil.setProperty(mixBean, "data2", "1,2,3,4");

		assertNotNull(mixBean.getData2());
		assertEquals(4, mixBean.getData2().size());
		assertEquals(1, mixBean.getData2().get(0).intValue());

		BeanUtil.setProperty(mixBean, "data5", "1,2,3,4,5");

		assertNotNull(mixBean.getData5());
		assertEquals(5, mixBean.getData5().size());
		assertEquals(1, mixBean.getData5().get(0).intValue());
	}

	@Test
	public void testMapWithKeyWithADot() {
		Map innerMap = new HashMap();
		innerMap.put("zzz.xxx", "hey");

		Map map = new HashMap();
		map.put("foo.bar", innerMap);

		Object value = BeanUtil.getProperty(map, "[foo.bar]");
		assertNotNull(value);

		value = BeanUtil.getProperty(map, "[foo.bar].[zzz.xxx]");
		assertNotNull(value);
		assertEquals("hey", value.toString());

	}

}