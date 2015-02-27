// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.WebApplication;
import jodd.madvoc.component.MadvocConfig;
import jodd.petite.PetiteContainer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MadvocParamsInjectorTest {

	@Test
	public void testInjection() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		PetiteContainer madpc = (PetiteContainer) webapp.getComponent(WebApplication.MADVOC_CONTAINER_NAME);
		MadvocConfig madvocConfig = new MadvocConfig();

		String baseName = FooBean.class.getName();

		madpc.defineParameter("foo", "1");

		madpc.defineParameter(baseName + ".integer", "173");
		madpc.defineParameter(baseName + ".string", "jodd");
		madpc.defineParameter(baseName, "huh");

		MadvocParamsInjector madvocParamsInjector = new MadvocParamsInjector(madvocConfig);

		FooBean fooBean = new FooBean();

		madvocParamsInjector.injectContext(new Target(fooBean), null, madpc);

		assertEquals(173, fooBean.getInteger().intValue());
		assertEquals("jodd", fooBean.getString());
	}
}
