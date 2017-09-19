// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

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
			fail("error");
		} catch (MadvocException ignore) {}
	}

}
