// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import junit.framework.TestCase;
import jodd.proxetta.data.BigFatJoe;
import jodd.proxetta.data.StatCounter;
import jodd.proxetta.data.StatCounterAdvice;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;
import jodd.io.FileUtil;
import jodd.util.ClassLoaderUtil;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.InterceptedBy;
import jodd.petite.meta.PetiteInject;
import jodd.jtx.meta.Transaction;
import jodd.jtx.JtxPropagationBehavior;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

public class BigClassTest extends TestCase {

	public void testAllFeatures() throws IOException, IllegalAccessException, InstantiationException {
		ProxyAspect aspect = new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo msign) {
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


		// test class annotation
		MadvocAction ma = (MadvocAction) clazz.getAnnotation(MadvocAction.class);
		assertEquals("madvocAction", ma.value());

		InterceptedBy ib = (InterceptedBy) clazz.getAnnotation(InterceptedBy.class);
		assertNotNull(ib.value());
		assertEquals(2, ib.value().length);


		// test method annotation
		ClassDescriptor cd = ClassIntrospector.lookup(clazz);
		Method m = cd.getMethod("publicMethod");
		assertNotNull(m);
		Annotation[] aa = m.getAnnotations();
		assertEquals(3, aa.length);
		Action a = (Action) aa[0];
		assertEquals("alias", a.alias());
		assertEquals("extension", a.extension());
		assertEquals("method", a.method());
		assertTrue(a.notInPath());
		assertEquals("value", a.value());

		PetiteInject pi = (PetiteInject) aa[1];
		assertEquals("", pi.value());

		Transaction tx = (Transaction) aa[2];
		assertTrue(tx.readOnly());
		assertEquals(-1, tx.timeout());
		assertEquals(JtxPropagationBehavior.PROPAGATION_REQUIRES_NEW, tx.propagation());


	}
}
