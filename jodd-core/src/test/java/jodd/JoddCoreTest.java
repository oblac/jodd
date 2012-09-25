// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddCoreTest {

	@Test
	public void testLoadedModules() {
		assertEquals(false, Jodd.isJoddBeanLoaded());
		assertEquals(false, Jodd.isJoddHttpLoaded());
		assertEquals(false, Jodd.isJoddMadvocLoaded());
		assertEquals(false, Jodd.isJoddMailLoaded());
		assertEquals(false, Jodd.isJoddPetiteLoaded());
		assertEquals(false, Jodd.isJoddPropsLoaded());
		assertEquals(false, Jodd.isJoddProxettaLoaded());
		assertEquals(false, Jodd.isJoddServletLoaded());
		assertEquals(false, Jodd.isJoddUploadLoaded());
		assertEquals(false, Jodd.isJoddVtorLoaded());
	}
}
