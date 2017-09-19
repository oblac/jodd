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

package jodd.util;

import jodd.io.FileUtil;
import jodd.util.cl.ExtendedURLClassLoader;
import jodd.util.fixtures.testdata.A;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;

public class ExtendedURLClassLoaderTest {

	private final URLClassLoader cl = (URLClassLoader) this.getClass().getClassLoader();

	@Test
	public void testLoadSystemClasses() throws ClassNotFoundException {
		URL[] urls = new URL[0];

		// parent-first
		ExtendedURLClassLoader cl1 = new ExtendedURLClassLoader(urls, cl, true);

		Class c1 = cl1.loadClass("java.lang.String");
		assertEquals(String.class, c1);

		// parent-last, still loaded by system loader
		ExtendedURLClassLoader cl2 = new ExtendedURLClassLoader(urls, cl, false);

		Class c2 = cl2.loadClass("java.lang.String");
		assertEquals(String.class, c2);

		assertEquals(c1, c2);
	}

	@Test
	public void testParentFirst() throws ClassNotFoundException {
		URLClassLoader parentCL = (URLClassLoader) A.class.getClassLoader();
		URL[] urls = parentCL.getURLs();

		// parent-first
		ExtendedURLClassLoader ecl = new ExtendedURLClassLoader(urls, parentCL, true);
		Class c1 = ecl.loadClass(A.class.getName());
		assertTrue(A.class.equals(c1));

		// force loader
		ecl = new ExtendedURLClassLoader(urls, parentCL, true);
		ecl.addLoaderOnlyRules(A.class.getPackage().getName() + ".*");
		c1 = ecl.loadClass(A.class.getName());
		assertFalse(A.class.equals(c1));

		// force parent, no loader
		URLClassLoader ucl = new URLClassLoader(new URL[0], null);
		ecl = new ExtendedURLClassLoader(urls, ucl, true);
		ecl.addParentOnlyRules(A.class.getName());
		try {
			ecl.loadClass(A.class.getName());
			fail("error");
		} catch (ClassNotFoundException ignore) {}
	}

	@Test
	public void testParentLast() throws ClassNotFoundException {
		URLClassLoader parentCL = (URLClassLoader) A.class.getClassLoader();
		URL[] urls = parentCL.getURLs();

		// parent-last
		ExtendedURLClassLoader ecl = new ExtendedURLClassLoader(urls, parentCL, false);
		Class c1 = ecl.loadClass(A.class.getName());
		assertFalse(A.class.equals(c1));

		// force parent
		ecl = new ExtendedURLClassLoader(urls, parentCL, false);
		ecl.addParentOnlyRules(A.class.getPackage().getName() + ".*");
		c1 = ecl.loadClass(A.class.getName());
		assertTrue(A.class.equals(c1));

		// force loader, no parent
		ecl = new ExtendedURLClassLoader(new URL[0], parentCL, false);
		ecl.addLoaderOnlyRules(A.class.getName());
		try {
			ecl.loadClass(A.class.getName());
			fail("error");
		} catch (ClassNotFoundException ignore) {}
	}

	@Test
	public void testGetResource() throws IOException {
		File tempRoot = FileUtil.createTempDirectory("jodd", "tmp");
		File temp = new File(tempRoot, "pckg");
		FileUtil.mkdir(temp);

		File resourceFile = new File(temp, "data");
		FileUtil.writeString(resourceFile, "RESOURCE CONTENT");
		resourceFile.deleteOnExit();
		URL[] urls = new URL[] {FileUtil.toURL(tempRoot)};

		// parent-first

		ExtendedURLClassLoader ecl = new ExtendedURLClassLoader(urls, cl, true);
		URL res = ecl.getResource("pckg/data");
		assertEquals(res, FileUtil.toURL(resourceFile));

		Enumeration<URL> enums = ecl.getResources("pckg/data");
		assertTrue(enums.hasMoreElements());
		assertEquals(res, enums.nextElement());

		// parent-first, parent-only
		ecl = new ExtendedURLClassLoader(urls, cl, true);
		ecl.addParentOnlyRules("pckg.data");
		res = ecl.getResource("pckg/data");
		assertNull(res);

		//// dot variant
		ecl = new ExtendedURLClassLoader(urls, cl, true);
		ecl.setMatchResourcesAsPackages(false);
		ecl.addParentOnlyRules("pckg/data");
		res = ecl.getResource("pckg/data");
		assertNull(res);


		// parent-last

		ecl = new ExtendedURLClassLoader(urls, cl, false);
		res = ecl.getResource("pckg/data");
		assertEquals(res, FileUtil.toURL(resourceFile));

		enums = ecl.getResources("pckg/data");
		assertTrue(enums.hasMoreElements());
		assertEquals(res, enums.nextElement());

		// parent-last, parent-only
		ecl = new ExtendedURLClassLoader(urls, cl, false);
		ecl.addLoaderOnlyRules("pckg.data");
		res = ecl.getResource("pckg/data");
		assertEquals(res, FileUtil.toURL(resourceFile));
		ecl.addParentOnlyRules("pckg.data");
		res = ecl.getResource("pckg/data");
		assertNull(res);

		//// dot variant
		ecl = new ExtendedURLClassLoader(urls, cl, false);
		ecl.setMatchResourcesAsPackages(false);
		ecl.addLoaderOnlyRules("pckg/data");
		res = ecl.getResource("pckg/data");
		assertEquals(res, FileUtil.toURL(resourceFile));
		ecl.addParentOnlyRules("pckg.data");
		res = ecl.getResource("pckg/data");
		assertNotNull(res);
		ecl.addParentOnlyRules("pckg/data");
		res = ecl.getResource("pckg/data");
		assertNull(res);

		FileUtil.deleteDir(tempRoot);
	}
}
