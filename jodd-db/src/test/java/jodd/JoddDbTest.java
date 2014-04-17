// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;
import static jodd.Jodd.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JoddDbTest {

	@Test
	public void testLoadedModules() {
		assertTrue(Jodd.isModuleLoaded(BEAN));
		assertTrue(Jodd.isModuleLoaded(DB));
		assertFalse(Jodd.isModuleLoaded(HTTP));
		assertFalse(Jodd.isModuleLoaded(MADVOC));
		assertFalse(Jodd.isModuleLoaded(MAIL));
		assertTrue(Jodd.isModuleLoaded(JTX));
		assertFalse(Jodd.isModuleLoaded(PETITE));
		assertTrue(Jodd.isModuleLoaded(PROPS));
		assertTrue(Jodd.isModuleLoaded(PROXETTA));
		assertFalse(Jodd.isModuleLoaded(SERVLET));
		assertFalse(Jodd.isModuleLoaded(UPLOAD));
		assertFalse(Jodd.isModuleLoaded(VTOR));
	}
}
