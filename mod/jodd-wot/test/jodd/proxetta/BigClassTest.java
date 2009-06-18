// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import junit.framework.TestCase;
import jodd.proxetta.data.BigFatJoe;
import jodd.proxetta.data.StatCounter;
import jodd.proxetta.data.StatCounterAdvice;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;
import jodd.io.FileUtil;
import jodd.util.ClassLoaderUtil;

import java.io.IOException;

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

		assertEquals(2, StatCounter.counter);		// 2 x static
		bigFatJoe.publicMethod();
		assertEquals(3, StatCounter.counter);
		bigFatJoe.callInnerMethods();
		assertEquals(6, StatCounter.counter);		// private method is not overriden

		bigFatJoe.superPublicMethod();
		assertEquals(7, StatCounter.counter);
		bigFatJoe.callInnerMethods2();
		assertEquals(8, StatCounter.counter);		// only public super methods are overriden

		
	}
}
