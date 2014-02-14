// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.methref;

import jodd.proxetta.ProxyAspect;
import jodd.proxetta.data.Str;
import jodd.proxetta.impl.ProxyProxetta;
import jodd.proxetta.pointcuts.AllTopMethodsPointcut;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
}