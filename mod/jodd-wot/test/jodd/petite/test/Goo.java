// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test;

import jodd.petite.meta.PetiteInject;

public class Goo {

	public Foo foo;

	@PetiteInject("loo")
	public Loo looCustom;

}
