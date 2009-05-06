// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

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
		final String resourceName = "jodd/swing/spy/icons/button.png";

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


}
