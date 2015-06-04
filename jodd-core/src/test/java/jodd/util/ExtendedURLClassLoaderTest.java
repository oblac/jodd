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
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExtendedURLClassLoaderTest {

	@Test
	public void testLoadSystemClasses() throws ClassNotFoundException {
		URL[] urls = new URL[0];

		// parent-first
		ExtendedURLClassLoader cl1 = new ExtendedURLClassLoader(urls, null, true);

		Class c1 = cl1.loadClass("java.lang.String");
		assertEquals(String.class, c1);

		// parent-last, still loaded by system loader
		ExtendedURLClassLoader cl2 = new ExtendedURLClassLoader(urls, null, false);

		Class c2 = cl2.loadClass("java.lang.String");
		assertEquals(String.class, c2);

		assertEquals(c1, c2);
	}

	@Test
	public void testParentLast() throws ClassNotFoundException {
		URLClassLoader thisClassLoader = (URLClassLoader) this.getClass().getClassLoader();

		URL[] urls = thisClassLoader.getURLs();

		String packageName = this.getClass().getPackage().getName();

		// parent-first
		ExtendedURLClassLoader cl1 = new ExtendedURLClassLoader(urls, null, true);

		Class c1 = cl1.loadClass(packageName + ".testdata.A");

		// parent-last, still loaded by system loader
		ExtendedURLClassLoader cl2 = new ExtendedURLClassLoader(urls, null, false);

		Class c2 = cl2.loadClass(packageName + ".testdata.A");

		assertFalse(c1.equals(c2));

		assertEquals(c1.getName(), c2.getName());
	}

	@Test
	public void testGetResource() throws IOException {
		File temp = FileUtil.createTempDirectory("jodd", "tmp");

		File resourceFile = new File(temp, "data");
		FileUtil.writeString(resourceFile, "RESOURCE CONTENT");
		resourceFile.deleteOnExit();

		URL[] urls = new URL[] {FileUtil.toURL(temp)};

		ExtendedURLClassLoader cl1 = new ExtendedURLClassLoader(urls, null, true);

		URL res = cl1.getResource("data");

		assertEquals(res, FileUtil.toURL(resourceFile));

		Enumeration<URL> enums = cl1.getResources("data");

		assertTrue(enums.hasMoreElements());
		assertEquals(res, enums.nextElement());

		FileUtil.deleteDir(temp);
	}
}