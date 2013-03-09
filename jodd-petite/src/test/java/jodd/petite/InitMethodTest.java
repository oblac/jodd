// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.tst4.Bar;
import jodd.petite.tst4.Foo;
import jodd.petite.tst4.Foo2;
import org.junit.Test;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_CONSTRUCT;
import static jodd.petite.meta.InitMethodInvocationStrategy.POST_DEFINE;
import static jodd.petite.meta.InitMethodInvocationStrategy.POST_INITIALIZE;
import static org.junit.Assert.assertEquals;

public class InitMethodTest {

	@Test
	public void testPostConstructor() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		petiteContainer.defineBean("bar", Bar.class);
		petiteContainer.defineBean("foo", Foo.class);

		// wiring
		petiteContainer.registerPropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerInitMethods("foo", new String[] {"init"}, POST_CONSTRUCT);

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo foo = (Foo) petiteContainer.getBean("foo");

		assertEquals("ctor null null", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}

	@Test
	public void testPostDefine() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		petiteContainer.defineBean("bar", Bar.class);
		petiteContainer.defineBean("foo", Foo.class);

		// wiring
		petiteContainer.registerPropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerInitMethods("foo", new String[] {"init"}, POST_DEFINE);

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo foo = (Foo) petiteContainer.getBean("foo");

		assertEquals("ctor bar null", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}

	@Test
	public void testPostInitialize() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		petiteContainer.defineBean("bar", Bar.class);
		petiteContainer.defineBean("foo", Foo.class);

		// wiring
		petiteContainer.registerPropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerInitMethods("foo", new String[] {"init"}, POST_INITIALIZE);

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo foo = (Foo) petiteContainer.getBean("foo");

		assertEquals("ctor bar data", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}

	@Test
	public void testPostAll() {
		PetiteContainer petiteContainer = new PetiteContainer();

		// define two beans
		petiteContainer.defineBean("bar", Bar.class);
		petiteContainer.defineBean("foo", Foo2.class);

		// wiring
		petiteContainer.registerPropertyInjectionPoint("foo", "bar", "bar");

		// init method
		petiteContainer.registerInitMethods("foo", new String[] {"init1"}, POST_CONSTRUCT);
		petiteContainer.registerInitMethods("foo", new String[] {"init2"}, POST_DEFINE);
		petiteContainer.registerInitMethods("foo", new String[] {"init3"}, POST_INITIALIZE);

		// param
		petiteContainer.defineParameter("foo.data", "data");

		// get bean
		Foo2 foo = (Foo2) petiteContainer.getBean("foo");

		assertEquals("1 null null 2 bar null 3 bar data", foo.result);
		assertEquals("bar", foo.bar.toString());
		assertEquals("data", foo.data);
	}
}