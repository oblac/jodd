package jodd;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JoddBeanTest {

	@Test
	public void testLoadedModules() {
		assertTrue(Jodd.isJoddBeanLoaded());
		assertFalse(Jodd.isJoddHttpLoaded());
		assertFalse(Jodd.isJoddMadvocLoaded());
		assertFalse(Jodd.isJoddMailLoaded());
		assertFalse(Jodd.isJoddPetiteLoaded());
		assertFalse(Jodd.isJoddPropsLoaded());
		assertFalse(Jodd.isJoddProxettaLoaded());
		assertFalse(Jodd.isJoddServletLoaded());
		assertFalse(Jodd.isJoddUploadLoaded());
		assertFalse(Jodd.isJoddVtorLoaded());
	}
}
