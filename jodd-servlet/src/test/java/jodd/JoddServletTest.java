// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;
import static jodd.Jodd.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JoddServletTest {

	@Test
	public void testLoadedModules() {
		assertTrue(Jodd.isModuleLoaded(BEAN));
		assertFalse(Jodd.isModuleLoaded(HTTP));
		assertFalse(Jodd.isModuleLoaded(MADVOC));
		assertFalse(Jodd.isModuleLoaded(MAIL));
		assertFalse(Jodd.isModuleLoaded(PETITE));
		assertFalse(Jodd.isModuleLoaded(PROPS));
		assertFalse(Jodd.isModuleLoaded(PROXETTA));
		assertTrue(Jodd.isModuleLoaded(SERVLET));
		assertTrue(Jodd.isModuleLoaded(UPLOAD));
		assertFalse(Jodd.isModuleLoaded(VTOR));
	}
}