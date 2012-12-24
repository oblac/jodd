// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddVtorTest {

	@Test
	public void testLoadedModules() {
		assertEquals(true, Jodd.isBeanLoaded());
		assertEquals(false, Jodd.isHttpLoaded());
		assertEquals(false, Jodd.isMadvocLoaded());
		assertEquals(false, Jodd.isMailLoaded());
		assertEquals(false, Jodd.isPetiteLoaded());
		assertEquals(false, Jodd.isPropsLoaded());
		assertEquals(false, Jodd.isProxettaLoaded());
		assertEquals(false, Jodd.isServletLoaded());
		assertEquals(false, Jodd.isUploadLoaded());
		assertEquals(true, Jodd.isVtorLoaded());
	}
}
