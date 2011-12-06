// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta;

import jodd.proxetta.ProxyPointcut;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.Proxetta;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.AnnotationInfo;
import jodd.io.FileUtil;
import jodd.util.ClassLoaderUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import examples.proxetta.log.Log;
import examples.proxetta.log.CustomProxyAdvice;

public class ProxettaExample {

	static ProxyAspect pd1 = new ProxyAspect(LogProxyAdvice.class, new ProxyPointcut() {

		public boolean apply(MethodInfo methodInfo) {
			System.out.println("#test " + methodInfo);
			AnnotationInfo[] anns = methodInfo.getAnnotations();
			if (anns != null) {
				for (AnnotationInfo a : anns) {
					if (a.getAnnotationClassname().equals(Log.class.getName())) {
						return true;
					}
				}
			}
			return false;
		}
	});

	static ProxyAspect pd2 = new ProxyAspect(CustomProxyAdvice.class, new ProxyPointcut() { 
		public boolean apply(MethodInfo methodInfo) {
			AnnotationInfo[] anns = methodInfo.getAnnotations();
			if (anns != null) {
				for (AnnotationInfo a : anns) {
					if (a.getAnnotationClassname().equals(Custom.class.getName())) {
						return true;
					}
				}
			}
			return false;
		}
	});


	public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {

		byte[] fooBytes = Proxetta.withAspects(pd1, pd2).createProxy(Foo.class);
		FileUtil.writeBytes("d://Foo.class", fooBytes);
		//Class fooClass = ClassLoaderUtil.defineClass("examples.proxetta.Foo$JoddProxy", fooBytes);
		Class fooClass = ClassLoaderUtil.defineClass(fooBytes);

		//Class fooClass = Foo.class;

		Custom custom = (Custom) fooClass.getAnnotation(Custom.class);
		System.out.println("\t\t\tclass annotation: " + custom);

		Foo foo = (Foo) fooClass.newInstance();
		System.out.println(foo.getClass());
		foo.one(Integer.valueOf(173));


		foo.four("123", 123);


		Constructor c = fooClass.getConstructor(String.class);
		custom = (Custom) c.getAnnotation(Custom.class);
		System.out.println("\t\t\tctor annotation: " + custom);
		foo = (Foo) c.newInstance("xxx");
		foo.one(Integer.valueOf(173));
		foo.two();
		foo.three();

		Method m = fooClass.getMethod("one", Integer.class);
		custom = m.getAnnotation(Custom.class);
		System.out.println("\t\t\tmethod annotation: " + custom);

		System.out.println("\n\n\n--------------------------------------------------------------------");
		Class zooClass = Proxetta.withAspects(pd1,pd2).forced(true).defineProxy(Zoo.class);
		System.out.println(zooClass);
		Zoo zoo = (Zoo) zooClass.newInstance();
		zoo.zoo();
		System.out.println("===");
		zoo.foo();
	}
}
