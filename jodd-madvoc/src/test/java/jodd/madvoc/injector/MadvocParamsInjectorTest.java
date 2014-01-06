// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.petite.PetiteContainer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MadvocParamsInjectorTest {

	@Test
	public void testInjection() {
		PetiteContainer madpc = new PetiteContainer();
		String baseName = FooBean.class.getName();

		madpc.defineParameter("foo", "1");

		madpc.defineParameter(baseName + ".integer", "173");
		madpc.defineParameter(baseName + ".string", "jodd");
		madpc.defineParameter(baseName, "huh");

		MadvocParamsInjector madvocParamsInjector = new MadvocParamsInjector(madpc);

		FooBean fooBean = new FooBean();

		madvocParamsInjector.inject(fooBean, baseName);

		assertEquals(173, fooBean.getInteger().intValue());
		assertEquals("jodd", fooBean.getString());
	}
}
