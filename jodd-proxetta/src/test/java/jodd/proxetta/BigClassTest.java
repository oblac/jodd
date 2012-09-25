// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.mutable.MutableBoolean;
import jodd.proxetta.data.*;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;
import jodd.util.ClassLoaderUtil;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class BigClassTest {

	@Test
	public void testAllFeatures() throws IOException, IllegalAccessException, InstantiationException {
		StatCounter.counter = 0;
		final MutableBoolean firstTime = new MutableBoolean(true);

		ProxyAspect aspect = new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo mi) {
				if (firstTime.value) {
					firstTime.value = false;
					ClassInfo ci = mi.getClassInfo();
					assertEquals("BigFatJoe", ci.getClassname());
					assertEquals(BigFatJoe.class.getPackage().getName(), ci.getPackage());
					assertEquals("jodd/proxetta/data/BigFatJoe", ci.getReference());
					assertEquals("jodd/proxetta/data/SmallSkinnyZoe", ci.getSuperName());
					AnnotationInfo[] anns = ci.getAnnotations();
					assertNotNull(anns);
					assertEquals(3, anns.length);
					AnnotationInfo ai = anns[0];
					assertSame(ai, getAnnotation(ci, MadvocAction.class));
					assertEquals(MadvocAction.class.getName(), ai.getAnnotationClassname());
					assertEquals("L" + MadvocAction.class.getName().replace('.', '/') + ";", ai.getAnnotationSignature());
					assertEquals("madvocAction", ai.getElement("value"));
					ai = anns[1];
					assertSame(ai, getAnnotation(ci, PetiteBean.class));
					assertEquals(PetiteBean.class.getName(), ai.getAnnotationClassname());
					assertEquals("L" + PetiteBean.class.getName().replace('.', '/') + ";", ai.getAnnotationSignature());
					ai = anns[2];
					assertSame(ai, getAnnotation(ci, InterceptedBy.class));
					assertEquals(InterceptedBy.class.getName(), ai.getAnnotationClassname());
					assertEquals("L" + InterceptedBy.class.getName().replace('.', '/') + ";", ai.getAnnotationSignature());
					assertTrue(ai.getElement("value") instanceof Object[]);
					assertFalse(ai.getElement("value") instanceof String[]);
					Object c1 = ((Object[]) ai.getElement("value"))[0];
					assertEquals("Ljodd/proxetta/data/Str;", ((Type) c1).getDescriptor());
				}
				if (mi.getMethodName().equals("publicMethod")) {
					AnnotationInfo[] anns = mi.getAnnotations();
					assertNotNull(anns);
					assertEquals(3, anns.length);

					AnnotationInfo ai = anns[0];
					assertSame(ai, getAnnotation(mi, Action.class));
					assertEquals(Action.class.getName(), ai.getAnnotationClassname());
					assertEquals("value", ai.getElement("value"));
					assertEquals("alias", ai.getElement("alias"));

					ai = anns[1];
					assertSame(ai, getAnnotation(mi, PetiteInject.class));
					assertEquals(PetiteInject.class.getName(), ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());

					ai = anns[2];
					assertSame(ai, getAnnotation(mi, Transaction.class));
					assertEquals(Transaction.class.getName(), ai.getAnnotationClassname());
					assertEquals(2, ai.getElementNames().size());
					String s = (String) ai.getElement("propagation");
					assertEquals("PROPAGATION_REQUIRES_NEW", s);
				}
				if (mi.getMethodName().equals("superPublicMethod")) {
					AnnotationInfo[] anns = mi.getAnnotations();
					assertNotNull(anns);
					assertEquals(3, anns.length);

					AnnotationInfo ai = anns[0];
					assertSame(ai, getAnnotation(mi, Action.class));
					assertEquals(Action.class.getName(), ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());

					ai = anns[1];
					assertSame(ai, getAnnotation(mi, PetiteInject.class));
					assertEquals(PetiteInject.class.getName(), ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());

					ai = anns[2];
					assertSame(ai, getAnnotation(mi, Transaction.class));
					assertEquals(Transaction.class.getName(), ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());
				}
				System.out.println(!isRootMethod(mi) + " " + mi.getDeclaredClassName() + '#' + mi.getMethodName());
				return !isRootMethod(mi);
			}
		});

		byte[] classBytes = ProxyProxetta.withAspects(aspect).builder(BigFatJoe.class).create();
		//FileUtil.writeBytes("d://joe.class", classBytes);
		Class clazz = ClassLoaderUtil.defineClass(classBytes);
		BigFatJoe bigFatJoe = (BigFatJoe) clazz.newInstance();

		assertEquals(BigFatJoe.class.getName() + "$Proxetta", bigFatJoe.getClass().getName());

		// test invocation

		assertEquals(3, StatCounter.counter);        // 2 x static + 1 x instance
		bigFatJoe.publicMethod();
		assertEquals(4, StatCounter.counter);
		bigFatJoe.callInnerMethods();
		assertEquals(7, StatCounter.counter);        // private method is not overriden

		bigFatJoe.superPublicMethod();
		assertEquals(8, StatCounter.counter);
		bigFatJoe.callInnerMethods2();
		assertEquals(9, StatCounter.counter);        // only public super methods are overriden


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
		assertEquals("value", a.value());

		PetiteInject pi = (PetiteInject) aa[1];
		assertEquals("", pi.value());

		Transaction tx = (Transaction) aa[2];
		assertTrue(tx.readOnly());
		assertEquals(1000, tx.timeout());
		assertEquals("PROPAGATION_REQUIRES_NEW", tx.propagation());

		bigFatJoe.runInnerClass();
		assertEquals(11, StatCounter.counter);        // proxy + call

	}
}
