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

import jodd.mutable.ValueHolder;
import jodd.mutable.ValueHolderWrapper;
import jodd.proxetta.fixtures.data.Foo;
import jodd.proxetta.fixtures.data.FooAnn;
import jodd.proxetta.fixtures.data.FooProxyAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MethodInfoTest {

	@Test
	public void testMethodInfo() {

		final ValueHolder<MethodInfo> valueHolder = ValueHolderWrapper.create();

		ProxyAspect proxyAspect = new ProxyAspect(
				FooProxyAdvice.class,
				new ProxyPointcut() {
					public boolean apply(MethodInfo methodInfo) {
						if (methodInfo.getMethodName().equals("p1")) {
							valueHolder.set(methodInfo);
							return true;
						}
						return false;
					}
		});

		ProxyProxetta proxyProxetta = ProxyProxetta.withAspects(proxyAspect);
		proxyProxetta.setClassNameSuffix("$$$Proxetta888");
		ProxyProxettaBuilder pb = proxyProxetta.builder();
		pb.setTarget(Foo.class);
		Foo foo = (Foo) pb.newInstance();

		assertNotNull(foo);

		MethodInfo mi = valueHolder.get();

		assertEquals("p1", mi.getMethodName());
		assertEquals(Foo.class.getName().replace('.', '/'), mi.getClassname());
		assertEquals("(java.lang.String)java.lang.String", mi.getDeclaration());
		assertEquals("(Ljava/lang/String;)Ljava/lang/String;", mi.getDescription());
		assertEquals("java.lang.String", mi.getReturnType().getType());
		assertEquals("Ljava/lang/String;", mi.getReturnType().getName());

		assertEquals("java.lang.String p1(java.lang.String)", mi.getSignature());

		assertEquals(1, mi.getArgumentsCount());
		assertEquals("Ljava/lang/String;", mi.getArgument(1).getName());

		assertTrue(mi.isTopLevelMethod());

		AnnotationInfo[] anns = mi.getArgument(1).getAnnotations();

		assertNotNull(anns);
		assertEquals(1, anns.length);
		assertEquals(FooAnn.class.getName(), anns[0].getAnnotationClassname());
	}

}
