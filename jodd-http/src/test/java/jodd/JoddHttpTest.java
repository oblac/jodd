// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddHttpTest {

	@Test
	public void testLoadedModules() {
		assertEquals(false, Jodd.isBeanLoaded());
		assertEquals(true, Jodd.isHttpLoaded());
		assertEquals(false, Jodd.isMadvocLoaded());
		assertEquals(false, Jodd.isMailLoaded());
		assertEquals(false, Jodd.isPetiteLoaded());
		assertEquals(false, Jodd.isPropsLoaded());
		assertEquals(false, Jodd.isProxettaLoaded());
		assertEquals(false, Jodd.isServletLoaded());
		assertEquals(true, Jodd.isUploadLoaded());
		assertEquals(false, Jodd.isVtorLoaded());
	}
}
