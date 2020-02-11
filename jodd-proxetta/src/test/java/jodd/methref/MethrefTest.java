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
		assertEquals("foo", Methref.of(Str.class).name(Str::foo));
		assertEquals("foo2", Methref.of(Str.class).name(s -> s.foo2(null, null)));

		final Str str = Methref.of(Str.class).proxy();
		str.foo();
		assertEquals("foo", Methref.lastName(str));
		str.foo2(null, null);
		assertEquals("foo2", Methref.lastName(str));
	}

	@Test
	void testNonString() {
		final Methref<Str> mref = Methref.of(Str.class);
		assertEquals("redirect:boo", "redirect:" + mref.name(Str::boo));
		mref.proxy().boo();
		assertEquals("redirect:boo", "redirect:" + Methref.lastName(mref.proxy()));
	}

	@Test
	void testPrimitives() {
		final Methref<Str> mref = Methref.of(Str.class);
		assertEquals("izoo", mref.name(Str::izoo));
		assertEquals("fzoo", mref.name(Str::fzoo));
		assertEquals("dzoo", mref.name(Str::dzoo));
		assertEquals("lzoo", mref.name(Str::lzoo));
		assertEquals("bzoo", mref.name(Str::bzoo));
		assertEquals("szoo", mref.name(Str::szoo));
		assertEquals("czoo", mref.name(Str::czoo));
		assertEquals("yzoo", mref.name(Str::yzoo));

		mref.proxy().izoo();
		assertEquals("izoo", mref.lastName());
		mref.proxy().fzoo();
		assertEquals("fzoo", mref.lastName());
		mref.proxy().dzoo();
		assertEquals("dzoo", mref.lastName());
		mref.proxy().lzoo();
		assertEquals("lzoo", mref.lastName());
		mref.proxy().bzoo();
		assertEquals("bzoo", mref.lastName());
		mref.proxy().szoo();
		assertEquals("szoo", mref.lastName());
		mref.proxy().czoo();
		assertEquals("czoo", mref.lastName());
		mref.proxy().yzoo();
		assertEquals("yzoo", mref.lastName());
	}

	@Test
	void testVoidOrTwoSteps() {
		final Methref<Str> m = Methref.of(Str.class);
		assertEquals("voo", m.name(Str::voo));

		m.proxy().voo();
		assertEquals("voo", m.lastName());
	}

	@Test
	void testMethRefOnProxifiedClass() {
		Methref<? extends Oink> m = Methref.of(Oink.class);
		String name = m.name(Oink::woink);
		assertEquals("woink", name);

		final ProxyAspect a1 = new ProxyAspect(DummyAdvice.class, new AllTopMethodsPointcut());
		final ProxyProxetta pp = Proxetta.proxyProxetta().withAspect(a1);
		final Oink oink = (Oink) pp.proxy().setTarget(Oink.class).newInstance();

		assertNotEquals(oink.getClass(), Oink.class);

		m = Methref.of(oink.getClass());
		name = m.name(Oink::woink);
		assertEquals("woink", name);
	}

	@Test
	void testParallelAccess() {
		final Methref<Str> methref1 = Methref.of(Str.class);

		final String m1 = methref1.name(Str::boo);

		final Methref<Str> methref2 = Methref.of(Str.class);

		final String m2 = methref2.name(Str::foo);

		assertEquals("boo", m1);
		assertEquals("foo", m2);
	}
}
