// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import junit.framework.TestCase;
import jodd.proxetta.data.BigFatJoe;
import jodd.proxetta.data.StatCounter;
import jodd.proxetta.data.StatCounterAdvice;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;
import jodd.io.FileUtil;
import jodd.util.ClassLoaderUtil;
import jodd.bean.BeanUtil;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.madvoc.meta.Action;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

public class BigClassTest extends TestCase {

	public void testAllFeatures() throws IOException, IllegalAccessException, InstantiationException {
		ProxyAspect aspect = new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodSignature msign) {
				System.out.println(!isRootMethod(msign) + " " + msign.getDeclaredClassName() + '#' + msign.getMethodName());
				return !isRootMethod(msign);
			}
		});

		byte[] classBytes = Proxetta.withAspects(aspect).createProxy(BigFatJoe.class);
		FileUtil.writeBytes("d://joe.class", classBytes);
		Class clazz = ClassLoaderUtil.defineClass(classBytes);
		BigFatJoe bigFatJoe = (BigFatJoe) clazz.newInstance();

		assertEquals("jodd.proxetta.data.BigFatJoe$Proxetta", bigFatJoe.getClass().getName());

		// test invocation

		assertEquals(3, StatCounter.counter);		// 2 x static + 1 x instance
		bigFatJoe.publicMethod();
		assertEquals(4, StatCounter.counter);
		bigFatJoe.callInnerMethods();
		assertEquals(7, StatCounter.counter);		// private method is not overriden

		bigFatJoe.superPublicMethod();
		assertEquals(8, StatCounter.counter);
		bigFatJoe.callInnerMethods2();
		assertEquals(9, StatCounter.counter);		// only public super methods are overriden

		// test annotation
		ClassDescriptor cd = ClassIntrospector.lookup(clazz);
		Method m = cd.getMethod("publicMethod");
		assertNotNull(m);
		Annotation[] ad = m.getAnnotations();
		assertEquals(1, ad.length);
		Action a = (Action) ad[0];
		assertEquals("alias", a.alias());
		assertEquals("extension", a.extension());
		assertEquals("method", a.method());
		assertTrue(a.notInPath());
		assertEquals("value", a.value());

		
	}
}
