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

import jodd.proxetta.fixtures.data.Transaction;
import jodd.proxetta.petite.fixtures.LogProxyAdvice;
import jodd.proxetta.pointcuts.MethodWithAnnotationPointcut;
import org.junit.jupiter.api.Test;
import service.Test568OpenService;
import service.Test568Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KotlinTest {

	@Test
	void testWrapperProxetta_unopen() throws InvocationTargetException, IllegalAccessException {
		ProxyAspect proxyAspect = ProxyAspect.of(LogProxyAdvice.class, MethodWithAnnotationPointcut.of(Transaction.class));

		final Object service =
			Proxetta.wrapperProxetta()
				.withAspect(proxyAspect)
				.setCreateTargetInDefaultCtor(true)
				.proxy()
				.setTarget(Test568Service.class)
				.newInstance();

		// no need to inject since setCreateTargetInstanceInDefaultCtor is set
//		ProxettaUtil.injectTargetIntoWrapper(new Test568Service(), service);

		Method method = jodd.util.ClassUtil.findMethod(service.getClass(), "findMember");

		String result = (String) method.invoke(service, Long.valueOf(1));

		assertEquals("1", result);
	}

	@Test
	void testProxyProxetta_open() {
		ProxyAspect proxyAspect = ProxyAspect.of(LogProxyAdvice.class, MethodWithAnnotationPointcut.of(Transaction.class));
		Test568OpenService service =
			(Test568OpenService) Proxetta.proxyProxetta()
				.withAspect(proxyAspect)
				.proxy()
				.setTarget(Test568OpenService.class)
				.newInstance();

		String result = service.findMember(2L);

		assertEquals("2", result);
	}
}
