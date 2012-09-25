// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServletBeanLoaderManagerTest {

	@Test
	public void testRegisterDefaults() {
		assertEquals(7, BeanLoaderManager.loaders.size());
	}

}
