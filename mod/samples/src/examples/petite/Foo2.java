// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.petite;

import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;

public class Foo2 {

	@PetiteInject
	public Boo2 boo2;

	public void foo() {
		if (boo2 != null) {
			boo2.boo();
		}
		System.out.println("foo: " + toString());
	}


	public static void main(String[] args) {

		PetiteContainer petite = new PetiteContainer();
		petite.registerBean(Foo2.class);
		petite.registerBean(Boo2.class);

		Foo2 foo2 = (Foo2) petite.getBean("foo2");

		foo2.foo();
	}

	

}
