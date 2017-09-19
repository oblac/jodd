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

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocTestCase;
import jodd.madvoc.interceptor.*;
import jodd.petite.PetiteContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterceptorsManagerTest extends MadvocTestCase {

	@SuppressWarnings({"unchecked"})
	@Test
	public void testExpand() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[]{ServletConfigInterceptor.class};

		Class<? extends ActionInterceptor>[] in = new Class[]{
				EchoInterceptor.class,
				DefaultWebAppInterceptors.class
		};
		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(2, out.length);
		assertEquals(EchoInterceptor.class, out[0]);
		assertEquals(ServletConfigInterceptor.class, out[1]);
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testExpand2() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[]{EchoInterceptor.class, LogEchoInterceptor.class, ServletConfigInterceptor.class};

		Class<? extends ActionInterceptor>[] in = new Class[]{
				AnnotatedPropertyInterceptor.class,
				DefaultWebAppInterceptors.class,
				EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(5, out.length);
		assertEquals(AnnotatedPropertyInterceptor.class, out[0]);
		assertEquals(EchoInterceptor.class, out[1]);
		assertEquals(LogEchoInterceptor.class, out[2]);
		assertEquals(ServletConfigInterceptor.class, out[3]);
		assertEquals(EchoInterceptor.class, out[4]);
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testExpandStack() {
		InjectorsManager injectorsManager = new InjectorsManager();
		injectorsManager.scopeDataResolver = new ScopeDataResolver();
		injectorsManager.madvocConfig = new MadvocConfig();
		injectorsManager.madpc = new PetiteContainer();
		injectorsManager.createInjectors();

		InterceptorsManager im = new InterceptorsManager();
		im.contextInjectorComponent = new ContextInjectorComponent();
		im.contextInjectorComponent.injectorsManager = injectorsManager;
		im.contextInjectorComponent.madvocController = new MadvocController();
		im.contextInjectorComponent.scopeDataResolver = injectorsManager.scopeDataResolver;
		im.contextInjectorComponent.madpc = injectorsManager.madpc;
		im.madvocConfig = injectorsManager.madvocConfig;
		im.madvocConfig.defaultInterceptors = new Class[]{EchoInterceptor.class, ServletConfigInterceptor.class};

		Class<? extends ActionInterceptor>[] in = new Class[]{
				TestStack.class,
				DefaultWebAppInterceptors.class,
				EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(7, out.length);
		assertEquals(AnnotatedPropertyInterceptor.class, out[0]);
		assertEquals(LogEchoInterceptor.class, out[1]);

		assertEquals(EchoInterceptor.class, out[2]);
		assertEquals(ServletConfigInterceptor.class, out[3]);

		assertEquals(EchoInterceptor.class, out[4]);
		assertEquals(ServletConfigInterceptor.class, out[5]);

		assertEquals(EchoInterceptor.class, out[6]);
	}
	
	
	@SuppressWarnings({"unchecked"})
	@Test
	public void testExpandConfigurableStack() {
		InjectorsManager injectorsManager = new InjectorsManager();
		injectorsManager.scopeDataResolver = new ScopeDataResolver();
		injectorsManager.madvocConfig = new MadvocConfig();
		injectorsManager.madpc = new PetiteContainer();

		InterceptorsManager im = new InterceptorsManager();
		im.contextInjectorComponent = new ContextInjectorComponent();
		im.contextInjectorComponent.injectorsManager = injectorsManager;
		im.contextInjectorComponent.madvocController = new MadvocController();
		im.contextInjectorComponent.scopeDataResolver = injectorsManager.scopeDataResolver;
		im.contextInjectorComponent.madpc = injectorsManager.madpc;
		im.madvocConfig = injectorsManager.madvocConfig;
		im.madvocConfig.defaultInterceptors = new Class[]{EchoInterceptor.class, ServletConfigInterceptor.class};

		injectorsManager.madpc.defineParameter(
				TestConfigurableStack.class.getName() + ".interceptors",
				AnnotatedPropertyInterceptor.class.getName() + "," +
				ServletConfigInterceptor.class.getName() + "," +
				LogEchoInterceptor.class.getName()
		);

		injectorsManager.createInjectors();

		Class<? extends ActionInterceptor>[] in = new Class[] {
			TestConfigurableStack.class,
			TestConfigurableStack2.class,
			EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(6, out.length);		// 3 + 2 + 1

		// assert: TestConfigurableStack => defined in madpc
		assertEquals(AnnotatedPropertyInterceptor.class, out[0]);
		assertEquals(ServletConfigInterceptor.class, out[1]);
		assertEquals(LogEchoInterceptor.class, out[2]);

		//assert: TestConfigurableStack2 => madvocConfig.defaultInterceptors
		assertEquals(EchoInterceptor.class, out[3]);
		assertEquals(ServletConfigInterceptor.class, out[4]);
		assertEquals(EchoInterceptor.class, out[5]);
	}
	
	
	@SuppressWarnings({"unchecked"})
	@Test
	public void testExpandSelf() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();

		im.madvocConfig.setDefaultInterceptors(new Class[]{
				EchoInterceptor.class,
				DefaultWebAppInterceptors.class    // cyclic dependency
		});

		Class<? extends ActionInterceptor>[] in = new Class[]{
				EchoInterceptor.class,
				DefaultWebAppInterceptors.class
		};
		try {
			Class<? extends ActionInterceptor>[] out = im.expand(in);
			fail("error");
		} catch (MadvocException ignore) {
		} catch (Exception ignored) {
			fail("error");
		}
	}


	@SuppressWarnings({"unchecked"})
	@Test
	public void testExpandStack2() {
		InjectorsManager injectorsManager = new InjectorsManager();
		injectorsManager.scopeDataResolver = new ScopeDataResolver();
		injectorsManager.madvocConfig = new MadvocConfig();
		injectorsManager.madpc = new PetiteContainer();
		injectorsManager.createInjectors();

		InterceptorsManager im = new InterceptorsManager();
		im.contextInjectorComponent = new ContextInjectorComponent();
		im.contextInjectorComponent.injectorsManager = injectorsManager;
		im.contextInjectorComponent.madvocController = new MadvocController();
		im.contextInjectorComponent.scopeDataResolver = injectorsManager.scopeDataResolver;
		im.contextInjectorComponent.madpc = injectorsManager.madpc;
		im.madvocConfig = injectorsManager.madvocConfig;
		im.madvocConfig.defaultInterceptors = new Class[]{EchoInterceptor.class, ServletConfigInterceptor.class, Test2Stack.class};

		Class<? extends ActionInterceptor>[] in = new Class[]{
				DefaultWebAppInterceptors.class,
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(4, out.length);
		assertEquals(EchoInterceptor.class, out[0]);
		assertEquals(ServletConfigInterceptor.class, out[1]);

		assertEquals(AnnotatedPropertyInterceptor.class, out[2]);
		assertEquals(LogEchoInterceptor.class, out[3]);

	}

	// ---------------------------------------------------------------- util

	public static class TestStack extends ActionInterceptorStack {
		@SuppressWarnings({"unchecked"})
		public TestStack() {
			super(new Class[]{Test2Stack.class, DefaultWebAppInterceptors.class});
		}
	}

	public static class Test2Stack extends ActionInterceptorStack {
		@SuppressWarnings({"unchecked"})
		public Test2Stack() {
			super(new Class[]{AnnotatedPropertyInterceptor.class, LogEchoInterceptor.class});
		}
	}
	
	public static class TestConfigurableStack extends ActionInterceptorStack {
	}

	public static class TestConfigurableStack2 extends ActionInterceptorStack {
		public TestConfigurableStack2() {
			super(DefaultWebAppInterceptors.class);
		}
	}
}
