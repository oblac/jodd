// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import jodd.proxetta.data.Str;
import jodd.proxetta.methref.Methref;
import junit.framework.TestCase;

public class MethrefTest extends TestCase {

	public void testString() {
		assertEquals("foo", Methref.sref(Str.class).foo());
		assertEquals("foo2", Methref.sref(Str.class).foo2(null, null));
	}

	public void testNonString() {
		Methref<Str> m = Methref.on(Str.class);
		assertEquals("redirect:boo", "redirect:" + m.ref(m.method().boo()));
		assertEquals("foo", m.ref(m.method().foo()));
	}

	public void testPrimitives() {
		Methref<Str> m = Methref.on(Str.class);
		assertEquals("izoo", m.ref(m.method().izoo()));
		assertEquals("fzoo", m.ref(m.method().fzoo()));
		assertEquals("dzoo", m.ref(m.method().dzoo()));
		assertEquals("lzoo", m.ref(m.method().lzoo()));
		assertEquals("bzoo", m.ref(m.method().bzoo()));
		assertEquals("szoo", m.ref(m.method().szoo()));
		assertEquals("czoo", m.ref(m.method().czoo()));
		assertEquals("yzoo", m.ref(m.method().yzoo()));
	}

	public void testVoid() {
		Methref<Str> m = Methref.on(Str.class);
		m.method().voo();
		assertEquals("voo", m.ref());

	}
}
