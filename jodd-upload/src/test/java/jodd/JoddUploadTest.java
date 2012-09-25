package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddUploadTest {

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
		assertEquals(true, Jodd.isJoddUploadLoaded());
		assertEquals(false, Jodd.isJoddVtorLoaded());
	}
}
