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

import jodd.proxetta.fixtures.data.ReflectionReplacementAdvice;
import jodd.proxetta.fixtures.data.Retro;
import jodd.proxetta.fixtures.data.ReturnNullAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.pointcuts.AllMethodsPointcut;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;


public class ReturnTest {

	@Test
	public void testWrapperWithProxyReturns() throws Exception {
		ProxyProxetta proxetta = ProxyProxetta.withAspects(
				new ProxyAspect(ReflectionReplacementAdvice.class, new AllMethodsPointcut()));

		ProxyProxettaBuilder builder = proxetta.builder(Retro.class, ".Retro2");

//		proxetta.setDebugFolder("d:\\");

		Class proxyClass = builder.define();

		Object proxy = proxyClass.newInstance();

		Field field = proxyClass.getField("$__target$0");

		Retro retro = new Retro();

		retro.flag = true;

		field.set(proxy, retro);

		retro = (Retro) proxy;

		assertNotNull(retro);

		assertEquals("retro", retro.method1());
		assertEquals(2, retro.method2());
		assertEquals(3, retro.method3());
		assertEquals(4, retro.method4());
		assertEquals(5, retro.method5());
		assertEquals(true, retro.method6());
		assertEquals(7.7, retro.method7(), 0.005);
		assertEquals(8.8, retro.method8(), 0.005);
		assertEquals(9, retro.method9().length);
		assertEquals('r', retro.method11());

		retro.method10();
	}

	@Test
	public void testNullReturns() throws Exception {
		ProxyProxetta proxetta = ProxyProxetta.withAspects(
				new ProxyAspect(ReturnNullAdvice.class, new AllMethodsPointcut()));

		ProxyProxettaBuilder builder = proxetta.builder(Retro.class, ".Retro3");

//		proxetta.setDebugFolder("d:\\");

		Class proxyClass = builder.define();

		Object proxy = proxyClass.newInstance();

		Retro retro = (Retro) proxy;

		assertNotNull(retro);

		assertNull(retro.method1());
		assertEquals(0, retro.method2());
		assertEquals(0, retro.method3());
		assertEquals(0, retro.method4());
		assertEquals(0, retro.method5());
		assertEquals(false, retro.method6());
		assertEquals(0, retro.method7(), 0.005);
		assertEquals(0, retro.method8(), 0.005);
		assertNull(retro.method9());
		assertEquals(0, retro.method11());

		retro.method10();
	}

}
