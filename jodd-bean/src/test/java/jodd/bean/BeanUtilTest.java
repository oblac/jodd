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

import jodd.bean.fixtures.*;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.mutable.MutableInteger;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnnecessaryBoxing")
public class BeanUtilTest {

	@Test
	public void testSimpleProperty() {
		FooBean fb = new FooBean();

		// read non initialized property (null)
		assertNull(BeanUtil.pojo.getSimpleProperty(fb, "fooInteger"));
		assertTrue(BeanUtil.pojo.hasProperty(fb, "fooInteger"));
		assertEquals(Integer.class, BeanUtil.pojo.getPropertyType(fb, "fooInteger"));

		// set property
		BeanUtil.pojo.setSimpleProperty(fb, "fooInteger", new Integer(173));
		// read initialized property
		assertEquals(new Integer(173), BeanUtil.pojo.getSimpleProperty(fb, "fooInteger"));

		// read non-initialized simple property (zero)
		assertEquals(new Integer(0), BeanUtil.pojo.getSimpleProperty(fb, "fooint"));
		assertTrue(BeanUtil.pojo.hasProperty(fb, "fooint"));
		assertEquals(int.class, BeanUtil.pojo.getPropertyType(fb, "fooint"));
		assertFalse(BeanUtil.pojo.hasProperty(fb, "fooint-xxx"));
		assertNull(BeanUtil.pojo.getPropertyType(fb, "fooint-xxx"));

		// read forced non-initialized property (not null)
		assertTrue(BeanUtil.pojo.hasProperty(fb, "fooByte"));
		assertEquals(Byte.class, BeanUtil.pojo.getPropertyType(fb, "fooByte"));
		assertEquals(new Byte((byte) 0), BeanUtil.forced.getSimpleProperty(fb, "fooByte"));

		Map m = new HashMap();
		// set property in map
		BeanUtil.pojo.setSimpleProperty(m, "foo", new Integer(173));
		// read property from map
		assertTrue(BeanUtil.pojo.hasProperty(m, "foo"));
		assertEquals(new Integer(173), BeanUtil.pojo.getSimpleProperty(m, "foo"));

		// read non-initialized map property
		assertTrue(BeanUtil.pojo.hasProperty(fb, "fooMap"));
		assertEquals(Map.class, BeanUtil.pojo.getPropertyType(fb, "fooMap"));
		assertNull(BeanUtil.pojo.getSimpleProperty(fb, "fooMap"));
		// read forced non-initialized map property
		assertNotNull(BeanUtil.forced.getSimpleProperty(fb, "fooMap"));

		// read non-initialized list property
		assertTrue(BeanUtil.pojo.hasProperty(fb, "fooList"));
		assertEquals(List.class, BeanUtil.pojo.getPropertyType(fb, "fooList"));
		assertNull(BeanUtil.pojo.getSimpleProperty(fb, "fooList"));
		// read forced non-initialized list property
		assertNotNull(BeanUtil.forced.getSimpleProperty(fb, "fooList"));

		// read non-initialized array (null)
		assertTrue(BeanUtil.pojo.hasProperty(fb, "fooStringA"));
		assertEquals(String[].class, BeanUtil.pojo.getPropertyType(fb, "fooStringA"));
		assertNull(BeanUtil.pojo.getSimpleProperty(fb, "fooStringA"));
		String[] tmp = new String[10];
		tmp[2] = "foo";
		// set array property
		BeanUtil.pojo.setSimpleProperty(fb, "fooStringA", tmp);
		// read array property
		tmp = BeanUtil.pojo.getSimpleProperty(fb, "fooStringA");
		assertEquals("foo", tmp[2]);

		fb.setFooStringA(null);
		// read non-initialized array property
		assertTrue(BeanUtil.pojo.hasProperty(fb, "fooStringA"));
		assertEquals(String[].class, BeanUtil.pojo.getPropertyType(fb, "fooStringA"));
		assertNull(BeanUtil.pojo.getSimpleProperty(fb, "fooStringA"));
		// read forced non-initialized array property
		assertNotNull(BeanUtil.forced.getSimpleProperty(fb, "fooStringA"));
	}

	@Test
	public void testSimplePropertySlimPrivate() {
		FooBeanSlim fb = new FooBeanSlim();

		// read non initialized property (null)
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooInteger"));
		assertNull(BeanUtil.declared.getSimpleProperty(fb, "fooInteger"));
		assertNull(BeanUtil.pojo.getPropertyType(fb, "fooInteger"));
		assertEquals(Integer.class, BeanUtil.declared.getPropertyType(fb, "fooInteger"));

		// set property
		BeanUtil.declared.setSimpleProperty(fb, "fooInteger", new Integer(173));
		// read initialized property
		assertEquals(new Integer(173), BeanUtil.declared.getSimpleProperty(fb, "fooInteger"));

		// read non-initialized simple property (zero)
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooint"));
		assertEquals(new Integer(0), BeanUtil.declared.getSimpleProperty(fb, "fooint"));

		// read forced non-initialized property (not null)
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooByte"));
		assertEquals(new Byte((byte) 0), BeanUtil.declaredForced.getSimpleProperty(fb, "fooByte"));

		Map m = new HashMap();
		// set property in map
		assertFalse(BeanUtil.declared.hasProperty(m, "foo"));
		BeanUtil.declared.setSimpleProperty(m, "foo", new Integer(173));
		// read property from map
		assertTrue(BeanUtil.declared.hasProperty(m, "foo"));
		assertEquals(new Integer(173), BeanUtil.declared.getSimpleProperty(m, "foo"));

		// read non-initialized map property
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooMap"));
		assertNull(BeanUtil.declared.getSimpleProperty(fb, "fooMap"));
		// read forced non-initialized map property
		assertNotNull(BeanUtil.declaredForced.getSimpleProperty(fb, "fooMap"));

		// read non-initialized list property
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooList"));
		assertNull(BeanUtil.declared.getSimpleProperty(fb, "fooList"));
		// read forced non-initialized list property
		assertNotNull(BeanUtil.declaredForced.getSimpleProperty(fb, "fooList"));

		// read non-initialized array (null)
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooStringA"));
		assertNull(BeanUtil.declared.getSimpleProperty(fb, "fooStringA"));
		String[] tmp = new String[10];
		tmp[2] = "foo";
		// set array property
		BeanUtil.declared.setSimpleProperty(fb, "fooStringA", tmp);
		// read array property
		tmp = BeanUtil.declared.getSimpleProperty(fb, "fooStringA");
		assertEquals("foo", tmp[2]);

		fb = new FooBeanSlim();
		// read non-initialized array property
		assertNull(BeanUtil.declared.getSimpleProperty(fb, "fooStringA"));
		// read forced non-initialized array property
		assertNotNull(BeanUtil.declaredForced.getSimpleProperty(fb, "fooStringA"));
	}

