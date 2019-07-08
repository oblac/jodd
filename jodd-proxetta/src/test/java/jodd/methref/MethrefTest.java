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

package jodd.methref;

import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.fixtures.data.Str;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllTopMethodsPointcut;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MethrefTest {

	@Test
	void testString() {
		assertEquals("foo", Methref.get(Str.class).foo());
		assertEquals("foo2", Methref.get(Str.class).foo2(null, null));
	}

	@Test
	void testNonString() {
		final Methref<Str> mref = Methref.of(Str.class);
		assertEquals("redirect:boo", "redirect:" + mref.ref(mref.get().boo()));
		assertEquals("foo", mref.ref(mref.get().foo()));
	}

	@Test
	void testPrimitives() {
		final Methref<Str> mref = Methref.of(Str.class);
		assertEquals("izoo", mref.ref(mref.get().izoo()));
		assertEquals("fzoo", mref.ref(mref.get().fzoo()));
		assertEquals("dzoo", mref.ref(mref.get().dzoo()));
		assertEquals("lzoo", mref.ref(mref.get().lzoo()));
		assertEquals("bzoo", mref.ref(mref.get().bzoo()));
		assertEquals("szoo", mref.ref(mref.get().szoo()));
		assertEquals("czoo", mref.ref(mref.get().czoo()));
		assertEquals("yzoo", mref.ref(mref.get().yzoo()));
	}

	@Test
	void testVoidOrTwoSteps() {
		final Methref<Str> m = Methref.of(Str.class);
		m.get().voo();
		assertEquals("voo", m.ref());
	}

	@Test
	void testMethRefOnProxifiedClass() {
		Methref<? extends Oink> m = Methref.of(Oink.class);
		m.get().woink();
		assertEquals("woink", m.ref());

		final ProxyAspect a1 = new ProxyAspect(DummyAdvice.class, new AllTopMethodsPointcut());
		final ProxyProxetta pp = Proxetta.proxyProxetta().withAspect(a1);
		final Oink oink = (Oink) pp.proxy().setTarget(Oink.class).newInstance();

		assertNotEquals(oink.getClass(), Oink.class);

		m = Methref.of(oink.getClass());
		m.get().woink();
		assertEquals("woink", m.ref());
	}

	@Test
	void testParallelAccess() {
		final Methref<Str> methref1 = Methref.of(Str.class);

		final String m1 = methref1.ref(methref1.get().boo());

		final Methref<Str> methref2 = Methref.of(Str.class);

		final String m2 = methref2.ref(methref2.get().foo());

		assertEquals(m1, methref1.ref());
		assertEquals(m2, methref2.ref());
	}

	@Test
	void testCount() {
		final Methref<Str> methref = Methref.of(Str.class);

		final Str str = methref.get();
		assertEquals(0, methref.count());

		str.boo();

		assertEquals("boo", methref.ref());
		assertEquals(1, methref.count());

		str.boo();

		assertEquals("boo", methref.ref());
		assertEquals(2, methref.count());
	}

	@Test
	void testTwoCount2() {
		final Methref<Str> methref = Methref.of(Str.class);
		final Methref<Str> methref2 = Methref.of(Str.class);

		final Str str = methref.get();
		assertEquals(0, methref.count());
		assertEquals(0, methref2.count());

		str.boo();

		assertEquals("boo", methref.ref());
		assertEquals(1, methref.count());
		assertEquals(0, methref2.count());

		str.boo();

		assertEquals("boo", methref.ref());
		assertEquals(2, methref.count());
		assertEquals(0, methref2.count());
	}
}
