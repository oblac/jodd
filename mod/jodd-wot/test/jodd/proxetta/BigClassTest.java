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
import jodd.petite.meta.PetiteBean;
import jodd.jtx.meta.Transaction;
import jodd.jtx.JtxPropagationBehavior;
import jodd.mutable.MutableBoolean;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import org.objectweb.asm.Type;

public class BigClassTest extends TestCase {

	public void testAllFeatures() throws IOException, IllegalAccessException, InstantiationException {

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
					assertSame(ai, lookupAnnotation(ci, MadvocAction.class));
					assertEquals("jodd.madvoc.meta.MadvocAction", ai.getAnnotationClassname());
					assertEquals("Ljodd/madvoc/meta/MadvocAction;", ai.getAnnotationSignature());
					assertEquals("madvocAction", ai.getElement("value"));
					ai = anns[1];
					assertSame(ai, lookupAnnotation(ci, PetiteBean.class));
					assertEquals("jodd.petite.meta.PetiteBean", ai.getAnnotationClassname());
					assertEquals("Ljodd/petite/meta/PetiteBean;", ai.getAnnotationSignature());
					assertTrue(ai.getElement("wiring") instanceof String[]);
					String[] w = (String[]) ai.getElement("wiring");
					assertEquals("Ljodd/petite/WiringMode;", w[0]);
					assertEquals("OPTIONAL", w[1]);
					ai = anns[2];
					assertSame(ai, lookupAnnotation(ci, InterceptedBy.class));
					assertEquals("jodd.madvoc.meta.InterceptedBy", ai.getAnnotationClassname());
					assertEquals("Ljodd/madvoc/meta/InterceptedBy;", ai.getAnnotationSignature());
					assertTrue(ai.getElement("value") instanceof Object[]);
					assertFalse(ai.getElement("value") instanceof String[]);
					Object c1 = ((Object[]) ai.getElement("value"))[0];
					assertEquals("Ljodd/madvoc/interceptor/EchoInterceptor;", ((Type) c1).getDescriptor());
				}
				if (mi.getMethodName().equals("publicMethod")) {
					AnnotationInfo[] anns = mi.getAnnotations();
					assertNotNull(anns);
					assertEquals(3, anns.length);

					AnnotationInfo ai = anns[0];
					assertSame(ai, lookupAnnotation(mi, Action.class));
					assertEquals("jodd.madvoc.meta.Action", ai.getAnnotationClassname());
					assertEquals("value", ai.getElement("value"));
					assertEquals(Boolean.TRUE, ai.getElement("notInPath"));
					assertEquals("alias", ai.getElement("alias"));

					ai = anns[1];
					assertSame(ai, lookupAnnotation(mi, PetiteInject.class));
					assertEquals("jodd.petite.meta.PetiteInject", ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());

					ai = anns[2];
					assertSame(ai, lookupAnnotation(mi, Transaction.class));
					assertEquals("jodd.jtx.meta.Transaction", ai.getAnnotationClassname());
					assertEquals(2, ai.getElementNames().size());
					String[] s = (String[]) ai.getElement("propagation");
					assertEquals("Ljodd/jtx/JtxPropagationBehavior;", s[0]);
					assertEquals("PROPAGATION_REQUIRES_NEW", s[1]);
				}
				if (mi.getMethodName().equals("superPublicMethod")) {
					AnnotationInfo[] anns = mi.getAnnotations();
					assertNotNull(anns);
					assertEquals(3, anns.length);

					AnnotationInfo ai = anns[0];
					assertSame(ai, lookupAnnotation(mi, Action.class));
					assertEquals("jodd.madvoc.meta.Action", ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());

					ai = anns[1];
					assertSame(ai, lookupAnnotation(mi, PetiteInject.class));
					assertEquals("jodd.petite.meta.PetiteInject", ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());

					ai = anns[2];
					assertSame(ai, lookupAnnotation(mi, Transaction.class));
					assertEquals("jodd.jtx.meta.Transaction", ai.getAnnotationClassname());
					assertEquals(0, ai.getElementNames().size());
				}
				System.out.println(!isRootMethod(mi) + " " + mi.getDeclaredClassName() + '#' + mi.getMethodName());
				return !isRootMethod(mi);
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
