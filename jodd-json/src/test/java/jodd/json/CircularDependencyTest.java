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

package jodd.json;

import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("Duplicates")
public class CircularDependencyTest {

	public static class A {
		private String name = null;
		private B pi = null;

		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }
		public B getPi() { return pi; }
		public void setPi( B pi ) { this.pi = pi; }
	}

	public static class B {
		private String prop = null;
		private A a = new A();
		private List<A> ps = new ArrayList<A>();

		public A getA() { return a; }
		public void setA(A a) { this.a = a; }
		public String getProp() { return prop; }
		public void setProp( String prop ) { this.prop = prop; }
		public List<A> getPs() { return ps; }
		public void addP( A p ) { ps.add( p ); }
	}

	@Test
	public void testCircularDependency_none() {
		A a1 = new A();
		a1.setName("a1");

		A a2 = new A();
		a2.setName("a2");

		B b = new B();
		b.setProp("value");
		b.setA(a2);		// b -> a2, no circ.dep.

		// circular reference
		a1.setPi(b);

		JsonSerializer serializer = new JsonSerializer().deep(true);

		String json = serializer.serialize(a1);
		JsonParser.create().parse(json);

		assertTrue(json.contains("a1"));
		assertTrue(json.contains("a2"));
	}

	@Test
	public void testCircularDependency_property() {
		A a1 = new A();
		a1.setName("a1");

		A a2 = new A();
		a2.setName("a2");

		B b = new B();
		b.setProp("value");
		b.setA(a1);		// b -> a1, has circ.dep.

		// circular reference
		a1.setPi(b);

		JsonSerializer serializer = new JsonSerializer().deep(true);

		String json = serializer.serialize(a1);
		JsonParser.create().parse(json);

		assertEquals(1, StringUtil.count(json, "a1"));
		assertEquals(0, StringUtil.count(json, "a2"));
		assertEquals(1, StringUtil.count(json, "pi"));
	}

	@Test
	public void testCircularDependency_propertyArray() {
		A a1 = new A();
		a1.setName("a1");

		A a2 = new A();
		a2.setName("a2");

		B b = new B();
		b.setProp("value");
		b.setA(a1);		// b -> a1, has circ.dep.

		b.addP(a1);
		b.addP(a2);

		// circular reference
		a1.setPi(b);

		JsonSerializer serializer = new JsonSerializer().deep(true);

		String json = serializer.serialize(a1);
		JsonParser.create().parse(json);

		assertEquals(1, StringUtil.count(json, "a1"));
		assertEquals(1, StringUtil.count(json, "a2"));
	}
}
