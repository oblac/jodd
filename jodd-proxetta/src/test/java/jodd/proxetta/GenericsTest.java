// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.advice.DelegateAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import org.junit.Test;

import static org.junit.Assert.fail;

public class GenericsTest {

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
	public void testClassesWithGenericsAsReturnValueWrapper() {
		try {
			ProxyAspect aspect = new ProxyAspect(DelegateAdvice.class);
			WrapperProxetta proxetta = WrapperProxetta.withAspects(aspect);
			WrapperProxettaBuilder builder = proxetta.builder(Foo.class, IFoo.class);
			builder.newInstance();
		}
		catch (Exception ex) {
			fail(ex.toString());
		}
	}

	@Test
	public void testClassesWithGenericsAsReturnValueProxy() {
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