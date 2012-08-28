// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.Inter;
import jodd.proxetta.impl.ProxyProxetta;
import junit.framework.TestCase;
import jodd.proxetta.data.FooProxyAdvice;
import jodd.proxetta.data.Abstra;
import jodd.proxetta.data.Abstra2;

public class AbstractsTest extends TestCase {

	public void testAbstract1() throws Exception {
		ProxyAspect proxyAspect = new ProxyAspect(FooProxyAdvice.class, new ProxyPointcut() {
			public boolean apply(MethodInfo methodInfo) {
				return true;
			}
		});

		Abstra aaa = (Abstra) ProxyProxetta.withAspects(proxyAspect).builder(Abstra.class).newInstance();
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
			ProxyProxetta.withAspects(proxyAspect).builder(Abstra2.class).newInstance();
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
			ProxyProxetta.withAspects(proxyAspect).builder(Inter.class).newInstance();
			fail();
		} catch (ProxettaException ignore) {
		}

	}
}
