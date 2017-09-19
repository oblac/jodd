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

package jodd.proxetta;

import jodd.proxetta.fixtures.data.*;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.pointcuts.AllMethodsPointcut;
import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SubclassTest {

	@Test
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
		proxyProxetta.setClassNameSuffix("$$$Proxetta");
		ProxyProxettaBuilder pb = proxyProxetta.builder();
		pb.setTarget(Foo.class);
		Foo foo = (Foo) pb.newInstance();

		Class fooProxyClass = foo.getClass();
		assertNotNull(fooProxyClass);

		Method[] methods = fooProxyClass.getMethods();
		assertEquals(12, methods.length);
		try {
			fooProxyClass.getMethod("m1");
		} catch (NoSuchMethodException nsmex) {
			fail(nsmex.toString());
		}


		methods = fooProxyClass.getDeclaredMethods();
		assertEquals(15, methods.length);
		try {
			fooProxyClass.getDeclaredMethod("m2");
		} catch (NoSuchMethodException nsmex) {
			fail(nsmex.toString());
		}

	}

	@Test
	public void testProxyClassNames() {
		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()));
		proxyProxetta.setVariableClassName(true);

		ProxyProxettaBuilder builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		Foo foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$$Proxetta", StringUtil.substring(foo.getClass().getName(), 0, -1));

		builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$$Proxetta", StringUtil.substring(foo.getClass().getName(), 0, -1));

		proxyProxetta.setClassNameSuffix("$$Ppp");
		builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getName() + "$$Ppp", StringUtil.substring(foo.getClass().getName(), 0, -1));

		proxyProxetta.setClassNameSuffix("$$Proxetta");
		proxyProxetta.setVariableClassName(false);
		builder = proxyProxetta.builder(Foo.class, ".Too");
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals(Foo.class.getPackage().getName() + ".Too$$Proxetta", foo.getClass().getName());

		builder = proxyProxetta.builder();
		builder.setTarget(Foo.class);
		builder.setTargetProxyClassName("foo.");
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals("foo.Foo$$Proxetta", foo.getClass().getName());

		proxyProxetta.setClassNameSuffix(null);
		builder = proxyProxetta.builder();
		builder.setTargetProxyClassName("foo.Fff");
		builder.setTarget(Foo.class);
		foo = (Foo) builder.newInstance();

		assertNotNull(foo);
		assertEquals("foo.Fff", foo.getClass().getName());

	}

	@Test
	public void testInnerOverride() {
		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(new ProxyAspect(FooProxyAdvice.class, new AllMethodsPointcut()));
		ProxyProxettaBuilder builder = proxyProxetta.builder();
		builder.setTarget(Two.class);
		builder.setTargetProxyClassName("foo.");

		Two two = (Two) builder.newInstance();

		assertNotNull(two);
		assertEquals("foo.Two$$Proxetta", two.getClass().getName());
	}

	@Test
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
		assertEquals("foo.Object$$Proxetta", object.getClass().getName());

		//System.out.println("----------list");

		StatCounter.counter = 0;

		builder = proxyProxetta.builder(ArrayList.class, "foo.");
		List list = (List) builder.newInstance();
		assertNotNull(list);
		assertEquals("foo.ArrayList$$Proxetta", list.getClass().getName());

		assertEquals(1, StatCounter.counter);
		list.add(new Integer(1));
		assertTrue(StatCounter.counter == 3 || StatCounter.counter == 2);

		System.out.println("----------set");

		builder = proxyProxetta.builder(HashSet.class, "foo.");
		Set set = (Set) builder.newInstance();

		assertNotNull(set);
		assertEquals("foo.HashSet$$Proxetta", set.getClass().getName());

		assertTrue(StatCounter.counter == 4 || StatCounter.counter == 3);
		set.add(new Integer(1));
		assertTrue(StatCounter.counter == 5 || StatCounter.counter == 4);

	}
}
