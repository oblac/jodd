// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test.impl;

import jodd.petite.test.Ioo;
import jodd.petite.test.Foo;
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
