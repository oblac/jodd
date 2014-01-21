// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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