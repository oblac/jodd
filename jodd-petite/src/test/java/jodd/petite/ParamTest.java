// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.tst.Foo;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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