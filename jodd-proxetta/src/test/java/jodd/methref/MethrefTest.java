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

import jodd.proxetta.ProxyAspect;
import jodd.proxetta.fixtures.data.Str;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllTopMethodsPointcut;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MethrefTest {

	@Test
	public void testString() {
		assertEquals("foo", Methref.onto(Str.class).foo());
		assertEquals("foo2", Methref.onto(Str.class).foo2(null, null));
	}

	@Test
	public void testNonString() {
		Methref<Str> mref = Methref.on(Str.class);
		assertEquals("redirect:boo", "redirect:" + mref.ref(mref.to().boo()));
		assertEquals("foo", mref.ref(mref.to().foo()));
	}

	@Test
	public void testPrimitives() {
		Methref<Str> mref = Methref.on(Str.class);
		assertEquals("izoo", mref.ref(mref.to().izoo()));
		assertEquals("fzoo", mref.ref(mref.to().fzoo()));
		assertEquals("dzoo", mref.ref(mref.to().dzoo()));
		assertEquals("lzoo", mref.ref(mref.to().lzoo()));
		assertEquals("bzoo", mref.ref(mref.to().bzoo()));
		assertEquals("szoo", mref.ref(mref.to().szoo()));
		assertEquals("czoo", mref.ref(mref.to().czoo()));
		assertEquals("yzoo", mref.ref(mref.to().yzoo()));
	}

	@Test
	public void testVoidOrTwoSteps() {
		Methref<Str> m = Methref.on(Str.class);
		m.to().voo();
		assertEquals("voo", m.ref());
	}

	@Test
	public void testMethRefOnProxifiedClass() {
		Methref<? extends Oink> m = Methref.on(Oink.class);
		m.to().woink();
		assertEquals("woink", m.ref());

		ProxyAspect a1 = new ProxyAspect(DummyAdvice.class, new AllTopMethodsPointcut());
		ProxyProxetta pp = ProxyProxetta.withAspects(a1);
		Oink oink = (Oink) pp.builder(Oink.class).newInstance();

		assertFalse(oink.getClass().equals(Oink.class));

		m = Methref.on(oink.getClass());
		m.to().woink();
		assertEquals("woink", m.ref());
	}

	@Test
	public void testParallelAccess() {
		Methref<Str> methref1 = Methref.on(Str.class);

		String m1 = methref1.ref(methref1.to().boo());

		Methref<Str> methref2 = Methref.on(Str.class);

		String m2 = methref2.ref(methref2.to().foo());

		assertEquals(m1, methref1.ref());
		assertEquals(m2, methref2.ref());

	}
}
