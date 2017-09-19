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

import jodd.core.JoddCore;
import jodd.io.FileUtil;
import jodd.io.findfile.ClassScanner;
import jodd.mutable.ValueHolder;
import jodd.mutable.ValueHolderWrapper;
import jodd.util.cl.DefaultClassLoaderStrategy;
import jodd.util.cl.ExtendedURLClassLoader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.*;

public class ClassLoaderUtilTest {

	@Test
	public void testStream() throws IOException {

		InputStream is = ClassLoaderUtil.getClassAsStream(ClassLoaderUtilTest.class);
		assertNotNull(is);
		is.close();

		is = ClassLoaderUtil.getClassAsStream(ClassLoaderUtilTest.class);
		assertNotNull(is);
		is.close();

		URL url;
		final String resourceName = "jodd/util/Bits.class";

		url = ClassLoaderUtil.getResourceUrl(resourceName);
		assertNotNull(url);

		is = ClassLoaderUtil.getResourceAsStream(resourceName);
		assertNotNull(is);
		is.close();
	}

	@Test
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

	@Test
	public void testLoadClass() throws Exception {
		try {
			ClassLoaderUtil.loadClass("not.existing.class");
		} catch (ClassNotFoundException cnfex) {
			assertEquals("Class not found: not.existing.class", cnfex.getMessage());
		}

		try {
			Class joddClass = ClassLoaderUtil.loadClass("jodd.util.ClassLoaderUtilTest");
			assertNotNull(joddClass);
		} catch (ClassNotFoundException ignore) {
			fail("error");
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

		String dummyClassName = Dummy.class.getName();
		assertEquals(Dummy.class, ClassLoaderUtil.loadClass(dummyClassName));

		assertEquals(Dummy[].class, ClassLoaderUtil.loadClass(dummyClassName + "[]"));
		assertEquals(Dummy[][].class, ClassLoaderUtil.loadClass(dummyClassName + "[][]"));

		// special case

		DefaultClassLoaderStrategy defaultClassLoaderStrategy = (DefaultClassLoaderStrategy) JoddCore.classLoaderStrategy;

		defaultClassLoaderStrategy.setLoadArrayClassByComponentTypes(true);

		URLClassLoader parentClassloader = (URLClassLoader)this.getClass().getClassLoader();
		URL[] urls = parentClassloader.getURLs();
		ExtendedURLClassLoader excl = new ExtendedURLClassLoader(urls, parentClassloader, false);

		Class dummyClass = ClassLoaderUtil.loadClass(dummyClassName, excl);
		assertFalse(Dummy.class == dummyClass); // classes are NOT the same as they are loaded by different class loaders
		assertEquals(Dummy.class.getName(), dummyClass.getName());

		// special case with array!

		Class dummyClassArray = ClassLoaderUtil.loadClass(dummyClassName + "[]", excl);
		assertFalse(Dummy[].class == dummyClassArray);
		assertEquals(Dummy[].class.getName(), dummyClassArray.getName());

		defaultClassLoaderStrategy.setLoadArrayClassByComponentTypes(false);
	}

	public static class Dummy {
	}

	@Test
	public void testWebJars() {
		URL url = ClassLoaderUtil.getResourceUrl("/META-INF/resources/webjars/jquery");

		File containerFile = FileUtil.toContainerFile(url);

		final ValueHolder<String> jqueryName = ValueHolderWrapper.create();

		ClassScanner classScanner = new ClassScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws Exception {
				if (entryData.getName().endsWith("jquery.js")) {
					jqueryName.set(entryData.getName());
				}
			}
		};

		classScanner.setIncludeResources(true);
		classScanner.scan(containerFile);

		assertNotNull(url);

		assertEquals("/META-INF/resources/webjars/jquery/2.1.1/jquery.js", jqueryName.get());
	}
}
