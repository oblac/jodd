// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test;

import jodd.petite.meta.PetiteBean;

@PetiteBean
public class Foo {

	public static int instanceCounter;

	int counter;

	public Foo() {
		instanceCounter++;
		counter = 0;
	}

	public int hello() {
		return instanceCounter;
	}


	public int getCounter() {
		return counter;
	}
}