	@Test
	public void testIndexProperty() {
		FooBean fb = new FooBean();

		// read forced non-initialized array property
		assertNull(fb.getFooStringA());
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooStringA[0]"));
		assertEquals(String.class, BeanUtil.declared.getPropertyType(fb, "fooStringA[0]"));
		try {
			BeanUtil.forced.getIndexProperty(fb, "fooStringA", 0);
			fail("error");
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}
		assertNotNull(fb.getFooStringA());
		assertEquals(0, fb.getFooStringA().length);

		// set array property (non-forced)
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooStringA[7]"));
		assertEquals(String.class, BeanUtil.declared.getPropertyType(fb, "fooStringA[7]"));
		try {
			BeanUtil.pojo.setIndexProperty(fb, "fooStringA", 7, "xxx");
			fail("error");
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}

		// set forced array property
		BeanUtil.forced.setIndexProperty(fb, "fooStringA", 40, "zzz");
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooStringA[40]"));
		assertEquals(String.class, BeanUtil.declared.getPropertyType(fb, "fooStringA[40]"));
		assertEquals(String[].class, BeanUtil.declared.getPropertyType(fb, "fooStringA"));
		assertEquals(String.class, BeanUtil.declared.getPropertyType(fb, "fooStringA[40]"));
		assertEquals("zzz", fb.getFooStringA()[40]);
		assertEquals(41, fb.getFooStringA().length);

		// set null
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooStringA[43]"));
		assertEquals(String.class, BeanUtil.declared.getPropertyType(fb, "fooStringA[43]"));
		BeanUtil.forced.setIndexProperty(fb, "fooStringA", 43, null);
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooStringA[43]"));
		assertEquals(String.class, BeanUtil.declared.getPropertyType(fb, "fooStringA[43]"));
		assertNull(fb.getFooStringA()[43]);
		assertEquals(44, fb.getFooStringA().length);

		// get forced
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooStringA[15]"));
		assertNotNull(BeanUtil.forced.getIndexProperty(fb, "fooStringA", 15));
		assertNull(fb.getFooStringA()[0]);
		assertNotNull(fb.getFooStringA()[15]);


		// set uninitialized array property
		fb.setFooStringA(null);
		assertEquals(String.class, BeanUtil.declared.getPropertyType(fb, "fooStringA[43]"));
		BeanUtil.forced.setIndexProperty(fb, "fooStringA", 7, "ccc");
		assertEquals("ccc", fb.getFooStringA()[7]);


		// read forced non-initialized list property
		assertNull(fb.getFooList());
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooList[1]"));
		assertEquals(Object.class, BeanUtil.declared.getPropertyType(fb, "fooList[1]"));
		try {
			BeanUtil.forced.getIndexProperty(fb, "fooList", 1);
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}
		assertNotNull(fb.getFooList());

		// set list property (non-forced)
		try {
			BeanUtil.pojo.setIndexProperty(fb, "fooList", 1, "xxx");
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}

		// set forced list property
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooList[40]"));
		BeanUtil.forced.setIndexProperty(fb, "fooList", 40, "zzz");
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooList[40]"));
		assertEquals(Object.class, BeanUtil.declared.getPropertyType(fb, "fooList[40]"));        // method type, not values type
		assertEquals(Object.class, BeanUtil.declared.getPropertyType(fb, "fooList[39]"));
		assertEquals("zzz", fb.getFooList().get(40));
		assertEquals(41, fb.getFooList().size());

		// set forced unitialized list property
		fb.setFooList(null);
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooList[1]"));
		BeanUtil.forced.setIndexProperty(fb, "fooList", 1, "xxx");
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooList[1]"));
		assertEquals("xxx", fb.getFooList().get(1));
		assertEquals(2, fb.getFooList().size());


		// read forced non-initialized map property
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooMap[foo]"));
		assertEquals(Object.class, BeanUtil.declared.getPropertyType(fb, "fooMap[foo]"));
		assertNull(BeanUtil.forced.getProperty(fb, "fooMap[foo]"));
		assertNotNull(fb.getFooMap());
		// set non-initialized map property
		fb.setFooMap(null);
		assertFalse(BeanUtil.declared.hasProperty(fb, "fooMap[foo]"));
		BeanUtil.forced.setProperty(fb, "fooMap[foo]", "xxx");
		assertTrue(BeanUtil.declared.hasProperty(fb, "fooMap[foo]"));
		assertEquals("xxx", fb.getFooMap().get("foo"));
		assertEquals(1, fb.getFooMap().size());
	}

