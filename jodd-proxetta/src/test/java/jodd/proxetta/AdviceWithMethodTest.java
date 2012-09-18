// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.CollectorAdvice;
import jodd.proxetta.data.Foo;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllTopMethodsPointcut;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.List;

public class AdviceWithMethodTest extends TestCase {

	public void testCollector() throws NoSuchFieldException, IllegalAccessException {
		ProxyProxetta proxetta = ProxyProxetta.withAspects(
				new ProxyAspect(CollectorAdvice.class, new AllTopMethodsPointcut())
		);

//		proxetta.setDebugFolder("d:\\");

		Foo foo = (Foo) proxetta.builder(Foo.class).newInstance();

		Field field = foo.getClass().getDeclaredField("$__methods$0");

		field.setAccessible(true);

		List<String> list = (List<String>) field.get(foo);

		assertNotNull(list);

		assertEquals(0, list.size());

		foo.m1();
		assertEquals(1, list.size());
		assertEquals("m1", list.get(0));

		foo.m1();
		assertEquals(2, list.size());
		assertEquals("m1", list.get(0));
		assertEquals("m1", list.get(1));
	}
}
