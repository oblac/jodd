package jodd.bean;

import junit.framework.TestCase;

public class ServletBeanLoaderManagerTest extends TestCase {

	public void testRegisterDefaults() {
		assertEquals(7, BeanLoaderManager.loaders.size());
	}

}
