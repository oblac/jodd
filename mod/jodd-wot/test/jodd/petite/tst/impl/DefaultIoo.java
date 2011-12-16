// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.tst.impl;

import jodd.petite.tst.Ioo;
import jodd.petite.tst.Foo;
import jodd.petite.meta.PetiteInject;

public class DefaultIoo implements Ioo {

	@PetiteInject
	Foo foo;

	public void hello() {
		System.out.println("DefaultIoo.hello");
	}

	public Foo getFoo() {
		return foo;
	}
}
