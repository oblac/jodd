// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;

public class ServletBeanLoaderManagerTest extends TestCase {

	public void testRegisterDefaults() {
		assertEquals(7, BeanLoaderManager.loaders.size());
	}

}
