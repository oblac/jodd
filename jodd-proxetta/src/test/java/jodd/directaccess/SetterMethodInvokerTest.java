// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.directaccess;

import jodd.util.ReflectUtil;
import org.junit.Test;

import static jodd.directaccess.SetterMethodInvokerClassBuilder.createNewInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SetterMethodInvokerTest {

	@Test
	public void testProp0() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp0"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, "jodd");
		assertEquals("jodd", someClass.getProp0());
	}
	@Test
	public void testProp1() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp1"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Integer.valueOf(123));
		assertEquals(123, someClass.getProp1());
	}

	@Test
	public void testProp2() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp2"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Long.valueOf(123));
		assertEquals(123, someClass.getProp2());
	}

	@Test
	public void testProp3() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp3"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Float.valueOf(12.3f));
		assertEquals(12.3, someClass.getProp3(), 0.1);
	}

	@Test
	public void testProp4() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp4"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Double.valueOf(12.3));
		assertEquals(12.3, someClass.getProp4(), 0.1);
	}

	@Test
	public void testProp5() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp5"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Short.valueOf((short) 12));
		assertEquals((short) 12, someClass.getProp5());
	}

	@Test
	public void testProp6() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp6"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Byte.valueOf((byte) 12));
		assertEquals((byte) 12, someClass.getProp6());
	}

	@Test
	public void testProp7() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp7"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Boolean.TRUE);
		assertTrue(someClass.isProp7());
	}

	@Test
	public void testProp8() throws InstantiationException, IllegalAccessException {
		SetterMethodInvoker methodInvoker = createNewInstance(ReflectUtil.findMethod(SomeClass.class, "setProp8"));

		SomeClass someClass = new SomeClass();

		methodInvoker.invoke(someClass, Character.valueOf('a'));
		assertEquals('a', someClass.getProp8());
	}

}