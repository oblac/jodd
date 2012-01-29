// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import jodd.util.testdata.JavaBean;
import jodd.util.testdata.W;
import jodd.util.testdata2.En;
import junit.framework.TestCase;
import jodd.util.testdata.A;
import jodd.util.testdata.B;
import jodd.util.testdata.C;
import jodd.util.testdata2.D;
import jodd.util.testdata2.E;
import jodd.util.subclass.SBase;
import jodd.util.subclass.SOne;
import jodd.util.subclass.IOne;
import jodd.util.subclass.STwo;
import jodd.util.subclass.ITwo;
import jodd.util.subclass.IBase;
import jodd.util.subclass.IExtra;
import jodd.mutable.MutableInteger;

public class ReflectUtilTest extends TestCase {

	public void testInvoke() {
		TFooBean bean = new TFooBean();
		
		String result;		
		try {
			result = (String) ReflectUtil.invoke(TFooBean.class, bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invoke(bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invoke(bean, "getPublic", null);
			assertEquals("public", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
		
		try {		
			ReflectUtil.invoke(TFooBean.class, bean, "getDefault", null, null);
			fail("ReflectUtil.invoke() works irregular!");
		} catch (Exception e) {}
		
		try {		
			ReflectUtil.invoke(TFooBean.class, bean, "getProtected", null, null);
			fail("ReflectUtil.invoke() works irregular!");
		} catch (Exception e) {}

		try {		
			ReflectUtil.invoke(TFooBean.class, bean, "getPrivate", null, null);
			fail("ReflectUtil.invoke() works irregular!");
		} catch (Exception e) {}
	}
	
	
	public void testInvokeEx() {
		TFooBean bean = new TFooBean();
		
		String result;		
		try {
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getPublic", null, null);
			assertEquals("public", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getPublic", null);
			assertEquals("public", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
		
		try {		
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getDefault", null, null);
			assertEquals("default", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getDefault", null, null);
			assertEquals("default", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
		
		try {		
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getProtected", null, null);
			assertEquals("protected", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getProtected", null, null);
			assertEquals("protected", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}

		try {		
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getPrivate", null, null);
			assertEquals("private", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getPrivate", null);
			assertEquals("private", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
	
	}
	
	public void testInvoke2() {
		TFooBean bean = new TFooBean();
		String result;
		try {		
			result = (String) ReflectUtil.invoke(TFooBean.class, bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"qwerty", new Integer(173)});
			assertEquals("qwerty173", result);
			result = (String) ReflectUtil.invoke(TFooBean.class, bean, "getMore", new Object[] {"Qwerty", new Integer(173)});
			assertEquals("Qwerty173", result);
			result = (String) ReflectUtil.invoke(bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"QWerty", new Integer(173)});
			assertEquals("QWerty173", result);
			result = (String) ReflectUtil.invoke(bean, "getMore", new Object[] {"QWErty", new Integer(173)});
			assertEquals("QWErty173", result);
			
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"qwerty", new Integer(173)});
			assertEquals("qwerty173", result);
			result = (String) ReflectUtil.invokeDeclared(TFooBean.class, bean, "getMore", new Object[] {"Qwerty", new Integer(173)});
			assertEquals("Qwerty173", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getMore", new Class[] {String.class, Integer.class}, new Object[] {"QWerty", new Integer(173)});
			assertEquals("QWerty173", result);
			result = (String) ReflectUtil.invokeDeclared(bean, "getMore", new Object[] {"QWErty", new Integer(173)});
			assertEquals("QWErty173", result);
		} catch (Exception e) {
			fail("ReflectUtil.invoke() failed " + e.toString());
		}
	}
	
	
	public void testMethod0() {
		TFooBean bean = new TFooBean();
		Method m;
		m = ReflectUtil.getMethod0(TFooBean.class, "getMore", String.class, Integer.class);
		assertNotNull(m);
		
		m = ReflectUtil.getMethod0(bean.getClass(), "getMore", String.class, Integer.class);
		assertNotNull(m);
		
		m = ReflectUtil.getMethod0(bean.getClass(), "getXXX", String.class, Integer.class);
		assertNull(m);
		
		m = ReflectUtil.getMethod0(bean.getClass(), "getPublic");
		assertNotNull(m);
		
		m = ReflectUtil.getMethod0(bean.getClass(), "getDefault");
		assertNull(m);
		
		m = ReflectUtil.getMethod0(bean.getClass(), "getProtected");
		assertNull(m);
		
		m = ReflectUtil.getMethod0(bean.getClass(), "getPrivate");
		assertNull(m);
	}


	public void testMethod() {
		TFooBean bean = new TFooBean();
		Method m;
		m = ReflectUtil.findMethod(TFooBean.class, "getMore");
		assertNotNull(m);
		
		m = ReflectUtil.findMethod(bean.getClass(), "getMore");
		assertNotNull(m);
		
		m = ReflectUtil.findMethod(bean.getClass(), "getXXX");
		assertNull(m);
	}
	
	
	public void testMatchClasses() {
		TFooBean a = new TFooBean();
		TFooBean b = new TFooBean();
		TFooBean2 c = new TFooBean2();

		assertTrue(TFooBean.class.isInstance(a));
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, a.getClass()));
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, b.getClass()));
		assertTrue(ReflectUtil.isSubclass(a.getClass(), b.getClass()));
		assertTrue(ReflectUtil.isSubclass(b.getClass(), a.getClass()));
		
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, c.getClass()));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, TFooBean.class));
		assertFalse(ReflectUtil.isSubclass(TFooBean.class, TFooBean2.class));
		assertTrue(ReflectUtil.isSubclass(c.getClass(), TFooBean.class));
		assertFalse(ReflectUtil.isSubclass(a.getClass(), TFooBean2.class));
		
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, Serializable.class));
		assertTrue(Serializable.class.isInstance(c));
		//noinspection ConstantConditions
		assertTrue(c instanceof Serializable);
		assertTrue(ReflectUtil.isInstanceOf(c, Serializable.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, Serializable.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, Comparable.class));
		assertFalse(ReflectUtil.isSubclass(TFooBean.class, Comparable.class));

		assertTrue(ReflectUtil.isSubclass(TFooBean.class, TFooIndyEx.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean2.class, TFooIndyEx.class));
		assertTrue(ReflectUtil.isSubclass(TFooBean.class, TFooIndy.class));
	}


	public void testAccessibleA() {
		Method[] ms = ReflectUtil.getAccessibleMethods(A.class, null);
		assertEquals(4 + 11, ms.length);			// there are 11 accessible Object methods (9 public + 2 protected)
		ms = ReflectUtil.getAccessibleMethods(A.class);
		assertEquals(4, ms.length);
		ms = A.class.getMethods();
		assertEquals(1 + 9, ms.length);				// there are 9 public Object methods
		ms = A.class.getDeclaredMethods();
		assertEquals(4, ms.length);
		ms = ReflectUtil.getSupportedMethods(A.class, null);
		assertEquals(4 + 12, ms.length);			// there are 12 total Object methods (9 public + 2 protected + 1 private)
		ms = ReflectUtil.getSupportedMethods(A.class);
		assertEquals(4, ms.length);


		Field[] fs = ReflectUtil.getAccessibleFields(A.class);
		assertEquals(4, fs.length);
		fs = A.class.getFields();
		assertEquals(1, fs.length);
		fs = A.class.getDeclaredFields();
		assertEquals(4, fs.length);
		fs = ReflectUtil.getSupportedFields(A.class);
		assertEquals(4, fs.length);
	}

	public void testAccessibleB() {
		Method[] ms = ReflectUtil.getAccessibleMethods(B.class, null);
		assertEquals(3 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(B.class);
		assertEquals(3, ms.length);
		ms = B.class.getMethods();
		assertEquals(1 + 9, ms.length);
		ms = B.class.getDeclaredMethods();
		assertEquals(0, ms.length);
		ms = ReflectUtil.getSupportedMethods(B.class, null);
		assertEquals(4 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(B.class);
		assertEquals(4, ms.length);


		Field[] fs = ReflectUtil.getAccessibleFields(B.class);
		assertEquals(3, fs.length);
		fs = B.class.getFields();
		assertEquals(1, fs.length);
		fs = B.class.getDeclaredFields();
		assertEquals(0, fs.length);
		fs = ReflectUtil.getSupportedFields(B.class);
		assertEquals(4, fs.length);
	}

	public void testAccessibleC() {
		Method[] ms = ReflectUtil.getAccessibleMethods(C.class, null);
		assertEquals(5 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(C.class);
		assertEquals(5, ms.length);
		ms = C.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = C.class.getDeclaredMethods();
		assertEquals(5, ms.length);
		ms = ReflectUtil.getSupportedMethods(C.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(C.class);
		assertEquals(5, ms.length);


		Field[] fs = ReflectUtil.getAccessibleFields(C.class);
		assertEquals(5, fs.length);
		fs = C.class.getFields();
		assertEquals(3, fs.length);
		fs = C.class.getDeclaredFields();
		assertEquals(5, fs.length);
		fs = ReflectUtil.getSupportedFields(C.class);
		assertEquals(5, fs.length);
	}

	public void testAccessibleD() {
		Method[] ms = ReflectUtil.getAccessibleMethods(D.class, null);
		assertEquals(3 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(D.class);
		assertEquals(3, ms.length);
		ms = D.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = D.class.getDeclaredMethods();
		assertEquals(0, ms.length);
		ms = ReflectUtil.getSupportedMethods(D.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(D.class);
		assertEquals(5, ms.length);

		Field[] fs = ReflectUtil.getAccessibleFields(D.class);
		assertEquals(3, fs.length);
		fs = D.class.getFields();
		assertEquals(3, fs.length);
		fs = D.class.getDeclaredFields();
		assertEquals(0, fs.length);
		fs = ReflectUtil.getSupportedFields(D.class);
		assertEquals(5, fs.length);
	}
		
	public void testAccessibleE() {
		Method[] ms = ReflectUtil.getAccessibleMethods(E.class, null);
		assertEquals(5 + 11, ms.length);
		ms = ReflectUtil.getAccessibleMethods(E.class);
		assertEquals(5, ms.length);
		ms = E.class.getMethods();
		assertEquals(2 + 9, ms.length);
		ms = E.class.getDeclaredMethods();
		assertEquals(4, ms.length);
		ms = ReflectUtil.getSupportedMethods(E.class, null);
		assertEquals(5 + 12, ms.length);
		ms = ReflectUtil.getSupportedMethods(E.class);
		assertEquals(5, ms.length);

		Field[] fs = ReflectUtil.getAccessibleFields(E.class);
		assertEquals(5, fs.length);
		fs = E.class.getFields();
		assertEquals(4, fs.length);
		fs = E.class.getDeclaredFields();
		assertEquals(4, fs.length);
		fs = ReflectUtil.getSupportedFields(E.class);
		assertEquals(5, fs.length);
	}


	public void testCast() {

		String s = "123";
		Integer d = ReflectUtil.castType(s, Integer.class);
		assertEquals(123, d.intValue());

		s = ReflectUtil.castType(d, String.class);
		assertEquals("123", s);

		MutableInteger md = ReflectUtil.castType(s, MutableInteger.class);
		assertEquals(123, md.intValue());

		B b = new B();
		A a = ReflectUtil.castType(b, A.class);
		assertEquals(a, b);
	}

	public void testCastEnums() {

		En en = ReflectUtil.castType("ONE", En.class);
		assertEquals(En.ONE, en);
		en = ReflectUtil.castType("TWO", En.class);
		assertEquals(En.TWO, en);
	}


	public void testIsSubclassAndInterface() {
		assertTrue(ReflectUtil.isSubclass(SBase.class, SBase.class));

		assertTrue(ReflectUtil.isSubclass(SOne.class, SBase.class));
		assertTrue(ReflectUtil.isSubclass(SOne.class, IOne.class));
		assertTrue(ReflectUtil.isInterfaceImpl(SOne.class, IOne.class));
		assertTrue(ReflectUtil.isSubclass(SOne.class, Serializable.class));
		assertTrue(ReflectUtil.isInterfaceImpl(SOne.class, Serializable.class));
		assertTrue(ReflectUtil.isSubclass(SOne.class, SOne.class));

		assertTrue(ReflectUtil.isSubclass(STwo.class, SBase.class));
		assertTrue(ReflectUtil.isSubclass(STwo.class, IOne.class));
		assertTrue(ReflectUtil.isInterfaceImpl(STwo.class, IOne.class));
		assertTrue(ReflectUtil.isSubclass(STwo.class, Serializable.class));
		assertTrue(ReflectUtil.isInterfaceImpl(STwo.class, Serializable.class));
		assertTrue(ReflectUtil.isSubclass(STwo.class, ITwo.class));
		assertTrue(ReflectUtil.isInterfaceImpl(STwo.class, ITwo.class));
		assertTrue(ReflectUtil.isSubclass(STwo.class, IBase.class));
		assertTrue(ReflectUtil.isInterfaceImpl(STwo.class, IBase.class));
		assertTrue(ReflectUtil.isSubclass(STwo.class, IExtra.class));
		assertTrue(ReflectUtil.isInterfaceImpl(STwo.class, IExtra.class));
		assertTrue(ReflectUtil.isSubclass(STwo.class, STwo.class));
		assertFalse(ReflectUtil.isInterfaceImpl(STwo.class, STwo.class));
	}

	public void testUnsetFieldModifiers() throws NoSuchFieldException, IllegalAccessException {
		Field f = W.class.getDeclaredField("constant");
		f.setAccessible(true);

		ReflectUtil.unsetFieldModifiers(f, Modifier.FINAL);

		try {
			f.set(null, Integer.valueOf(173));
		} catch (IllegalAccessException ignore) {
			fail();
		}

		Integer i = (Integer) f.get(null);
		assertEquals(173, i.intValue());
	}

	public void testUnsetFieldModifiers2() throws NoSuchFieldException, IllegalAccessException {
		Field f = W.class.getDeclaredField("priv");
		f.setAccessible(true);

		ReflectUtil.unsetFieldModifiers(f, Modifier.FINAL);

		try {
			f.set(null, Integer.valueOf(173));
		} catch (IllegalAccessException ignore) {
			fail();
		}

		Integer i = (Integer) f.get(null);
		assertEquals(173, i.intValue());
	}

	public void testSetFieldModifiers() throws NoSuchFieldException, IllegalAccessException {
		Field f = W.class.getDeclaredField("regular");

		ReflectUtil.setFieldModifiers(f, Modifier.FINAL);

		try {
			f.set(null, Integer.valueOf(173));
			fail();
		} catch (IllegalAccessException ignore) {
		}
	}

	public void testBeanPropertyNames() {
		String name = ReflectUtil.getBeanPropertyGetterName(ReflectUtil.findMethod(JavaBean.class, "getOne"));
		assertEquals("one", name);

		name = ReflectUtil.getBeanPropertySetterName(ReflectUtil.findMethod(JavaBean.class, "setOne"));
		assertEquals("one", name);

		name = ReflectUtil.getBeanPropertyGetterName(ReflectUtil.findMethod(JavaBean.class, "isTwo"));
		assertEquals("two", name);

		name = ReflectUtil.getBeanPropertySetterName(ReflectUtil.findMethod(JavaBean.class, "setTwo"));
		assertEquals("two", name);

		name = ReflectUtil.getBeanPropertyGetterName(ReflectUtil.findMethod(JavaBean.class, "getThree"));
		assertEquals("three", name);

		name = ReflectUtil.getBeanPropertySetterName(ReflectUtil.findMethod(JavaBean.class, "setThree"));
		assertEquals("three", name);

		name = ReflectUtil.getBeanPropertyGetterName(ReflectUtil.findMethod(JavaBean.class, "getF"));
		assertEquals("f", name);

		name = ReflectUtil.getBeanPropertySetterName(ReflectUtil.findMethod(JavaBean.class, "setF"));
		assertEquals("f", name);

		name = ReflectUtil.getBeanPropertyGetterName(ReflectUtil.findMethod(JavaBean.class, "getG"));
		assertEquals("g", name);

		name = ReflectUtil.getBeanPropertySetterName(ReflectUtil.findMethod(JavaBean.class, "setG"));
		assertEquals("g", name);

		name = ReflectUtil.getBeanPropertyGetterName(ReflectUtil.findMethod(JavaBean.class, "getURL"));
		assertEquals("URL", name);

		name = ReflectUtil.getBeanPropertySetterName(ReflectUtil.findMethod(JavaBean.class, "setURL"));
		assertEquals("URL", name);

		name = ReflectUtil.getBeanPropertyGetterName(ReflectUtil.findMethod(JavaBean.class, "getBIGsmall"));
		assertEquals("BIGsmall", name);

		name = ReflectUtil.getBeanPropertySetterName(ReflectUtil.findMethod(JavaBean.class, "setBIGsmall"));
		assertEquals("BIGsmall", name);
	}

}
