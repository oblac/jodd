// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import junit.framework.TestCase;
import jodd.proxetta.data.FooProxyAdvice;
import jodd.proxetta.data.Foo;
import jodd.proxetta.pointcuts.AllMethodsPointcut;

import java.lang.reflect.Method;

public class SubclassTest extends TestCase {

	public void test1() {

		ProxyAspect a1 = new ProxyAspect(FooProxyAdvice.class, new ProxyPointcut() {
			public boolean apply(MethodSignature msign) {
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

	public void test2() {
		Foo foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.variableClassName()
				.createProxyInstance(Foo.class);
	    assertNotNull(foo);

		foo = Proxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()))
				.variableClassName()
				.createProxyInstance(Foo.class);
	    assertNotNull(foo);


	}
}
