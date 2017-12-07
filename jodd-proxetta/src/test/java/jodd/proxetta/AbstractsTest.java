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

import jodd.proxetta.fixtures.data.Abstra;
import jodd.proxetta.fixtures.data.Abstra2;
import jodd.proxetta.fixtures.data.Foo;
import jodd.proxetta.fixtures.data.FooProxyAdvice;
import jodd.proxetta.fixtures.data.Inter;
import jodd.proxetta.fixtures.data.InvalidAdvice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class AbstractsTest {

	@Test
	void testAbstract1() {
		ProxyAspect proxyAspect = new ProxyAspect(FooProxyAdvice.class, methodInfo -> true);

		Abstra aaa = (Abstra) Proxetta.proxyProxetta().withAspect(proxyAspect).proxy().setTarget(Abstra.class).newInstance();
		assertNotNull(aaa);
		aaa.foo();
	}

	@Test
	void testAbstract2() {
		ProxyAspect proxyAspect = new ProxyAspect(FooProxyAdvice.class, methodInfo -> true);

		try {
			Proxetta.proxyProxetta().withAspect(proxyAspect).proxy().setTarget(Abstra2.class).newInstance();
			fail("error");
		} catch (ProxettaException ignore) {
		}
	}

	@Test
	void testInterface() {
		ProxyAspect proxyAspect = new ProxyAspect(FooProxyAdvice.class, methodInfo -> true);

		try {
			Proxetta.proxyProxetta().withAspects(proxyAspect).proxy().setTarget(Inter.class).newInstance();
			fail("error");
		} catch (ProxettaException ignore) {
		}
	}

	@Test
	void testAdviceWithInnerClass() {
		ProxyAspect proxyAspect = new ProxyAspect(InvalidAdvice.class, methodInfo -> true);

		try {
			Proxetta.proxyProxetta().withAspect(proxyAspect).proxy().setTarget(Foo.class).newInstance();
			fail("error");
		} catch (ProxettaException ignore) {
			System.out.println(ignore);
		}

	}
}
