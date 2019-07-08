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

import jodd.madvoc.MadvocTestCase;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.madvoc.interceptor.AnnotatedPropertyInterceptor;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.LogEchoInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;
import jodd.petite.PetiteContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class InterceptorsManagerTest extends MadvocTestCase {

	@SuppressWarnings({"unchecked"})
	@Test
	void testExpand_nothing() {
		InterceptorsManager im = new InterceptorsManager();

		Class<? extends ActionInterceptor>[] in = new Class[] {
			EchoInterceptor.class,
			ServletConfigInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertArrayEquals(new Class[] {EchoInterceptor.class, ServletConfigInterceptor.class}, out);
	}

	@SuppressWarnings({"unchecked"})
	@Test
	void testExpand_stacks() {
		InterceptorsManager im =createInterceptorManager();

		Class<? extends ActionInterceptor>[] in = new Class[] {
				AnnotatedPropertyInterceptor.class,
				TestStack3.class,
				EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);

		assertArrayEquals(new Class[] {
			AnnotatedPropertyInterceptor.class,
			EchoInterceptor.class,
			LogEchoInterceptor.class,
			ServletConfigInterceptor.class,
			EchoInterceptor.class,
		}, out);
	}

	@SuppressWarnings({"unchecked"})
	@Test
	void testExpandStack() {
		InterceptorsManager im = createInterceptorManager();

		Class<? extends ActionInterceptor>[] in = new Class[]{
			TestStack.class,
			TestStack3.class,
			EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);

		assertArrayEquals(new Class[] {
			AnnotatedPropertyInterceptor.class,
			LogEchoInterceptor.class,
			EchoInterceptor.class,
			LogEchoInterceptor.class,
			ServletConfigInterceptor.class,
			EchoInterceptor.class
		}, out);
	}


	@SuppressWarnings({"unchecked"})
	@Test
	void testExpandStack2() {
		InterceptorsManager im = createInterceptorManager();

		Class<? extends ActionInterceptor>[] in = new Class[]{
			TestConfigurableStack2.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);

		assertArrayEquals(new Class[] {
			EchoInterceptor.class,
			ServletConfigInterceptor.class,
			AnnotatedPropertyInterceptor.class,
			LogEchoInterceptor.class,
		}, out);
	}


	// ---------------------------------------------------------------- util

	private InterceptorsManager createInterceptorManager() {
		PetiteContainer madpc = new PetiteContainer();
		madpc.addSelf("madpc");
		madpc.addBean("madvocEncoding", new MadvocEncoding());

		InterceptorsManager im = new InterceptorsManager();
		im.contextInjectorComponent = new ContextInjectorComponent();
		im.contextInjectorComponent.madvocController = new MadvocController();
		im.contextInjectorComponent.madvocController.servletContextProvider = new ServletContextProvider(null);
		im.contextInjectorComponent.scopeDataInspector = new ScopeDataInspector();
		im.contextInjectorComponent.scopeResolver = new ScopeResolver();
		im.contextInjectorComponent.scopeResolver.madpc = madpc;
		return im;
	}


	// ---------------------------------------------------------------- stack

	public static class TestStack extends ActionInterceptorStack {
		@SuppressWarnings({"unchecked"})
		public TestStack() {
			super(TestStack2.class);
		}
	}

	public static class TestStack2 extends ActionInterceptorStack {
		@SuppressWarnings({"unchecked"})
		public TestStack2() {
			super(AnnotatedPropertyInterceptor.class, LogEchoInterceptor.class);
		}
	}

	public static class TestStack3 extends ActionInterceptorStack {
		public TestStack3() {
			super(
				EchoInterceptor.class,
				LogEchoInterceptor.class,
				ServletConfigInterceptor.class
			);
		}
	}
	public static class TestConfigurableStack2 extends ActionInterceptorStack {
		public TestConfigurableStack2() {
			super(EchoInterceptor.class, ServletConfigInterceptor.class, TestStack2.class);
		}

	}
}
