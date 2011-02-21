// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocTestCase;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.interceptor.ActionInterceptorStack;
import jodd.madvoc.interceptor.AnnotatedFieldsInterceptor;
import jodd.madvoc.interceptor.DefaultWebAppInterceptors;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.interceptor.LogEchoInterceptor;
import jodd.madvoc.interceptor.ServletConfigInterceptor;

public class InterceptorManagerTest extends MadvocTestCase {

	@SuppressWarnings({"unchecked"})
	public void testExpand() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[] {ServletConfigInterceptor.class};

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
	public void testExpand2() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[] {EchoInterceptor.class, LogEchoInterceptor.class, ServletConfigInterceptor.class};

		Class<? extends ActionInterceptor>[] in = new Class[] {
				AnnotatedFieldsInterceptor.class,
				DefaultWebAppInterceptors.class,
				EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(5, out.length);
		assertEquals(AnnotatedFieldsInterceptor.class, out[0]);
		assertEquals(EchoInterceptor.class, out[1]);
		assertEquals(LogEchoInterceptor.class, out[2]);
		assertEquals(ServletConfigInterceptor.class, out[3]);
		assertEquals(EchoInterceptor.class, out[4]);
	}

	@SuppressWarnings({"unchecked"})
	public void testExpandStack() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[] {EchoInterceptor.class, ServletConfigInterceptor.class};

		Class<? extends ActionInterceptor>[] in = new Class[] {
				TestStack.class,
				DefaultWebAppInterceptors.class,
				EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(7, out.length);
		assertEquals(AnnotatedFieldsInterceptor.class, out[0]);
		assertEquals(LogEchoInterceptor.class, out[1]);

		assertEquals(EchoInterceptor.class, out[2]);
		assertEquals(ServletConfigInterceptor.class, out[3]);

		assertEquals(EchoInterceptor.class, out[4]);
		assertEquals(ServletConfigInterceptor.class, out[5]);

		assertEquals(EchoInterceptor.class, out[6]);
	}

	@SuppressWarnings({"unchecked"})
	public void testExpandSelf() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();

		im.madvocConfig.setDefaultInterceptors(new Class[]{
				EchoInterceptor.class,
				DefaultWebAppInterceptors.class	// cyclic dependency
		});

		Class<? extends ActionInterceptor>[] in = new Class[]{
				EchoInterceptor.class,
				DefaultWebAppInterceptors.class
		};
		try {
			Class<? extends ActionInterceptor>[] out = im.expand(in);
			fail();
		} catch (MadvocException ignore) {
		} catch (Exception ignored) {
			fail();
		}
	}


	@SuppressWarnings({"unchecked"})
	public void testExpandStack2() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[] {EchoInterceptor.class, ServletConfigInterceptor.class, Test2Stack.class};

		Class<? extends ActionInterceptor>[] in = new Class[] {
				DefaultWebAppInterceptors.class,
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(4, out.length);
		assertEquals(EchoInterceptor.class, out[0]);
		assertEquals(ServletConfigInterceptor.class, out[1]);

		assertEquals(AnnotatedFieldsInterceptor.class, out[2]);
		assertEquals(LogEchoInterceptor.class, out[3]);

	}

	public static class TestStack extends ActionInterceptorStack {
		@SuppressWarnings( {"unchecked"})
		public TestStack() {
			super(new Class[] {Test2Stack.class, DefaultWebAppInterceptors.class});
		}
	}

	public static class Test2Stack extends ActionInterceptorStack {
		@SuppressWarnings( {"unchecked"})
		public Test2Stack() {
			super(new Class[] {AnnotatedFieldsInterceptor.class, LogEchoInterceptor.class});
		}
	}

}
