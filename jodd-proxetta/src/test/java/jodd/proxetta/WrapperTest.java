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

import jodd.proxetta.fixtures.data.*;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class WrapperTest {

	@BeforeEach
	public void setUp() throws Exception {
		StatCounter.counter = 0;
	}

	@Test
	public void testClassWrapper() throws Exception {
		Calc calc = new CalcImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return !methodInfo.isRootMethod() && methodInfo.isPublicMethod();
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		// wrapper over CLASS
		// resulting object has ALL interfaces
		// resulting object wraps ALL target class methods
		WrapperProxettaBuilder builder = proxetta.builder(calc.getClass());

		Class calc2Class = builder.define();

		Object object = calc2Class.newInstance();

		assertTrue(object instanceof Calc);
		assertEquals(CalcImpl.class, ProxettaUtil.getTargetClass(object.getClass()));
		assertEquals(1, calc2Class.getInterfaces().length);

		builder.injectTargetIntoWrapper(calc, object);

		assertEquals(1, StatCounter.counter);    // counter in static block !!!

		Method method = calc2Class.getMethod("hello");
		assertNotNull(method);
		method.invoke(object);

		assertEquals(2, StatCounter.counter);

		method = calc2Class.getMethod("calculate", int.class, int.class);
		assertNotNull(method);
		Integer result = (Integer) method.invoke(object, 3, 7);
		assertEquals(10, result.intValue());

		assertEquals(3, StatCounter.counter);

		assertNotNull(calc2Class.getMethod("customMethod"));
	}

	@Test
	public void testClassWrapperCastToInterface() throws Exception {
		Calc calc = new CalcImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return !methodInfo.isRootMethod() && methodInfo.isPublicMethod();
			}
		}));

		//proxetta.setDebugFolder("/Users/igor");

		// wrapper over CLASS casted to interface,
		// resulting object has ONE interface
		// ALL target methods are wrapped
		WrapperProxettaBuilder builder = proxetta.builder(calc.getClass(), Calc.class, ".CalcImpl2");

		Class<Calc> calc2Class = builder.define();

		Calc calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);    // counter in static block !!!

		calc2.hello();

		assertEquals(2, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(3, StatCounter.counter);

		assertNotNull(calc2Class.getMethod("customMethod"));
	}

	@Test
	public void testInterfaceWrapper() throws Exception {
		Calc calc = new CalcImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return methodInfo.isTopLevelMethod() && methodInfo.isPublicMethod();
			}
		}));

		//proxetta.setDebugFolder("/Users/igor");

		// wrapper over INTERFACE
		// resulting object has ONE interface
		// only interface methods are wrapped
		WrapperProxettaBuilder builder = proxetta.builder(Calc.class, ".CalcImpl3");

		Class<Calc> calc2Class = builder.define();

		Calc calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);    // counter in static block !!!

		calc2.hello();

		assertEquals(2, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(3, StatCounter.counter);

		try {
			calc2Class.getMethod("customMethod");
			fail("error");
		} catch (Exception ex) {
		}
	}


	@Test
	public void testPartialMethodsWrapped() throws Exception {

		Calc calc = new CalcSuperImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return
						methodInfo.isPublicMethod() &&
								(methodInfo.getMethodName().equals("hello") || methodInfo.getMethodName().equals("ola"));
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		WrapperProxettaBuilder builder = proxetta.builder(CalcSuper.class);

		Class<CalcSuper> calc2Class = builder.define();

		CalcSuper calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);    // counter in static block !!!

		calc2.hello();

		assertEquals(2, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(2, StatCounter.counter);        // counter not called in calculate!

		calc2.ola();

		assertEquals(3, StatCounter.counter);

		calc2.superhi();
		calc2.maybe(4, 5);
		calc2.calculate(4, 5);

		assertEquals(3, StatCounter.counter);
	}

	@Test
	public void testNoPointcutMatched() throws Exception {

		Calc calc = new CalcSuperImpl();

		WrapperProxetta proxetta = WrapperProxetta.withAspects(new ProxyAspect(StatCounterAdvice.class, new ProxyPointcutSupport() {
			public boolean apply(MethodInfo methodInfo) {
				return false;
			}
		}));

//		proxetta.setDebugFolder("d:\\");

		WrapperProxettaBuilder builder = proxetta.builder(CalcSuper.class, ".CalcSuper22");

		Class<CalcSuper> calc2Class = builder.define();

		CalcSuper calc2 = calc2Class.newInstance();

		builder.injectTargetIntoWrapper(calc, calc2);

		assertEquals(1, StatCounter.counter);    // counter in static block !!!

		calc2.hello();

		assertEquals(1, StatCounter.counter);

		assertEquals(10, calc2.calculate(3, 7));

		assertEquals(1, StatCounter.counter);        // counter not called in calculate!

		calc2.ola();

		assertEquals(1, StatCounter.counter);

		calc2.superhi();
		calc2.maybe(4, 5);
		calc2.calculate(4, 5);

		assertEquals(1, StatCounter.counter);
	}

}
