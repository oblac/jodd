// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.petite;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import jodd.petite.PetiteContainer;

@PetiteBean
public class Boo {

	@PetiteInject
	protected Goo goo;

	public void boo() {
		goo.goo();
		System.out.println("boo " + toString());
	}


	public static void main(String[] args) {

		PetiteContainer petite = new PetiteContainer();
		petite.registerBean(Foo.class);
		petite.registerBean(Goo.class);
		petite.registerBean(Boo.class);

		Foo foo = (Foo) petite.getBean("foo");

		foo.foo();
	}
}
