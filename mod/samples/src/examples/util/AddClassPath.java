// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.util;

import jodd.util.ClassLoaderUtil;

public class AddClassPath {
	public static void main(String args[]) throws Exception {
		String working = "d:\\Projects\\java\\apps\\jodd\\mod\\samples\\";


//		byte[] classBytes = FileUtil.readBytes(working + "/mod/samples/TestClass.class");
//		Class c = ClassLoaderUtil.defineClass("TestClass", classBytes);
//		System.out.println(c);


		ClassLoaderUtil.addFileToClassPath(working, ClassLoader.getSystemClassLoader());
		Class c = Class.forName("TestClass");
		Object o = c.newInstance();
		System.out.println(o);
	}
}
