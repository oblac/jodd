// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package examples.petite.news;

import jodd.petite.meta.PetiteBean;
import examples.petite.Boo;

@PetiteBean("boo")
public class Boo2 extends Boo {

	@Override
	public void boo() {
		goo.goo();
		System.out.println("!!!boo2 " + toString());
	}
}