	@Test
	public void testIndexPropertySlimPrivate() {
		FooBeanSlim fb = new FooBeanSlim();

		// read forced non-initialized array property
		assertNull(fb.getStringA());
		try {
			BeanUtil.declaredForced.getIndexProperty(fb, "fooStringA", 0);
			fail("error");
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}
		assertNotNull(fb.getStringA());
		assertEquals(0, fb.getStringA().length);

		// set array property (non-forced)
		try {
			BeanUtil.declared.setIndexProperty(fb, "fooStringA", 7, "xxx");
			fail("error");
		} catch (ArrayIndexOutOfBoundsException aioobex) {
			// ignore
		}

		// set forced array property
		BeanUtil.declaredForced.setIndexProperty(fb, "fooStringA", 40, "zzz");
		assertEquals("zzz", fb.getStringA()[40]);
		assertEquals(41, fb.getStringA().length);

		BeanUtil.declaredForced.setIndexProperty(fb, "fooStringA", 43, null);
		assertNull(fb.getStringA()[43]);
		assertEquals(44, fb.getStringA().length);


		// set uninitialized array property
		fb = new FooBeanSlim();
		assertNull(fb.getStringA());
		BeanUtil.declaredForced.setIndexProperty(fb, "fooStringA", 7, "ccc");
		assertNotNull(fb.getStringA());
		assertEquals("ccc", fb.getStringA()[7]);


		// read forced non-initialized list property
		assertNull(fb.getList());
		try {
			BeanUtil.declaredForced.getIndexProperty(fb, "fooList", 1);
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}
		assertNotNull(fb.getList());

		// set list property (non-forced)
		try {
			BeanUtil.declared.setIndexProperty(fb, "fooList", 1, "xxx");
			fail("error");
		} catch (IndexOutOfBoundsException ioobex) {
			// ignore
		}

		// set forced list property
		BeanUtil.declaredForced.setIndexProperty(fb, "fooList", 40, "zzz");
		assertEquals("zzz", fb.getList().get(40));
		assertEquals(41, fb.getList().size());

		// set forced unitialized list property
		fb = new FooBeanSlim();
		BeanUtil.declaredForced.setIndexProperty(fb, "fooList", 1, "xxx");
		assertEquals("xxx", fb.getList().get(1));

		// read forced non-initialized map property
		assertNull(fb.getMap());
		assertNull(BeanUtil.declaredForced.getProperty(fb, "fooMap[foo]"));
		assertNotNull(fb.getMap());

		// set non-initialized map property
		fb = new FooBeanSlim();
		assertNull(fb.getMap());
		BeanUtil.declaredForced.setProperty(fb, "fooMap[foo]", "xxx");
		assertNotNull(fb.getMap());
		assertEquals("xxx", fb.getMap().get("foo"));
	}


	// ---------------------------------------------------------------- types

	@Test
	public void testSetPropertyNumbers() {
		FooBean fb = new FooBean();

		// Integer
		String propName = "fooInteger";
		BeanUtil.pojo.setProperty(fb, propName, new Integer(1));
		assertEquals(1, fb.getFooInteger().intValue());
		BeanUtil.pojo.setProperty(fb, propName, null);
		assertNull(fb.getFooInteger());
		BeanUtil.pojo.setProperty(fb, propName, "2");            // valid string
		assertEquals(2, fb.getFooInteger().intValue());
		try {
			BeanUtil.pojo.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2, fb.getFooInteger().intValue());

		// int
		propName = "fooint";
		BeanUtil.pojo.setProperty(fb, propName, new Integer(1));
		assertEquals(1, fb.getFooint());

		try {
			BeanUtil.pojo.setProperty(fb, propName, null);     // null is not an int
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(1, fb.getFooint());

		BeanUtil.pojo.setProperty(fb, propName, "2");
		assertEquals(2, fb.getFooint());

		try {
			BeanUtil.pojo.setProperty(fb, propName, "w");    // invalid string
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2, fb.getFooint());


		// Long
		propName = "fooLong";
		BeanUtil.pojo.setProperty(fb, propName, new Long(1));
		assertEquals(1L, fb.getFooLong().longValue());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3L, fb.getFooLong().longValue());
		BeanUtil.pojo.setProperty(fb, propName, null);
		assertNull(fb.getFooLong());
		BeanUtil.pojo.setProperty(fb, propName, "2");            // valid string
		assertEquals(2L, fb.getFooLong().longValue());

		try {
			BeanUtil.pojo.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2L, fb.getFooLong().longValue());

		// long
		propName = "foolong";
		BeanUtil.pojo.setProperty(fb, propName, new Long(1));
		assertEquals(1L, fb.getFoolong());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3L, fb.getFoolong());

		try {
			BeanUtil.pojo.setProperty(fb, propName, null);             // null is not a long
			fail("error");
		} catch (Exception ignored) {
		}

		assertEquals(3L, fb.getFoolong());
		BeanUtil.pojo.setProperty(fb, propName, "2");            // valid string
		assertEquals(2L, fb.getFoolong());
		try {
			BeanUtil.pojo.setProperty(fb, propName, "w");        // invalid string
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2L, fb.getFoolong());

