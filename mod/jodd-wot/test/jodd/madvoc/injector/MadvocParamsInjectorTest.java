// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.petite.PetiteContainer;
import junit.framework.TestCase;

public class MadvocParamsInjectorTest extends TestCase {

	public void testInjection() {
		PetiteContainer madpc = new PetiteContainer();

		madpc.defineParameter("foo", "1");
		madpc.defineParameter(FooBean.class.getName() + ".integer", "173");
		madpc.defineParameter(FooBean.class.getName() + ".string", "jodd");
		madpc.defineParameter(FooBean.class.getName(), "huh");

		MadvocParamsInjector madvocParamsInjector = new MadvocParamsInjector(madpc);

		FooBean fooBean = new FooBean();

		madvocParamsInjector.inject(fooBean);

		assertEquals(173, fooBean.getInteger().intValue());
		assertEquals("jodd", fooBean.getString());
	}
}
