// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import junit.framework.TestCase;
import jodd.petite.test.Foo;

import java.util.Properties;

public class ParamTest extends TestCase {

	public void testSimpleParams() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);

		pc.defineParameter("foo.name", "FOONAME");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("FOONAME", foo.getName());
	}

	public void testRefParams() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);

		pc.defineParameter("foo.name", "$${name}");
		pc.defineParameter("name", "${name${num}}");
		pc.defineParameter("num", "2");
		pc.defineParameter("name2", "FOONAME");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("$FOONAME", foo.getName());
	}

	public void testRefParamsEscape() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);

		pc.defineParameter("foo.name", "\\${name}");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("${name}", foo.getName());
	}

	public void testRefParamsNoResolve() {
		PetiteContainer pc = new PetiteContainer();
		pc.getConfig().setResolveReferenceParameters(false);
		pc.registerBean(Foo.class);

		pc.defineParameter("foo.name", "${name}");
		pc.defineParameter("name", "${name2}");
		pc.defineParameter("name2", "FOONAME");

		Foo foo = (Foo) pc.getBean("foo");
		assertNotNull(foo);
		assertEquals("${name}", foo.getName());
	}

	public void testProperties() {
		PetiteContainer pc = new PetiteContainer();
		pc.registerBean(Foo.class);

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
