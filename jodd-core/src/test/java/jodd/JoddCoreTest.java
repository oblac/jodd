// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Test;

import static jodd.Jodd.*;
import static org.junit.Assert.assertFalse;

public class JoddCoreTest {

	@Test
	public void testLoadedModules() {
		assertFalse(Jodd.isModuleLoaded(BEAN));
		assertFalse(Jodd.isModuleLoaded(HTTP));
		assertFalse(Jodd.isModuleLoaded(MADVOC));
		assertFalse(Jodd.isModuleLoaded(MAIL));
		assertFalse(Jodd.isModuleLoaded(PETITE));
		assertFalse(Jodd.isModuleLoaded(PROPS));
		assertFalse(Jodd.isModuleLoaded(PROXETTA));
		assertFalse(Jodd.isModuleLoaded(SERVLET));
		assertFalse(Jodd.isModuleLoaded(UPLOAD));
		assertFalse(Jodd.isModuleLoaded(VTOR));
	}
}