		// Byte
		propName = "fooByte";
		BeanUtil.pojo.setProperty(fb, propName, new Byte((byte) 1));
		assertEquals(1, fb.getFooByte().byteValue());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3, fb.getFooByte().byteValue());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(257));
		assertEquals(1, fb.getFooByte().byteValue());                // lower byte of 257
		BeanUtil.pojo.setProperty(fb, propName, null);
		assertNull(fb.getFooByte());
		BeanUtil.pojo.setProperty(fb, propName, "2");                    // valid string
		assertEquals(2, fb.getFooByte().byteValue());

		try {
			BeanUtil.pojo.setProperty(fb, propName, "x");            // invalid string - value stays the same
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2, fb.getFooByte().byteValue());

		// byte
		propName = "foobyte";
		BeanUtil.pojo.setProperty(fb, propName, new Byte((byte) 1));
		assertEquals(1, fb.getFoobyte());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3, fb.getFoobyte());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(257));
		assertEquals(1, fb.getFoobyte());
		try {
			BeanUtil.pojo.setProperty(fb, propName, null);        // null is not a byte
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(1, fb.getFoobyte());
		BeanUtil.pojo.setProperty(fb, propName, "2");            // valid string
		assertEquals(2, fb.getFoobyte());
		try {
			BeanUtil.pojo.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2, fb.getFoobyte());

		// Boolean
		propName = "fooBoolean";
		BeanUtil.pojo.setProperty(fb, propName, Boolean.TRUE);
		assertTrue(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, Boolean.FALSE);
		assertFalse(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, null);
		assertNull(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "yes");
		assertTrue(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "y");
		assertTrue(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "true");
		assertTrue(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "on");
		assertTrue(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "1");
		assertTrue(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "no");
		assertFalse(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "n");
		assertFalse(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "false");
		assertFalse(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "off");
		assertFalse(fb.getFooBoolean());
		BeanUtil.pojo.setProperty(fb, propName, "0");
		assertFalse(fb.getFooBoolean());

		// boolean
		propName = "fooboolean";
		BeanUtil.pojo.setProperty(fb, propName, Boolean.TRUE);
		assertTrue(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, Boolean.FALSE);
		assertFalse(fb.getFooboolean());

		try {
			BeanUtil.pojo.setProperty(fb, propName, null);
			fail("error");
		} catch (Exception ignored) {
		}

		assertFalse(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "yes");
		assertTrue(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "y");
		assertTrue(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "true");
		assertTrue(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "on");
		assertTrue(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "1");
		assertTrue(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "no");
		assertFalse(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "n");
		assertFalse(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "false");
		assertFalse(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "off");
		assertFalse(fb.getFooboolean());
		BeanUtil.pojo.setProperty(fb, propName, "0");
		assertFalse(fb.getFooboolean());

		// Float
		propName = "fooFloat";
		BeanUtil.pojo.setProperty(fb, propName, new Float(1.1));
		assertEquals(1.1, fb.getFooFloat(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFooFloat(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, null);
		assertNull(fb.getFooFloat());
		BeanUtil.pojo.setProperty(fb, propName, "2.2");            // valid string
		assertEquals(2.2, fb.getFooFloat(), 0.0005);
		try {
			BeanUtil.pojo.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2.2, fb.getFooFloat(), 0.0005);

		// float
		propName = "foofloat";
		BeanUtil.pojo.setProperty(fb, propName, new Float(1.1));
		assertEquals(1.1, fb.getFoofloat(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFoofloat(), 0.0005);
		try {
			BeanUtil.pojo.setProperty(fb, propName, null);            // null is not a long
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(3.0, fb.getFoofloat(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, "2.2");                // valid string

		assertEquals(2.2, fb.getFoofloat(), 0.0005);
		try {
			BeanUtil.pojo.setProperty(fb, propName, "w");            // invalid string
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2.2, fb.getFoofloat(), 0.0005);

		// Double
		propName = "fooDouble";
		BeanUtil.pojo.setProperty(fb, propName, new Double(1.1));
		assertEquals(1.1, fb.getFooDouble(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFooDouble(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, null);
		assertNull(fb.getFooDouble());
		BeanUtil.pojo.setProperty(fb, propName, "2.2");            // valid string
		assertEquals(2.2, fb.getFooDouble(), 0.0005);
		try {
			BeanUtil.pojo.setProperty(fb, propName, "x");        // invalid string - value stays the same
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2.2, fb.getFooDouble(), 0.0005);

		// double
		propName = "foodouble";
		BeanUtil.pojo.setProperty(fb, propName, new Double(1.1));
		assertEquals(1.1, fb.getFoodouble(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, new Integer(3));
		assertEquals(3.0, fb.getFoodouble(), 0.0005);
		try {
			BeanUtil.pojo.setProperty(fb, propName, null);        // null is not a long
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(3.0, fb.getFoodouble(), 0.0005);
		BeanUtil.pojo.setProperty(fb, propName, "2.2");                // valid string

		assertEquals(2.2, fb.getFoodouble(), 0.0005);
		try {
			BeanUtil.pojo.setProperty(fb, propName, "w");                    // invalid string
			fail("error");
		} catch (Exception ignored) {
		}
		assertEquals(2.2, fb.getFoodouble(), 0.0005);
	}


	@Test
	public void testSetPropertySql() {
		FooBean2 fb = new FooBean2();

		String propName = "fooTimestamp";
		//noinspection deprecation
		Timestamp ts = new Timestamp(101, 0, 17, 1, 2, 3, 4);
		assertEquals(Timestamp.class, BeanUtil.pojo.getPropertyType(fb, propName));
		BeanUtil.pojo.setProperty(fb, propName, ts);
		assertEquals("2001-01-17 01:02:03.000000004", fb.getFooTimestamp().toString());

		propName = "fooTime";
		//noinspection deprecation
		Time t = new Time(17, 13, 15);
		BeanUtil.pojo.setProperty(fb, propName, t);

		assertEquals("17:13:15", fb.getFooTime().toString());

		propName = "fooDate";
		//noinspection deprecation
		Date d = new Date(101, 1, 17);
		assertEquals(Date.class, BeanUtil.pojo.getPropertyType(fb, propName));
		BeanUtil.pojo.setProperty(fb, propName, d);
		assertEquals("2001-02-17", fb.getFooDate().toString());
	}


	@Test
	public void testSetPropertyMath() {
		FooBean2 fb = new FooBean2();
		String propName = "fooBigDecimal";
		assertEquals(BigDecimal.class, BeanUtil.pojo.getPropertyType(fb, propName));
		BeanUtil.pojo.setProperty(fb, propName, new BigDecimal("1.2"));
		assertEquals(1.2, fb.getFooBigDecimal().doubleValue(), 0.0005);
	}

	@Test
	public void testSetPropertyString() {
		FooBean fb = new FooBean();

		// String
		String propName = "fooString";
		BeanUtil.pojo.setProperty(fb, propName, "string");
		assertEquals("string", fb.getFooString());
		BeanUtil.pojo.setProperty(fb, propName, null);
		assertNull(fb.getFooString());

		// String array
		propName = "fooStringA";
		String[] sa = new String[]{"one", "two", "three"};
		BeanUtil.pojo.setProperty(fb, propName, sa);
		assertEquals("one", fb.getFooStringA()[0]);
		assertEquals("two", fb.getFooStringA()[1]);
		assertEquals("three", fb.getFooStringA()[2]);
		BeanUtil.pojo.setProperty(fb, propName, "just a string");
		sa = BeanUtil.pojo.getProperty(fb, propName);
		assertEquals(1, sa.length);
		assertEquals("just a string", sa[0]);

		// Character
		propName = "fooCharacter";
		BeanUtil.pojo.setProperty(fb, propName, new Character('a'));
		assertEquals('a', fb.getFooCharacter().charValue());
		BeanUtil.pojo.setProperty(fb, propName, "1");
		assertEquals('1', fb.getFooCharacter().charValue());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(789));
		assertEquals(789, fb.getFooCharacter().charValue());

		// char
		propName = "foochar";
		BeanUtil.pojo.setProperty(fb, propName, new Character('a'));
		assertEquals('a', fb.getFoochar());
		BeanUtil.pojo.setProperty(fb, propName, "1");
		assertEquals('1', fb.getFoochar());
		BeanUtil.pojo.setProperty(fb, propName, new Integer(789));
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

		Integer v = BeanUtil.pojo.getProperty(fb, "fooInteger");
		assertEquals(101, v.intValue());
		v = BeanUtil.pojo.getProperty(fb, "fooint");
		assertEquals(102, v.intValue());
		Long vl = BeanUtil.pojo.getProperty(fb, "fooLong");
		assertEquals(103, vl.longValue());
		vl = BeanUtil.pojo.getProperty(fb, "foolong");
		assertEquals(104, vl.longValue());
		Byte vb = BeanUtil.pojo.getProperty(fb, "fooByte");
		assertEquals(105, vb.intValue());
		vb = BeanUtil.pojo.getProperty(fb, "foobyte");
		assertEquals(106, vb.intValue());
		Character c = BeanUtil.pojo.getProperty(fb, "fooCharacter");
		assertEquals('7', c.charValue());
		c = BeanUtil.pojo.getProperty(fb, "foochar");
		assertEquals('8', c.charValue());
		Boolean b = BeanUtil.pojo.getProperty(fb, "fooBoolean");
		assertTrue(b);
		b = BeanUtil.pojo.getProperty(fb, "fooboolean");
		assertFalse(b);
		Float f = BeanUtil.pojo.getProperty(fb, "fooFloat");
		assertEquals(109.0, f, 0.005);
		f = BeanUtil.pojo.getProperty(fb, "foofloat");
		assertEquals(110.0, f, 0.005);
		Double d = BeanUtil.pojo.getProperty(fb, "fooDouble");
		assertEquals(111.0, d, 0.005);
		d = BeanUtil.pojo.getProperty(fb, "foodouble");
		assertEquals(112.0, d, 0.005);
		String s = BeanUtil.pojo.getProperty(fb, "fooString");
		assertEquals("113", s);
		String[] sa = BeanUtil.pojo.getProperty(fb, "fooStringA");
		assertEquals(2, sa.length);
		assertEquals("114", sa[0]);
		assertEquals("115", sa[1]);
	}


	@Test
	public void testNested() {
		Cbean cbean = new Cbean();
		String value = "testnest";
		String value2 = "nesttest";
		assertEquals(String.class, BeanUtil.declared.getPropertyType(cbean, "bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasProperty(cbean, "bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasRootProperty(cbean, "bbean.abean.fooProp"));
		BeanUtil.pojo.setProperty(cbean, "bbean.abean.fooProp", value);
		assertEquals(value, BeanUtil.pojo.getProperty(cbean, "bbean.abean.fooProp"));
		Bbean bbean = BeanUtil.pojo.getProperty(cbean, "bbean");
		assertTrue(BeanUtil.declared.hasProperty(bbean, "abean.fooProp"));
		assertTrue(BeanUtil.declared.hasRootProperty(bbean, "abean.fooProp"));
		assertEquals(value, BeanUtil.pojo.getProperty(bbean, "abean.fooProp"));
		Abean abean = BeanUtil.pojo.getProperty(bbean, "abean");
		assertEquals(value,  BeanUtil.pojo.getProperty(abean, "fooProp"));
		BeanUtil.pojo.setProperty(bbean, "abean.fooProp", value2);
		assertEquals(value2, BeanUtil.pojo.getProperty(bbean, "abean.fooProp"));
	}

	@Test
	public void testIster() {
		Abean abean = new Abean();
		Boolean b = BeanUtil.pojo.getProperty(abean, "something");
		assertTrue(b);
		try {
			BeanUtil.pojo.getProperty(abean, "Something");
			fail("error");
		} catch (BeanException bex) {
			// ignore
		}
	}

	@Test
	public void testMap() {
		Cbean cbean = new Cbean();
		Abean abean = cbean.getBbean().getAbean();
		assertNull(BeanUtil.declared.getPropertyType(abean, "mval"));
		BeanUtil.pojo.setProperty(abean, "mval", new Integer(173));
		BeanUtil.pojo.setProperty(abean, "mval2", new Integer(1));
		assertEquals((abean.get("mval")).intValue(), 173);
		assertEquals(173, ((Integer) BeanUtil.pojo.getProperty(abean, "mval")).intValue());
		assertEquals(1, ((Integer) BeanUtil.pojo.getProperty(abean, "mval2")).intValue());
		assertTrue(BeanUtil.declared.hasProperty(cbean, "bbean.abean.mval"));
		assertTrue(BeanUtil.declared.hasRootProperty(cbean, "bbean.abean.mval"));
		BeanUtil.pojo.setProperty(cbean, "bbean.abean.mval", new Integer(3));
		assertEquals(3, ((Integer) BeanUtil.pojo.getProperty(abean, "mval")).intValue());
		assertEquals(3, ((Integer) BeanUtil.pojo.getProperty(cbean, "bbean.abean.mval")).intValue());
		HashMap map = new HashMap();
		BeanUtil.pojo.setProperty(map, "val1", new Integer(173));
		assertEquals(173, ((Integer) map.get("val1")).intValue());
		Integer i = BeanUtil.pojo.getProperty(map, "val1");
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

		assertEquals(Object.class, BeanUtil.declared.getPropertyType(fb, "fooMap[dd]"));
		assertEquals("value2", BeanUtil.pojo.getProperty(fb, "fooMap[dd]"));
		assertEquals("value2", BeanUtil.pojo.getProperty(m2, "map[dd]"));

		assertEquals("value", BeanUtil.pojo.getProperty(fb, "fooMap[dd.dd]"));
		assertEquals(Object.class, BeanUtil.declared.getPropertyType(fb, "fooMap[dd.dd]"));
		assertEquals("value", BeanUtil.pojo.getProperty(m2, "map[dd.dd]"));
	}

	@Test
	public void testMap3() {
		Map m = new HashMap();
		BeanUtil.pojo.setProperty(m, "Foo", "John");
		assertEquals("John", m.get("Foo"));
		assertNull(m.get("foo"));
		assertFalse(BeanUtil.declared.hasProperty(m, "foo"));
		assertFalse(BeanUtil.declared.hasRootProperty(m, "foo"));
		BeanUtil.pojo.setProperty(m, "foo", new HashMap());
		assertTrue(BeanUtil.declared.hasProperty(m, "foo"));
		assertTrue(BeanUtil.declared.hasRootProperty(m, "foo"));
		assertFalse(BeanUtil.declared.hasProperty(m, "foo.Name"));
		assertTrue(BeanUtil.declared.hasRootProperty(m, "foo.Name"));
		BeanUtil.pojo.setProperty(m, "foo.Name", "Doe");
		assertEquals("John", m.get("Foo"));
		assertEquals("Doe", ((HashMap) m.get("foo")).get("Name"));
		assertNull(((HashMap) m.get("foo")).get("name"), "Doe not null");
		assertEquals("John", BeanUtil.pojo.getProperty(m, "Foo"));
		assertEquals("Doe", BeanUtil.pojo.getProperty(m, "foo.Name"));
		try {
			assertNull(BeanUtil.pojo.getProperty(m, "foo.name"));
			fail("error");
		} catch (Exception ignored) {
		}
	}

	@Test
	public void testNotDeclared() {
		FooBean3 fb = new FooBean3();

		try {
			BeanUtil.pojo.setProperty(fb, "pprotected", new Integer(1));
			fail("error");
		} catch (Exception ignored) {
		}
		try {
			BeanUtil.pojo.getProperty(fb, "pprotected");
			fail("error");
		} catch (Exception ignored) {
		}

		try {
			BeanUtil.pojo.setProperty(fb, "ppackage", new Integer(2));
			fail("error");
		} catch (Exception ignored) {
		}
		try {
			BeanUtil.pojo.getProperty(fb, "ppackage");
			fail("error");
		} catch (Exception ignored) {
		}

		try {
			BeanUtil.pojo.setProperty(fb, "pprivate", new Integer(3));
			fail("error");
		} catch (Exception ignored) {
		}
		try {
			BeanUtil.pojo.getProperty(fb, "pprivate");
			fail("error");
		} catch (Exception ignored) {
		}
	}

	@Test
	public void testDeclared() {
		FooBean3 fb = new FooBean3();

		BeanUtil.declared.setProperty(fb, "pprotected", new Integer(1));
		Integer value = BeanUtil.declared.getProperty(fb, "pprotected");
		assertNotNull(value);
		assertEquals(1, value.intValue());

		BeanUtil.declared.setProperty(fb, "ppackage", new Integer(2));
		value = BeanUtil.declared.getProperty(fb, "ppackage");
		assertNotNull(value);
		assertEquals(2, value.intValue());

		BeanUtil.declared.setProperty(fb, "pprivate", new Integer(3));
		value = BeanUtil.declared.getProperty(fb, "pprivate");
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
		assertTrue(BeanUtil.declared.hasProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasRootProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertEquals("xxx", BeanUtil.pojo.getProperty(fb4, "data[0].bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasProperty(fb4, "data[1].bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasRootProperty(fb4, "data[1].bbean.abean.fooProp"));
		assertEquals("yyy", BeanUtil.pojo.getProperty(fb4, "data[1].bbean.abean.fooProp"));

		assertTrue(BeanUtil.declared.hasProperty(fb4, "data[2].bbean.abean.fooProp"));
		assertEquals("zzz", BeanUtil.pojo.getProperty(fb4, "data[2].bbean.abean.fooProp"));
		BeanUtil.pojo.setProperty(fb4, "data[2].bbean.abean.fooProp", "ZZZ");
		assertEquals("ZZZ", BeanUtil.pojo.getProperty(fb4, "data[2].bbean.abean.fooProp"));

		assertTrue(BeanUtil.declared.hasProperty(fb4, "list[0].bbean.abean.fooProp"));
		assertEquals("LLL", BeanUtil.pojo.getProperty(fb4, "list[0].bbean.abean.fooProp"));
		BeanUtil.pojo.setProperty(fb4, "list[0].bbean.abean.fooProp", "EEE");
		assertEquals("EEE", BeanUtil.pojo.getProperty(fb4, "list[0].bbean.abean.fooProp"));
		assertEquals("lll", BeanUtil.pojo.getProperty(fb4, "list[1]"));
		BeanUtil.pojo.setProperty(fb4, "list[1]", "eee");

		assertFalse(BeanUtil.declared.hasProperty(fb4, "list[1].bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasRootProperty(fb4, "list[1].bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasProperty(fb4, "list[1]"));
		assertEquals("eee", BeanUtil.pojo.getProperty(fb4, "list[1]"));
		assertTrue(BeanUtil.declared.hasProperty(dummy, "fb.data[0].bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasProperty(dummy, "fb.data[1].bbean.abean.fooProp"));
		assertTrue(BeanUtil.declared.hasProperty(dummy, "fb.data[2].bbean.abean.fooProp"));
		assertEquals("xxx", BeanUtil.pojo.getProperty(dummy, "fb.data[0].bbean.abean.fooProp"));
		assertEquals("yyy", BeanUtil.pojo.getProperty(dummy, "fb.data[1].bbean.abean.fooProp"));
		assertEquals("zzz", BeanUtil.pojo.getProperty(dummy, "fb.data[2].bbean.abean.fooProp"));

		BeanUtil.pojo.setProperty(dummy, "fb.data[2].bbean.abean.fooProp", "ZZZ");
		assertEquals("ZZZ", BeanUtil.pojo.getProperty(dummy, "fb.data[2].bbean.abean.fooProp"));
		assertEquals(new Integer(173), BeanUtil.pojo.getProperty(dummy, "data[0]"));

		BeanUtil.pojo.setProperty(dummy, "data[0]", new Integer(-173));
		assertEquals(new Integer(-173), BeanUtil.pojo.getProperty(dummy, "data[0]"));
	}


	@Test
	public void testForced() {
		XBean x = new XBean();
		assertTrue(BeanUtil.declared.hasProperty(x, "y"));
		assertFalse(BeanUtil.declared.hasProperty(x, "y.foo"));
		assertTrue(BeanUtil.declared.hasRootProperty(x, "y.foo"));
		assertFalse(BeanUtil.declared.hasProperty(x, "y[23].foo"));
		assertTrue(BeanUtil.declared.hasRootProperty(x, "y[23].foo"));
		try {
			BeanUtil.pojo.setProperty(x, "y.foo", "yyy");
			fail("error");
		} catch (Exception ignored) {
		}
		assertNull(x.getY());

		BeanUtil.forced.setProperty(x, "y.foo", "yyy");
		assertTrue(BeanUtil.declared.hasProperty(x, "y.foo"));
		assertEquals("yyy", x.getY().getFoo());

		assertNotNull(x.getYy());
		assertFalse(BeanUtil.declared.hasProperty(x, "yy[2].foo"));
		assertTrue(BeanUtil.declared.hasRootProperty(x, "yy[2].foo"));
		try {
			BeanUtil.pojo.setProperty(x, "yy[2].foo", "yyy");
			fail("error");
		} catch (Exception ignored) {
		}
		assertNull(x.getYy()[2]);

		BeanUtil.forced.setProperty(x, "yy[2].foo", "xxx");
		assertTrue(BeanUtil.declared.hasProperty(x, "yy[2].foo"));
		assertEquals("xxx", x.getYy()[2].getFoo());

		assertFalse(BeanUtil.declared.hasProperty(x, "yy[20].foo"));
		assertTrue(BeanUtil.declared.hasRootProperty(x, "yy[20].foo"));
		BeanUtil.forced.setProperty(x, "yy[20].foo", "zzz");
		assertTrue(BeanUtil.declared.hasProperty(x, "yy[20].foo"));
		assertEquals("zzz", x.getYy()[20].getFoo());
	}


	@Test
	public void testSilent() {
		FooBean fb = new FooBean();
		assertFalse(BeanUtil.declared.hasProperty(fb, "notexisting"));
		assertFalse(BeanUtil.declared.hasRootProperty(fb, "notexisting"));
		try {
			BeanUtil.pojo.setProperty(fb, "notexisting", null);
			fail("error");
		} catch (Exception ignored) {
		}

		try {
			BeanUtil.silent.setProperty(fb, "notexisting", null);
		} catch (Exception ex) {
			fail("error");
		}
	}


	@Test
	public void testGenerics() {
		Gig gig = new Gig();

		assertEquals(String.class, BeanUtil.declared.getPropertyType(gig, "listOfStrings[1]"));
		BeanUtil.forced.setProperty(gig, "listOfStrings[1]", "string");
		assertNull(gig.getListOfStrings().get(0));
		assertEquals("string", gig.getListOfStrings().get(1));
		assertEquals(2, gig.getListOfStrings().size());

		assertEquals(MutableInteger.class, BeanUtil.declared.getPropertyType(gig, "listOfIntegers[1]"));
		BeanUtil.forced.setProperty(gig, "listOfIntegers[1]", Integer.valueOf(1));
		assertNull(gig.getListOfIntegers().get(0));
		assertEquals(1, gig.getListOfIntegers().get(1).intValue());
		assertEquals(2, gig.getListOfStrings().size());

		assertEquals(MutableInteger.class, BeanUtil.declared.getPropertyType(gig, "listOfIntegers[3]"));
		BeanUtil.forced.setProperty(gig, "listOfIntegers[3]", "3");
		assertNull(gig.getListOfIntegers().get(0));
		assertEquals(1, gig.getListOfIntegers().get(1).intValue());
		assertNull(gig.getListOfIntegers().get(2));
		assertEquals(3, gig.getListOfIntegers().get(3).intValue());
		assertEquals(4, gig.getListOfIntegers().size());

		assertNull(BeanUtil.declared.getPropertyType(gig, "listOfAbeans[1].fooProp"));   // [1] doesnt exist yet
		BeanUtil.forced.setProperty(gig, "listOfAbeans[1].fooProp", "xxx");
		assertEquals(String.class, BeanUtil.declared.getPropertyType(gig, "listOfAbeans[1].fooProp"));
		assertNull(gig.getListOfAbeans().get(0));
		assertEquals("xxx", gig.getListOfAbeans().get(1).getFooProp());
		assertEquals(2, gig.getListOfAbeans().size());

		assertEquals(Integer.class, BeanUtil.declared.getPropertyType(gig, "mapOfIntegers[kkk]"));
		BeanUtil.forced.setProperty(gig, "mapOfIntegers[kkk]", "173");
		assertEquals(173, gig.getMapOfIntegers().get("kkk").intValue());
		assertEquals(1, gig.getMapOfIntegers().size());

		assertEquals(Abean.class, BeanUtil.declared.getPropertyType(gig, "mapOfAbeans[kkk]"));
		BeanUtil.forced.setProperty(gig, "mapOfAbeans[kkk].fooProp", "zzz");
		assertEquals("zzz", gig.getMapOfAbeans().get("kkk").getFooProp());
		assertEquals(1, gig.getMapOfAbeans().size());

	}

	@Test
	public void testNoGenerics() {
		Gig gig = new Gig();

		BeanUtil.forced.setProperty(gig, "listOfStrings2[1]", "string");
		assertNull(gig.getListOfStrings2().get(0));
		assertEquals("string", gig.getListOfStrings2().get(1));
		assertEquals(2, gig.getListOfStrings2().size());


		BeanUtil.forced.setProperty(gig, "listOfIntegers2[1]", Integer.valueOf(1));
		assertNull(gig.getListOfIntegers2().get(0));
		assertEquals(1, ((Integer) gig.getListOfIntegers2().get(1)).intValue());
		assertEquals(2, gig.getListOfStrings2().size());

		BeanUtil.forced.setProperty(gig, "listOfIntegers2[3]", "3");
		assertNull(gig.getListOfIntegers2().get(0));
		assertEquals(1, ((Integer) gig.getListOfIntegers2().get(1)).intValue());
		assertNull(gig.getListOfIntegers2().get(2));
		assertEquals("3", gig.getListOfIntegers2().get(3));
		assertEquals(4, gig.getListOfIntegers2().size());

		BeanUtil.forced.setProperty(gig, "listOfAbeans2[1].fooProp", "xxx");
		assertNull(gig.getListOfAbeans2().get(0));
		assertEquals("xxx", ((Map) gig.getListOfAbeans2().get(1)).get("fooProp"));
		assertEquals(2, gig.getListOfAbeans2().size());

		BeanUtil.forced.setProperty(gig, "mapOfIntegers2[kkk]", "173");
		assertEquals("173", gig.getMapOfIntegers2().get("kkk"));
		assertEquals(1, gig.getMapOfIntegers2().size());

		BeanUtil.forced.setProperty(gig, "mapOfAbeans2[kkk].fooProp", "zzz");
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
			BeanUtil.pojo.getProperty(map, "xxx");
			fail("error");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.pojo.getProperty(gig, "doo");
			fail("error");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.pojo.setProperty(gig, "xxx", "value");
			fail("error");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.pojo.getProperty(gig, "listOfAbeans[1].fooProp");
			fail("error");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.forced.setProperty(gig, "listOfAbeans[xxx].fooProp", "123");
			fail("error");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		try {
			gig.setZoro("zoro");
			BeanUtil.pojo.getProperty(gig, "zoro[1].fooProp");
			fail("error");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.pojo.setProperty(gig, "zoro[1]", "foo");
			fail("error");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			BeanUtil.silent.setProperty(gig, "zoro[1].doo", "foo");
		} catch (Exception e) {
			fail("error");
			System.out.println(e.getMessage());
		}
	}


	@Test
	public void testGeneralMapOnly() {
		Map map = new HashMap();
		BeanUtil.forced.setProperty(map, "foo.lll", "value");
		assertNotNull(map.get("foo"));
		assertEquals("value", ((Map) map.get("foo")).get("lll"));

		map = new HashMap();
		BeanUtil.forced.setProperty(map, "foo.lll[2]", "value");
		assertNotNull(map.get("foo"));
		assertNotNull(((Map) map.get("foo")).get("lll"));
		assertEquals("value", ((Map) ((Map) map.get("foo")).get("lll")).get("2"));
	}


	@Test
	public void testInheritance() {
		ZBean zb = new ZBean();

		assertEquals("public", BeanUtil.pojo.getProperty(zb, "ppublic"));
		try {
			assertEquals("protected", BeanUtil.pojo.getProperty(zb, "pprotected"));
			fail("error");
		} catch (BeanException ignored) {
		}

		assertEquals("protected", BeanUtil.declared.getProperty(zb, "pprotected"));
		try {
			assertEquals("private", BeanUtil.declared.getProperty(zb, "pprivate"));
			fail("error");
		} catch (BeanException ignored) {
		}

		// top

		assertEquals("public", BeanUtil.pojo.getProperty(zb, "tpublic"));
		try {
			assertEquals("protected", BeanUtil.pojo.getProperty(zb, "tprotected"));
			fail("error");
		} catch (BeanException ignored) {
		}

		assertEquals("protected", BeanUtil.declared.getProperty(zb, "tprotected"));
		assertEquals("private", BeanUtil.declared.getProperty(zb, "tprivate"));
	}


	@Test
	public void testSimpleThis() {
		FooBean fb = new FooBean();
		BeanUtilBean beanUtilBean = new BeanUtilBean();

		beanUtilBean.setSimpleProperty(fb, "fooString", "test");
		assertEquals("test", fb.getFooString());
		assertEquals("test", beanUtilBean.getSimpleProperty(fb, "fooString"));
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
		Object value = BeanUtil.pojo.getProperty(i, "flag");
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
		value = BeanUtil.pojo.getProperty(i2, "flag");
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
			BeanUtil.pojo.getProperty(ub, "URLaddress");
		} catch (Exception ex) {
			fail("error");
		}
	}

	@Test
	public void testPropertiesWithDot() {
		Properties props = new Properties();
		BeanUtil.pojo.setProperty(props, "ldap", "data");

		assertEquals("data", props.getProperty("ldap"));

		BeanUtil.pojo.setProperty(props, "[ldap.auth.enabled]", "data2");

		assertEquals("data", props.getProperty("ldap"));
		assertEquals("data2", props.getProperty("ldap.auth.enabled"));


		Map map = new HashMap();
		FooBean fb = new FooBean();

		BeanUtil.pojo.setProperty(map, "[aaa.bbb]", fb);
		BeanUtil.forced.setProperty(map, "[aaa.bbb].fooMap[xxx.ccc]", "zzzz");
		assertEquals("zzzz", ((FooBean) map.get("aaa.bbb")).getFooMap().get("xxx.ccc"));

		BeanUtil.forced.setProperty(fb, "fooint", "123");
		assertEquals(123, fb.getFooint());

		try {
			BeanUtil.pojo.setProperty(map, ".[aaa.bbb]", "zzzz");
			fail("error");
		} catch (Exception ignored) {
		}

		try {
			BeanUtil.forced.setProperty(fb, "..fooint", "123");
			fail("error");
		} catch (Exception ignored) {
		}

		BeanUtil.forced.setProperty(map, "[aaa.bbb].fooMap.[eee.ccc]", "zzzz");
		// forced works because *this is a map!
		assertEquals("zzzz", BeanUtil.pojo.getProperty(map, "[aaa.bbb].fooMap.[eee.ccc]"));
	}

	@Test
	public void testEnums() {
		EnumBean enumBean = new EnumBean();

		BeanUtil.pojo.setProperty(enumBean, "id", Integer.valueOf(123));
		BeanUtil.pojo.setProperty(enumBean, "color", "RED");
		BeanUtil.pojo.setProperty(enumBean, "status", "STARTED");

		assertEquals(123, enumBean.getId());
		assertEquals(Color.RED, enumBean.getColor());
		assertEquals(Status.STARTED, enumBean.getStatus());
	}

	@Test
	public void testSubSup1() {
		SupBean supBean = new SupBean();
		//BeanUtil.pojo.setProperty(supBean, "v1", "V1");
		String v = BeanUtil.pojo.getProperty(supBean, "v1");

		assertEquals("v1sup", v);

		supBean = new SubBean();
		BeanUtil.pojo.setProperty(supBean, "v1", "V1");
		v = BeanUtil.pojo.getProperty(supBean, "v1");

		assertEquals("V1sup", v);
	}

	@Test
	public void testSubSup2() {
		SupBean supBean = new SubBean();
		BeanUtil.pojo.setProperty(supBean, "v2", "V2");
		//String v = (String) BeanUtil.pojo.getProperty(supBean, "v2");

		String v = BeanUtil.pojo.getProperty(supBean, "v2");

		assertEquals("V2sub", v);
	}

	@Test
	public void testCollections() {
		MixBean mixBean = new MixBean();
		BeanUtil.pojo.setProperty(mixBean, "data", "1,2,3");

		assertNotNull(mixBean.data);
		assertEquals(3, mixBean.data.size());
		assertEquals(1, mixBean.data.get(0).intValue());

		BeanUtil.pojo.setProperty(mixBean, "data2", "1,2,3,4");

		assertNotNull(mixBean.getData2());
		assertEquals(4, mixBean.getData2().size());
		assertEquals(1, mixBean.getData2().get(0).intValue());

		BeanUtil.pojo.setProperty(mixBean, "data5", "1,2,3,4,5");

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

		Object value = BeanUtil.pojo.getProperty(map, "[foo.bar]");
		assertNotNull(value);

		value = BeanUtil.pojo.getProperty(map, "[foo.bar].[zzz.xxx]");
		assertNotNull(value);
		assertEquals("hey", value.toString());

	}

}
