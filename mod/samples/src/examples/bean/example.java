// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.bean;

import jodd.bean.BeanUtil;

public class example {

	public static void main(String[] args) {
		Foo foo = new Foo();

		BeanUtil.setProperty(foo, "readwrite", "John Doe");
		System.out.println(BeanUtil.getProperty(foo, "readwrite"));
		BeanUtil.setDeclaredProperty(foo, "readonly", "John Doe");
		System.out.println(BeanUtil.getProperty(foo, "readonly"));
	}
}
