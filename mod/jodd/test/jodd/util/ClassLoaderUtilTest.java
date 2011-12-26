// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClassLoaderUtilTest extends TestCase {

	public void testStream() throws IOException {

		InputStream is = ClassLoaderUtil.getClassAsStream(ClassLoaderUtilTest.class);
		assertNotNull(is);
		is.close();

		is = ClassLoaderUtil.getClassAsStream(ClassLoaderUtilTest.class);
		assertNotNull(is);
		is.close();

		URL url;
		final String resourceName = "jodd/util/Bits.class";

		url = ClassLoaderUtil.getResourceUrl(resourceName, ClassLoaderUtilTest.class);
		assertNotNull(url);

		is = ClassLoaderUtil.getResourceAsStream(resourceName);
		assertNotNull(is);
		is.close();
	}

	public void testClassFileName() {
		assertEquals("jodd/util/ClassLoaderUtilTest.class", ClassLoaderUtil.getClassFileName(ClassLoaderUtilTest.class));
		assertEquals("jodd/util/ClassLoaderUtilTest.class", ClassLoaderUtil.getClassFileName(ClassLoaderUtilTest[].class));
		assertEquals("jodd/util/ClassLoaderUtilTest$Boo.class", ClassLoaderUtil.getClassFileName(Boo.class));

		assertEquals("jodd/util/ClassLoaderUtilTest.class", ClassLoaderUtil.getClassFileName(ClassLoaderUtilTest.class.getName()));
		assertEquals("jodd/util/ClassLoaderUtilTest$Boo.class", ClassLoaderUtil.getClassFileName(Boo.class.getName()));
	}

	public static class Boo {
		int v;
	}

	public void testLoadClass() throws ClassNotFoundException {
		try {
			ClassLoaderUtil.loadClass("not.existing.class");
		} catch (ClassNotFoundException cnfex) {
			assertEquals("Class not found: not.existing.class", cnfex.getMessage());
		}

		try {
			Class joddClass = ClassLoaderUtil.loadClass("jodd.Jodd");
			assertNotNull(joddClass);
		} catch (ClassNotFoundException ignore) {
			fail();
		}
		assertEquals(Integer.class, ClassLoaderUtil.loadClass("java.lang.Integer"));
		assertEquals(int.class, ClassLoaderUtil.loadClass("int"));
		assertEquals(boolean.class, ClassLoaderUtil.loadClass("boolean"));
		assertEquals(short.class, ClassLoaderUtil.loadClass("short"));
		assertEquals(byte.class, ClassLoaderUtil.loadClass("byte"));
		assertEquals(char.class, ClassLoaderUtil.loadClass("char"));
		assertEquals(double.class, ClassLoaderUtil.loadClass("double"));
		assertEquals(float.class, ClassLoaderUtil.loadClass("float"));
		assertEquals(long.class, ClassLoaderUtil.loadClass("long"));

		assertEquals(Integer[].class, ClassLoaderUtil.loadClass("java.lang.Integer[]"));
		assertEquals(int[].class, ClassLoaderUtil.loadClass("int[]"));
		assertEquals(boolean[].class, ClassLoaderUtil.loadClass("boolean[]"));
		assertEquals(short[].class, ClassLoaderUtil.loadClass("short[]"));
		assertEquals(byte[].class, ClassLoaderUtil.loadClass("byte[]"));
		assertEquals(char[].class, ClassLoaderUtil.loadClass("char[]"));
		assertEquals(double[].class, ClassLoaderUtil.loadClass("double[]"));
		assertEquals(float[].class, ClassLoaderUtil.loadClass("float[]"));
		assertEquals(long[].class, ClassLoaderUtil.loadClass("long[]"));

		assertEquals(Integer[][].class, ClassLoaderUtil.loadClass("java.lang.Integer[][]"));
		assertEquals(int[][].class, ClassLoaderUtil.loadClass("int[][]"));
	}

}
