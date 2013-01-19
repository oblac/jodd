// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.petite.PetiteContainer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MadvocParamsInjectorTest {

	@Test
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
