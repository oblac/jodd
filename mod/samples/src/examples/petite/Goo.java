// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.petite;

import jodd.petite.meta.PetiteBean;

@PetiteBean
public class Goo {

	public void goo() {
		System.out.println("goo! " + toString());
	}
}
