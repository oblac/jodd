// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.petite.news;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import examples.petite.Foo;
import examples.petite.Boo;

@PetiteBean("foo")
public class FooImpl2 implements Foo {

	@PetiteInject
	Boo boo;

	public void foo() {
		if (boo != null) {
			boo.boo();
		}
		System.out.println("!!!foo2 " + toString());
	}
}
