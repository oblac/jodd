package jodd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoddDbTest {

	@Test
	public void testLoadedModules() {
		assertEquals(true, Jodd.isJoddBeanLoaded());
		assertEquals(true, Jodd.isJoddDbLoaded());
		assertEquals(false, Jodd.isJoddHttpLoaded());
		assertEquals(false, Jodd.isJoddMadvocLoaded());
		assertEquals(false, Jodd.isJoddMailLoaded());
		assertEquals(true, Jodd.isJoddJtxLoaded());
		assertEquals(false, Jodd.isJoddPetiteLoaded());
		assertEquals(false, Jodd.isJoddPropsLoaded());
		assertEquals(true, Jodd.isJoddProxettaLoaded());
		assertEquals(false, Jodd.isJoddServletLoaded());
		assertEquals(false, Jodd.isJoddUploadLoaded());
		assertEquals(false, Jodd.isJoddVtorLoaded());
	}
}
