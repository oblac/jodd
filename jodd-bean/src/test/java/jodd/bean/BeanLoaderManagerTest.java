// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;

public class BeanLoaderManagerTest extends TestCase {

	public void testRegisterDefaults() {
		assertEquals(2, BeanLoaderManager.loaders.size());
	}
}
