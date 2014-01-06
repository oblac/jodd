// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.core;

import jodd.petite.PetiteContainer;
import jodd.props.Props;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DefaultAppCoreTest {

	@Test
	public void testAppPropsNameAndPattern() {
		AppCore appCore = new AppCore();

		appCore.initCore();

		Assert.assertEquals("app.props", appCore.appPropsName);
		Assert.assertEquals("/app*.prop*", appCore.appPropsNamePattern);

		Assert.assertEquals("core", AppCore.PETITE_CORE);

		appCore.initLogger();
		appCore.initProps();
		appCore.initScanner();
		appCore.startPetite();
		PetiteContainer pc = appCore.petite;

		AppScanner as = (AppScanner) pc.getBean(AppCore.PETITE_SCAN);

		assertSame(appCore.appScanner, as);

		assertTrue(as.ignoreExceptions);
		assertEquals(3, as.includedEntries.length);
		assertEquals("jodd.*", as.includedEntries[0]);
		assertEquals("foo.*", as.includedEntries[1]);
		assertEquals("bar.*", as.includedEntries[2]);

		assertEquals(1, as.includedJars.length);
		assertEquals("xxx", as.includedJars[0]);
	}

	public static class AppCore extends DefaultAppCore {
		public AppCore() {
			appDir = "";
		}

		@Override
		protected void initProps() {
			appProps = new Props();

			appProps.setValue("scan.ignoreExceptions", "true");
			appProps.setValue("scan.includedEntries", "jodd.*,foo.*,bar.*");
			appProps.setValue("scan.includedJars", "xxx");
		}
	}
}