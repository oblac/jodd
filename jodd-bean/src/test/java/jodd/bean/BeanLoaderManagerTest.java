package jodd.bean;

import junit.framework.TestCase;

public class BeanLoaderManagerTest extends TestCase {

	public void testRegisterDefaults() {
		assertEquals(2, BeanLoaderManager.loaders.size());
	}
}
