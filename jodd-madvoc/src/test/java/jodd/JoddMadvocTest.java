// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddMadvocTest {

	@Test
	public void testLoadedModules() {
		assertEquals(true, Jodd.isBeanLoaded());
		assertEquals(false, Jodd.isHttpLoaded());
		assertEquals(true, Jodd.isMadvocLoaded());
		assertEquals(false, Jodd.isMailLoaded());
		assertEquals(true, Jodd.isPetiteLoaded());
		assertEquals(true, Jodd.isPropsLoaded());
		//assertEquals(false, Jodd.isProxettaLoaded());
		assertEquals(true, Jodd.isServletLoaded());
		assertEquals(true, Jodd.isUploadLoaded());
		assertEquals(false, Jodd.isVtorLoaded());
	}
}
