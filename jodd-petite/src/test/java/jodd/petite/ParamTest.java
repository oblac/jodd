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

package jodd.petite;

import jodd.petite.fixtures.tst.Foo;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParamTest {

	@Test
	public void testSimpleParams() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false);

		pc.defineParameter("foo.name", "FOONAME");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("FOONAME", foo.getName());
	}

	@Test
	public void testRefParams() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false);

		pc.defineParameter("foo.name", "$${name}");
		pc.defineParameter("name", "${name${num}}");
		pc.defineParameter("num", "2");
		pc.defineParameter("name2", "FOONAME");
		pc.defineParameter("FOONAME", "aaa");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("$FOONAME", foo.getName());
	}

	@Test
	public void testRefParamsEscape() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false);

		pc.defineParameter("foo.name", "\\${name}");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("${name}", foo.getName());
	}

	@Test
	public void testRefParamsNoResolve() {
		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setResolveReferenceParameters(false);
		pc.registerPetiteBean(Foo.class, null, null, null, false);

		pc.defineParameter("foo.name", "${name}");
		pc.defineParameter("name", "${name2}");
		pc.defineParameter("name2", "FOONAME");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("${name}", foo.getName());
	}

	@Test
	public void testProperties() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerPetiteBean(Foo.class, null, null, null, false);

		Properties p = new Properties();
		p.setProperty("foo.name", "${name}");
		p.setProperty("name", "${name2}");
		p.setProperty("name2", "FOONAME");
		pc.defineParameters(p);

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("FOONAME", foo.getName());
	}

}
