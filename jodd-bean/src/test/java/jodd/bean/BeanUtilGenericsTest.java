// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import jodd.bean.data.Woof;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BeanUtilGenericsTest {

	@Test
	public void testAllBeanSetters() {
		Woof woof = new Woof();
		Class type = woof.getClass();
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		Method[] allSetters = cd.getAllBeanSetters(true);
		assertNotNull(allSetters);
		assertEquals(7, allSetters.length);
	}

	// ---------------------------------------------------------------- fields

	public static class BaseClass<A, B> {
		public A f1;		// String
		public B f2;		// Integer


	}

	public static class ConcreteClass extends BaseClass<String, Integer> {
	}

	public static class BaseClass2<X> extends BaseClass<X, Integer> {
	}

	public static class ConcreteClass2 extends BaseClass2<String> {
	}

	@Test
	public void testSetField() {
		BaseClass base = new BaseClass();

		BeanUtil.setProperty(base, "f1", Integer.valueOf(173));
		assertEquals(Integer.valueOf(173), base.f1);
		assertEquals(Integer.class, base.f1.getClass());

		BeanUtil.setProperty(base, "f2", "123");
		assertEquals("123", base.f2);
		assertEquals(String.class, base.f2.getClass());

		// concrete implementation #1

		ConcreteClass impl1 = new ConcreteClass();

		BeanUtil.setProperty(impl1, "f1", Integer.valueOf(173));
		assertEquals("173", impl1.f1);
		assertEquals(String.class, impl1.f1.getClass());

		BeanUtil.setProperty(impl1, "f2", "123");
		assertEquals(Integer.valueOf(123), impl1.f2);
		assertEquals(Integer.class, impl1.f2.getClass());

		// concrete implementation #2

		ConcreteClass2 impl2 = new ConcreteClass2();

		BeanUtil.setProperty(impl2, "f1", Integer.valueOf(173));
		assertEquals("173", impl2.f1);
		assertEquals(String.class, impl2.f1.getClass());

		BeanUtil.setProperty(impl2, "f2", "123");
		assertEquals(Integer.valueOf(123), impl2.f2);
		assertEquals(Integer.class, impl2.f2.getClass());
	}


}