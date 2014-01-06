// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.ReflectionReplacementAdvice;
import jodd.proxetta.data.Retro;
import jodd.proxetta.data.ReturnNullAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.pointcuts.AllMethodsPointcut;
import org.junit.Test;
import static org.junit.Assert.*;

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
