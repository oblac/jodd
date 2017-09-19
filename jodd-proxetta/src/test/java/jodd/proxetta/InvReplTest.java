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

import jodd.io.FastByteArrayOutputStream;
import jodd.proxetta.impl.InvokeProxetta;
import jodd.proxetta.fixtures.inv.*;
import jodd.util.ClassLoaderUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class InvReplTest {

	@Test
	public void testReplacement() throws IllegalAccessException, InstantiationException, NoSuchMethodException, IOException {

		InvokeProxetta proxetta = initProxetta();

		String className = One.class.getCanonicalName();
		byte[] klazz = proxetta.builder(One.class).create();
		//FileUtil.writeBytes("/Users/igor/OneClone.class", klazz);

		FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
//		PrintStream out = System.out;
		System.setOut(new PrintStream(fbaos));

		One one = (One) ClassLoaderUtil.defineClass((new StringBuilder()).append(className).append(JoddProxetta.invokeProxyClassNameSuffix).toString(), klazz).newInstance();
		assertEquals("one ctor!one ctor!", fbaos.toString());    // clone ctor calls super ctor,
		fbaos.reset();

		one.example1();
		assertEquals("REPLACED VIRTUAL! jodd.proxetta.fixtures.inv.Two * one!173>overriden sub", fbaos.toString());
		fbaos.reset();

		one.example2();
		assertEquals("REPLACED STATIC! one * jodd/proxetta/fixtures/inv/Two * example2 * void example2() * jodd.proxetta.fixtures.inv.One * jodd.proxetta.fixtures.inv.One$$Clonetou!15013static: 4", fbaos.toString());
		fbaos.reset();

		one.example3();
		assertEquals("state = REPLACED ctor!", fbaos.toString());
		fbaos.reset();

		assertEquals("jodd.proxetta.fixtures.inv.One$$Clonetou", one.getClass().getName());
		assertTrue(one instanceof Serializable);

		Annotation[] anns = one.getClass().getAnnotations();
		assertEquals(3, anns.length);

		Method ms = one.getClass().getMethod("example1");
		anns = ms.getAnnotations();
		assertEquals(1, anns.length);

	}

	@Test
	public void testSuper() {
		InvokeProxetta proxetta = initProxetta();
		try {
			proxetta.builder(OneWithSuper.class).define();
			fail("error");
		} catch (ProxettaException ignore) {

		}
	}

	@Test
	public void testInterface() {
		InvokeProxetta proxetta = initProxetta();
		try {
			proxetta.builder(Inter.class).newInstance();
			fail("error");
		} catch (ProxettaException ignore) {
		}
	}

	@Test
	public void testCurrentTimeMillis() {
		TimeClass timeClass = (TimeClass) InvokeProxetta.withAspects(new InvokeAspect() {
			@Override
			public boolean apply(MethodInfo methodInfo) {
				return methodInfo.isTopLevelMethod();
			}

			@Override
			public InvokeReplacer pointcut(InvokeInfo invokeInfo) {
				if (
						invokeInfo.getClassName().equals("java.lang.System") &&
						invokeInfo.getMethodName().equals("currentTimeMillis")
					) {
					return InvokeReplacer.with(MySystem.class, "currentTimeMillis");
				}
				return null;
			}
		}).builder(TimeClass.class).newInstance();

		long time = timeClass.time();

		assertEquals(10823, time);
	}

	@Test
	public void testWimp() {
		Wimp wimp = (Wimp) InvokeProxetta.withAspects(new InvokeAspect() {
			@Override
			public boolean apply(MethodInfo methodInfo) {
				return methodInfo.isTopLevelMethod();
			}

			@Override
			public InvokeReplacer pointcut(InvokeInfo invokeInfo) {
				return InvokeReplacer.NONE;
			}
		}).builder(Wimp.class).newInstance();

		int i = wimp.foo();
		assertEquals(0, i);

		String txt = wimp.aaa(3, null, null);
		assertEquals("int3WelcomeToJodd", txt);

		txt = wimp.ccc(3, "XXX", 1, null);
		assertEquals(">4:String:4long:4XXX:ccc:Wimp", txt);
	}


	protected InvokeProxetta initProxetta() {
		return InvokeProxetta.withAspects(
				new InvokeAspect() {
					@Override
					public InvokeReplacer pointcut(InvokeInfo invokeInfo) {
						if (invokeInfo.getMethodName().equals("invvirtual")) {
							return InvokeReplacer.with(Replacer.class, "rInvVirtual")
									.passOwnerName(false);
						} else {
							return null;
						}
					}
				}
				, new InvokeAspect() {
					@Override
					public InvokeReplacer pointcut(InvokeInfo invokeInfo) {
						if (invokeInfo.getMethodName().equals("invstatic")) {
							return InvokeReplacer.with(Replacer.class, "rInvStatic")
									.passOwnerName(true)
									.passMethodName(true)
									.passMethodSignature(true)
									.passThis(true)
									.passTargetClass(true);
						} else {
							return null;
						}
					}
				}
				, new InvokeAspect() {
					@Override
					public InvokeReplacer pointcut(InvokeInfo invokeInfo) {
						if (invokeInfo.getMethodName().equals("invinterface")) {
							return InvokeReplacer.with(Replacer.class, "rInvInterface");
						} else {
							return null;
						}
					}
				}
				, new InvokeAspect() {
					@Override
					public InvokeReplacer pointcut(InvokeInfo invokeInfo) {
						if (invokeInfo.getMethodName().equals("<init>") && invokeInfo.getClassName().equals(Two.class.getCanonicalName())) {
							return InvokeReplacer.with(Replacer.class, "rInvNew");
						} else {
							return null;
						}
					}
				}
		);
	}
}
