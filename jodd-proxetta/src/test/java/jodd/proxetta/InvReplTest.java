// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.io.FastByteArrayOutputStream;
import jodd.JoddProxetta;
import jodd.proxetta.impl.InvokeProxetta;
import jodd.proxetta.inv.*;
import jodd.util.ClassLoaderUtil;
import org.junit.Test;

import java.io.PrintStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class InvReplTest {

	@Test
	public void testReplacement() throws IllegalAccessException, InstantiationException, NoSuchMethodException {

		InvokeProxetta proxetta = initProxetta();

		String className = One.class.getCanonicalName();
		byte klazz[] = proxetta.builder(One.class).create();
		//FileUtil.writeBytes("d:\\OneClone.class", klazz);

		FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
//		PrintStream out = System.out;
		System.setOut(new PrintStream(fbaos));

		One one = (One) ClassLoaderUtil.defineClass((new StringBuilder()).append(className).append(JoddProxetta.invokeProxyClassNameSuffix).toString(), klazz).newInstance();
		assertEquals("one ctor!one ctor!", fbaos.toString());    // clone ctor calls super ctor,
		fbaos.reset();

		one.example1();
		assertEquals("REPLACED VIRTUAL! jodd.proxetta.inv.Two * one!173>overriden sub", fbaos.toString());
		fbaos.reset();

		one.example2();
		assertEquals("REPLACED STATIC! one * jodd/proxetta/inv/Two * example2 * void example2() * jodd.proxetta.inv.One * jodd.proxetta.inv.One$$Clonetou!15013static: 4", fbaos.toString());
		fbaos.reset();

		one.example3();
		assertEquals("state = REPLACED ctor!", fbaos.toString());
		fbaos.reset();

		assertEquals("jodd.proxetta.inv.One$$Clonetou", one.getClass().getName());
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
			fail();
		} catch (ProxettaException ignore) {

		}
	}

	@Test
	public void testInterface() {
		InvokeProxetta proxetta = initProxetta();
		try {
			proxetta.builder(Inter.class).newInstance();
			fail();
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
		InvokeProxetta fp = InvokeProxetta.withAspects(
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
		return fp;
	}
}
