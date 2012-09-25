// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddPetiteTest {

	@Test
	public void testLoadedModules() {
		assertEquals(true, Jodd.isJoddBeanLoaded());
		assertEquals(false, Jodd.isJoddHttpLoaded());
		assertEquals(false, Jodd.isJoddMadvocLoaded());
		assertEquals(false, Jodd.isJoddMailLoaded());
		assertEquals(true, Jodd.isJoddPetiteLoaded());
		assertEquals(true, Jodd.isJoddPropsLoaded());
		assertEquals(true, Jodd.isJoddProxettaLoaded());
		assertEquals(true, Jodd.isJoddServletLoaded());
		assertEquals(true, Jodd.isJoddUploadLoaded());
		assertEquals(false, Jodd.isJoddVtorLoaded());
	}
}
