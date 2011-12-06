// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package examples.petite;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import jodd.petite.scope.ProtoScope;

@PetiteBean(scope=ProtoScope.class)
public class Zoo {

	@PetiteInject
	Foo foo;

	void zoo() {
		foo.foo();
		System.out.println("Zoonnn: " + toString() + "    " + this.getClass());
	}
}
