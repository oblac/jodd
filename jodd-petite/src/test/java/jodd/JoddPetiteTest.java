package jodd;

import junit.framework.TestCase;

public class JoddPetiteTest extends TestCase {

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
