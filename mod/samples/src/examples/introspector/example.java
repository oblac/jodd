// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.introspector;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;

import java.util.WeakHashMap;

public class example {

	public static void main(String[] args) throws ClassNotFoundException {

		ClassLoader customCL = new FooClassLoader();
		Class c = customCL.loadClass("examples.introspector.Foo");

		ClassDescriptor cd2 = ClassIntrospector.lookup(c);
		System.out.println(c.getClassLoader());
		System.out.println(cd2);
		System.out.println(ClassIntrospector.getStatistics());

		customCL = null;
		cd2 = null;

		System.gc();
		System.gc();
		System.gc();

		System.out.println("---after------------------------\n");
		System.out.println(ClassIntrospector.getStatistics());
		System.out.println("---again------------------------\n");
		cd2 = ClassIntrospector.lookup(c);
		System.out.println(cd2);
		System.out.println(ClassIntrospector.getStatistics());
//		ClassDescriptor cd = ClassIntrospector.lookup(Foo.class);
	}   
}
