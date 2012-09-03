// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.data.StatCounterAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import junit.framework.TestCase;
import jodd.proxetta.data.FooProxyAdvice;
import jodd.proxetta.data.Foo;
import jodd.proxetta.data.Two;
import jodd.proxetta.data.StatCounter;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class SubclassTest extends TestCase {

	public void test1() {

		ProxyAspect a1 = new ProxyAspect(FooProxyAdvice.class, new ProxyPointcut() {
			public boolean apply(MethodInfo methodInfo) {
				return true;
			}
		});

/*
		byte[] b = Proxetta.withAspects(a1).createProxy(Foo.class);
		try {
			FileUtil.writeBytes("d:\\Foo.class", b);
		} catch (IOException e) {
			e.printStackTrace();
		}
*/
		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(a1);
		proxyProxetta.setClassNameSuffix("$$Proxetta");
		ProxyProxettaBuilder pb = proxyProxetta.builder();
		pb.setTarget(Foo.class);
		Foo foo = (Foo) pb.newInstance();

		Class fooProxyClass = foo.getClass();
		assertNotNull(fooProxyClass);

		Method[] methods = fooProxyClass.getMethods();
		assertEquals(11, methods.length);
		try {
			fooProxyClass.getMethod("m1");
		} catch (NoSuchMethodException nsmex) {
			fail(nsmex.toString());
		}


		methods = fooProxyClass.getDeclaredMethods();
		assertEquals(13, methods.length);
		try {
			fooProxyClass.getDeclaredMethod("m2");
		} catch (NoSuchMethodException nsmex) {
			fail(nsmex.toString());
		}

	}

	public void testProxyClassNames() {
		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()));
		proxyProxetta.setVariableClassName(true);

		ProxyProxettaBuilder builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		Foo foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$Proxetta1", foo.getClass().getName());

		builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$Proxetta2", foo.getClass().getName());

		proxyProxetta.setClassNameSuffix("$Ppp");
		builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$Ppp3", foo.getClass().getName());

		proxyProxetta.setClassNameSuffix("$Proxetta");
		proxyProxetta.setVariableClassName(false);
		builder = proxyProxetta.builder(Foo.class, ".Too");
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getPackage().getName() + ".Too$Proxetta", foo.getClass().getName());

		builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		builder.setTargetProxyClassName("foo.");
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals("foo.Foo$Proxetta", foo.getClass().getName());

		proxyProxetta.setClassNameSuffix(null);
		builder = proxyProxetta.builder();
		builder.setTargetProxyClassName("foo.Fff");
		builder.setTarget(Foo.class);
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals("foo.Fff", foo.getClass().getName());

	}

	public void testInnerOverride() {
		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()));
		ProxyProxettaBuilder builder = proxyProxetta.builder();
		builder.setTarget(Two.class);
		builder.setTargetProxyClassName("foo.");

		Two two = (Two) builder.newInstance();

		assertNotNull(two);
		assertEquals("foo.Two$Proxetta", two.getClass().getName());
	}

	public void testJdk() throws Exception {
		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new AllMethodsPointcut()));
		proxyProxetta.setVariableClassName(false);

		ProxyProxettaBuilder builder = proxyProxetta.builder();
		builder.setTarget(Object.class);
		try {
			builder.define();
			fail("Default class loader should not load java.*");
		} catch (RuntimeException rex) {
			// ignore
		}

		builder = proxyProxetta.builder();
		builder.setTarget(Object.class);
		builder.setTargetProxyClassName("foo.");
		Object object = builder.newInstance();

		assertNotNull(object);
		assertEquals("foo.Object$Proxetta", object.getClass().getName());

		System.out.println("----------list");

		StatCounter.counter = 0;

		builder = proxyProxetta.builder(ArrayList.class, "foo.");
		List list = (List) builder.newInstance();
		assertNotNull(list);
		assertEquals("foo.ArrayList$Proxetta", list.getClass().getName());

		assertEquals(1, StatCounter.counter);
		list.add(new Integer(1));
		assertTrue(StatCounter.counter == 3 || StatCounter.counter == 2);

		System.out.println("----------set");

		builder = proxyProxetta.builder(HashSet.class, "foo.");
		Set set = (Set) builder.newInstance();

		assertNotNull(set);
		assertEquals("foo.HashSet$Proxetta", set.getClass().getName());

		assertTrue(StatCounter.counter == 4 || StatCounter.counter == 3);
		set.add(new Integer(1));
		assertTrue(StatCounter.counter == 5 || StatCounter.counter == 4);

	}
}
