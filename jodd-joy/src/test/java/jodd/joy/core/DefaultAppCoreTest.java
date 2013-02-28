// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

import junit.framework.Assert;
import org.junit.Test;

public class DefaultAppCoreTest {

	@Test
	public void testAppPropsNameAndPattern() {
		AppCore appCore = new AppCore();

		appCore.initCore();

		Assert.assertEquals("app.props", appCore.appPropsName);
		Assert.assertEquals("/app*.prop*", appCore.appPropsNamePattern);
	}

	public static class AppCore extends DefaultAppCore {

		public AppCore() {
			appDir = "";
		}
	}
}