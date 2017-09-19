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

import jodd.proxetta.fixtures.data.CollectorAdvice;
import jodd.proxetta.fixtures.data.Foo;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllTopMethodsPointcut;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdviceWithMethodTest {

	@Test
	public void testCollector() throws NoSuchFieldException, IllegalAccessException {
		ProxyProxetta proxetta = ProxyProxetta.withAspects(
				new ProxyAspect(CollectorAdvice.class, new AllTopMethodsPointcut())
		);

//		proxetta.setDebugFolder("d:\\");

		Foo foo = (Foo) proxetta.builder(Foo.class).newInstance();

		Field field = foo.getClass().getDeclaredField("$__methods$0");

		field.setAccessible(true);

		List<String> list = (List<String>) field.get(foo);

		assertNotNull(list);

		assertEquals(0, list.size());

		foo.m1();
		assertEquals(1, list.size());
		assertEquals("m1", list.get(0));

		foo.m1();
		assertEquals(2, list.size());
		assertEquals("m1", list.get(0));
		assertEquals("m1", list.get(1));
	}
}
