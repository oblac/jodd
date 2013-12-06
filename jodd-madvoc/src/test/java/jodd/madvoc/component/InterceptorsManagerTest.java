// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocTestCase;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.filter.EchoFilter;
import jodd.madvoc.interceptor.*;
import jodd.petite.PetiteContainer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

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
	@Test
	public void testExpandStack() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[]{EchoInterceptor.class, ServletConfigInterceptor.class};

		im.madvocContextInjector = new MadvocContextInjector();
		im.madvocContextInjector.madpc = new PetiteContainer();
		im.madvocContextInjector.createInjectors();

		Class<? extends ActionInterceptor>[] in = new Class[]{
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
	@Test
	public void testExpandConfigurableStack() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[]{EchoInterceptor.class, ServletConfigInterceptor.class};

		PetiteContainer madpc = new PetiteContainer();
		madpc.defineParameter(
				TestConfigurableStack.class.getName() + ".interceptors",
				AnnotatedFieldsInterceptor.class.getName() + "," +
				ServletConfigInterceptor.class.getName() + "," +
				LogEchoInterceptor.class.getName()
		);
		im.madvocContextInjector = new MadvocContextInjector();
		im.madvocContextInjector.madpc = madpc;
		im.madvocContextInjector.createInjectors();

		Class<? extends ActionInterceptor>[] in = new Class[] {
			TestConfigurableStack.class,
			TestConfigurableStack2.class,
			EchoInterceptor.class
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(6, out.length);		// 3 + 2 + 1

		// assert: TestConfigurableStack => defined in madpc
		assertEquals(AnnotatedFieldsInterceptor.class, out[0]);
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
			fail();
		} catch (MadvocException ignore) {
		} catch (Exception ignored) {
			fail();
		}
	}


	@SuppressWarnings({"unchecked"})
	@Test
	public void testExpandStack2() {
		InterceptorsManager im = new InterceptorsManager();
		im.madvocConfig = new MadvocConfig();
		im.madvocConfig.defaultInterceptors = new Class[]{EchoInterceptor.class, ServletConfigInterceptor.class, Test2Stack.class};

		im.madvocContextInjector = new MadvocContextInjector();
		im.madvocContextInjector.madpc = new PetiteContainer();
		im.madvocContextInjector.createInjectors();

		Class<? extends ActionInterceptor>[] in = new Class[]{
				DefaultWebAppInterceptors.class,
		};

		Class<? extends ActionInterceptor>[] out = im.expand(in);
		assertEquals(4, out.length);
		assertEquals(EchoInterceptor.class, out[0]);
		assertEquals(ServletConfigInterceptor.class, out[1]);

		assertEquals(AnnotatedFieldsInterceptor.class, out[2]);
		assertEquals(LogEchoInterceptor.class, out[3]);

	}

	@Test
	public void testExtractFilters() {
		EchoInterceptor inter = new EchoInterceptor();
		EchoFilter filter = new EchoFilter();
		InterceptorsManager interceptorsManager = new InterceptorsManager();

		ActionFilter[] filters = interceptorsManager.extractActionFilters(
				new ActionInterceptor[] {
					filter, filter, inter, inter
		});
		assertEquals(2, filters.length);
		assertSame(filter, filters[0]);
		assertSame(filter, filters[1]);

		filters = interceptorsManager.extractActionFilters(
				new ActionInterceptor[] {
					filter, inter, inter
		});
		assertEquals(1, filters.length);
		assertSame(filter, filters[0]);

		filters = interceptorsManager.extractActionFilters(
				new ActionInterceptor[] {
					inter, inter
		});
		assertEquals(0, filters.length);

		try {
			interceptorsManager.extractActionFilters(
					new ActionInterceptor[] {
						filter, inter, filter, inter
			});
			fail();
		} catch (MadvocException ignore) {
		}

		try {
			interceptorsManager.extractActionFilters(
					new ActionInterceptor[] {
						inter, inter, filter, inter
			});
			fail();
		} catch (MadvocException ignore) {
		}

		try {
			interceptorsManager.extractActionFilters(
					new ActionInterceptor[] {
						inter, filter
			});
			fail();
		} catch (MadvocException ignore) {
		}

	}

	@Test
	public void testExtractInterceptors() {
		EchoInterceptor inter = new EchoInterceptor();
		EchoFilter filter = new EchoFilter();
		InterceptorsManager interceptorsManager = new InterceptorsManager();

		ActionInterceptor[] interceptors = interceptorsManager.extractActionInterceptors(
				new ActionInterceptor[] {
						filter, filter, inter, inter
				});
		assertEquals(2, interceptors.length);
		assertSame(inter, interceptors[0]);
		assertSame(inter, interceptors[1]);

		interceptors = interceptorsManager.extractActionInterceptors(
				new ActionInterceptor[] {
					filter, filter, inter
		});
		assertEquals(1, interceptors.length);
		assertSame(inter, interceptors[0]);

		interceptors = interceptorsManager.extractActionInterceptors(
				new ActionInterceptor[] {
					filter, filter
		});
		assertEquals(0, interceptors.length);

		try {
			interceptorsManager.extractActionInterceptors(
					new ActionInterceptor[] {
						filter, inter, filter, inter
			});
			fail();
		} catch (MadvocException ignore) {
		}

		try {
			interceptorsManager.extractActionInterceptors(
					new ActionInterceptor[] {
						inter, inter, filter, inter
			});
			fail();
		} catch (MadvocException ignore) {
		}

		try {
			interceptorsManager.extractActionInterceptors(
					new ActionInterceptor[] {
						inter, filter
			});
			fail();
		} catch (MadvocException ignore) {
		}

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
			super(new Class[]{AnnotatedFieldsInterceptor.class, LogEchoInterceptor.class});
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
