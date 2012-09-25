package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddMadvocTest {

	@Test
	public void testLoadedModules() {
		assertEquals(true, Jodd.isJoddBeanLoaded());
		assertEquals(false, Jodd.isJoddHttpLoaded());
		assertEquals(true, Jodd.isJoddMadvocLoaded());
		assertEquals(false, Jodd.isJoddMailLoaded());
		assertEquals(true, Jodd.isJoddPetiteLoaded());
		assertEquals(true, Jodd.isJoddPropsLoaded());
		assertEquals(false, Jodd.isJoddProxettaLoaded());
		assertEquals(true, Jodd.isJoddServletLoaded());
		assertEquals(true, Jodd.isJoddUploadLoaded());
		assertEquals(false, Jodd.isJoddVtorLoaded());
	}
}
