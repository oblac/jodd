// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.Inter;
import junit.framework.TestCase;
import jodd.proxetta.data.FooProxyAdvice;
import jodd.proxetta.data.Abstra;
import jodd.proxetta.data.Abstra2;

public class AbstractsTest extends TestCase {

	public void testAbstract1() {
		ProxyAspect proxyAspect = new ProxyAspect(FooProxyAdvice.class, new ProxyPointcut() {
			public boolean apply(MethodInfo methodInfo) {
				return true;
			}
		});

		Abstra aaa = Proxetta.withAspects(proxyAspect).createProxyInstance(Abstra.class);
		assertNotNull(aaa);
		aaa.foo();
	}

	public void testAbstract2() {
		ProxyAspect proxyAspect = new ProxyAspect(FooProxyAdvice.class, new ProxyPointcut() {
			public boolean apply(MethodInfo methodInfo) {
				return true;
			}
		});

		try {
			Proxetta.withAspects(proxyAspect).createProxyInstance(Abstra2.class);
			fail();
		} catch (ProxettaException ignore) {
		}
	}

	public void testInterface() {
		ProxyAspect proxyAspect = new ProxyAspect(FooProxyAdvice.class, new ProxyPointcut() {
			public boolean apply(MethodInfo methodInfo) {
				return true;
			}
		});

		try {
			Proxetta.withAspects(proxyAspect).createProxyInstance(Inter.class);
			fail();
		} catch (ProxettaException ignore) {
		}

	}
}
