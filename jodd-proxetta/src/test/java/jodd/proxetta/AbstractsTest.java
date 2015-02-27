// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.Abstra;
import jodd.proxetta.data.Abstra2;
import jodd.proxetta.data.Foo;
import jodd.proxetta.data.FooProxyAdvice;
import jodd.proxetta.data.Inter;
import jodd.proxetta.data.InvalidAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AbstractsTest {

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testAdviceWithInnerClass() {
		ProxyAspect proxyAspect = new ProxyAspect(InvalidAdvice.class, new ProxyPointcut() {
			public boolean apply(MethodInfo methodInfo) {
				return true;
			}
		});

		try {
			ProxyProxetta.withAspects(proxyAspect).builder(Foo.class).newInstance();
			fail();
		} catch (ProxettaException ignore) {
			System.out.println(ignore);
		}

	}
}
