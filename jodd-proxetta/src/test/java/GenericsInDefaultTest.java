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

import jodd.proxetta.ProxyAspect;
import jodd.proxetta.advice.DelegateAdvice;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.impl.ProxyProxettaBuilder;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

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
