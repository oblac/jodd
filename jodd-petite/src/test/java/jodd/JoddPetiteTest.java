// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.petite.JoddPetite;
import org.junit.Test;

import static jodd.Jodd.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JoddPetiteTest {

	@Test
	public void testLoadedModules() {
		assertTrue(JoddPetite.useProxetta);

		assertTrue(Jodd.isModuleLoaded(BEAN));
		assertFalse(Jodd.isModuleLoaded(HTTP));
		assertFalse(Jodd.isModuleLoaded(MADVOC));
		assertFalse(Jodd.isModuleLoaded(MAIL));
		assertTrue(Jodd.isModuleLoaded(PETITE));
		assertTrue(Jodd.isModuleLoaded(PROPS));
		assertTrue(Jodd.isModuleLoaded(PROXETTA));
		assertTrue(Jodd.isModuleLoaded(SERVLET));
		assertTrue(Jodd.isModuleLoaded(UPLOAD));
		assertFalse(Jodd.isModuleLoaded(VTOR));
	}
}
