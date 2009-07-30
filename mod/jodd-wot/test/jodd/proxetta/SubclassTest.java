// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import junit.framework.TestCase;
import jodd.proxetta.data.FooProxyAdvice;
import jodd.proxetta.data.Foo;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Date;

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
		Foo foo = Proxetta.withAspects(a1).createProxyInstance(Foo.class);


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
		Foo foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.variableClassName()
				.createProxyInstance(Foo.class);
		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$Proxetta1", foo.getClass().getName());

		foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.variableClassName()
				.createProxyInstance(Foo.class);
		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$Proxetta2", foo.getClass().getName());

		foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.variableClassName()
				.useClassNameSuffix("$Ppp")
				.createProxyInstance(Foo.class);
		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$Ppp3", foo.getClass().getName());


		foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.createProxyInstance(Foo.class, ".Too");
		assertNotNull(foo);
		assertEquals(Foo.class.getPackage().getName() + ".Too$Proxetta", foo.getClass().getName());

		foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.createProxyInstance(Foo.class, "foo.");
		assertNotNull(foo);
		assertEquals("foo.Foo$Proxetta", foo.getClass().getName());

		foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.dontUseClassNameSuffix()
				.createProxyInstance(Foo.class, "foo.Fff");
		assertNotNull(foo);
		assertEquals("foo.Fff", foo.getClass().getName());

	}


	public void testJdk() {
		try {
			Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
					.variableClassName()
					.createProxyInstance(Object.class);
			fail("Default class loader should not load java.*");
		} catch (RuntimeException rex) {
			// ignore
		}

		Object foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.createProxyInstance(Object.class, "foo.");
		assertNotNull(foo);
		assertEquals("foo.Object$Proxetta", foo.getClass().getName());

	}
}
