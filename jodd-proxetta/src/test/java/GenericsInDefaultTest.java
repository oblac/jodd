// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

import jodd.proxetta.ProxyAspect;
import jodd.proxetta.advice.DelegateAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import org.junit.Test;

import static org.junit.Assert.fail;

public class GenericsInDefaultTest {

	public static class Bar<T> {
	}

	public static interface IFoo {
		String getId();
		Bar<Foo> getFoo();
	}

	public static class Foo implements IFoo {
		private String id;
		public String getId() {
			return id;
		}
		// remove the generic and it works
		public Bar<Foo> getFoo() {
			return null;
		}
	}

	@Test
	public void testClassesWithGenericsAsReturnValueWrapperDefault() {
		try {
			ProxyAspect aspect = new ProxyAspect(DelegateAdvice.class);
			WrapperProxetta proxetta = WrapperProxetta.withAspects(aspect);
			WrapperProxettaBuilder builder = proxetta.builder(Foo.class, IFoo.class);
			builder.newInstance();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}

	@Test
	public void testClassesWithGenericsAsReturnValueProxyDefault() {
		try {
			ProxyAspect aspect = new ProxyAspect(DelegateAdvice.class);
			ProxyProxetta proxetta = ProxyProxetta.withAspects(aspect);
			ProxyProxettaBuilder builder = proxetta.builder(Foo.class);
			builder.newInstance();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}
}