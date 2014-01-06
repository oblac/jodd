// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JoddBeanTest {

	@Test
	public void testLoadedModules() {
		assertTrue(Jodd.isBeanLoaded());
		assertFalse(Jodd.isHttpLoaded());
		assertFalse(Jodd.isMadvocLoaded());
		assertFalse(Jodd.isMailLoaded());
		assertFalse(Jodd.isPetiteLoaded());
		assertFalse(Jodd.isPropsLoaded());
		assertFalse(Jodd.isProxettaLoaded());
		assertFalse(Jodd.isServletLoaded());
		assertFalse(Jodd.isUploadLoaded());
		assertFalse(Jodd.isVtorLoaded());
	}
}
