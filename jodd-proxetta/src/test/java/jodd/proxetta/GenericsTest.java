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

import jodd.proxetta.advice.DelegateAdvice;
import jodd.proxetta.fixtures.data.Action;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaFactory;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.impl.WrapperProxettaFactory;
import jodd.proxetta.pointcuts.AllMethodsPointcut;
import org.junit.jupiter.api.Test;

import static jodd.proxetta.ProxyTarget.createArgumentsClassArray;
import static org.junit.jupiter.api.Assertions.fail;

class GenericsTest {
	public static class Bar<T> {
	}

	public static interface IFoo {
		String getId();
		Bar<Foo> getFoo();
	}

	public static class Foo implements IFoo {
		private String id;
		@Override
		public String getId() {
			return id;
		}
		// remove the generic and it works
		@Override
		public Bar<Foo> getFoo() {
			return null;
		}
	}

	@Test
	void testClassesWithGenericsAsReturnValueWrapper() {
		try {
			ProxyAspect aspect = new ProxyAspect(DelegateAdvice.class, new AllMethodsPointcut());
			WrapperProxetta proxetta = Proxetta.wrapperProxetta().withAspects(aspect);
			WrapperProxettaFactory builder = proxetta.proxy().setTarget(Foo.class).setTargetInterface(IFoo.class);
			builder.newInstance();
		}
		catch (Exception ex) {
			fail(ex.toString());
		}
	}

	@Test
	void testClassesWithGenericsAsReturnValueProxy() {
		try {
			ProxyAspect aspect = new ProxyAspect(DelegateAdvice.class, new AllMethodsPointcut());
			ProxyProxetta proxetta = Proxetta.proxyProxetta().withAspects(aspect);
			ProxyProxettaFactory builder = proxetta.proxy().setTarget(Foo.class);
			builder.newInstance();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.toString());
		}
	}

	// ---------------------------------------------------------------- misc

	public static class Boo<T> {
		@Action
		public void save(T t) {
		}
	}

	public static class MyBoo extends Boo<Foo> {
	}

	public static class LogAdvice implements ProxyAdvice {

		@Override
		public Object execute() {
			System.out.println(ProxyTarget.targetMethodName());

			Class[] methodArgsTypes = createArgumentsClassArray();

			System.out.println(methodArgsTypes);

			return ProxyTarget.invoke();
		}
	}

	@Test
	void testExtendingGenerics() {
		ProxyAspect aspect = new ProxyAspect(LogAdvice.class, new AllMethodsPointcut());
		ProxyProxetta proxetta = Proxetta.proxyProxetta().withAspects(aspect);
		ProxyProxettaFactory builder = proxetta.proxy().setTarget(MyBoo.class);
		Boo boo = (Boo) builder.newInstance();
		boo.save(new Foo());
	}
}
