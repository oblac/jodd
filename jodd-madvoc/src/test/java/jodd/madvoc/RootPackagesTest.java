// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class RootPackagesTest {

	@Test
	public void testRootPackagesPackagePath() {
		RootPackages rootPackages = new RootPackages();

		rootPackages.addRootPackage("xx");
		rootPackages.addRootPackage("xx.admin.actions", "admin");
		rootPackages.addRootPackage("xx.cms.actions", "cms");

		assertEquals("", rootPackages.findPackagePathForActionPackage("xx"));
		assertNull(rootPackages.getPackageActionPath("xx"));
		assertEquals("/admin", rootPackages.findPackagePathForActionPackage("xx.admin.actions"));
		assertEquals("/admin/hey", rootPackages.findPackagePathForActionPackage("xx.admin.actions.hey"));
		assertEquals("/cms", rootPackages.findPackagePathForActionPackage("xx.cms.actions"));
		assertEquals("/cms/hay", rootPackages.findPackagePathForActionPackage("xx.cms.actions.hay"));
	}

	@Test
	public void testRootPackagesFindForPath() {
		RootPackages rootPackages = new RootPackages();

		rootPackages.addRootPackage("xx");
		rootPackages.addRootPackage("xx.admin.actions", "admin");
		rootPackages.addRootPackage("xx.cms.actions", "cms");

		assertEquals("xx", rootPackages.findRootPackageForActionPath("/foo"));
		assertEquals("xx.admin.actions", rootPackages.findRootPackageForActionPath("/admin"));
		assertEquals("xx.admin.actions", rootPackages.findRootPackageForActionPath("/admin/hey"));
		assertEquals("xx.cms.actions", rootPackages.findRootPackageForActionPath("/cms"));
		assertEquals("xx.cms.actions", rootPackages.findRootPackageForActionPath("/cms/hay"));
	}

	@Test
	public void testDuplicateRootPackages() {
		RootPackages rootPackages = new RootPackages();
		rootPackages.addRootPackage("xx.zz", "foo");
		try {
			rootPackages.addRootPackage("xx.zz", "bar");
			fail();
		} catch (MadvocException ignore) {}
	}

}