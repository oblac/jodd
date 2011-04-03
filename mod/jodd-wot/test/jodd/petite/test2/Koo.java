// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test2;

import jodd.petite.meta.PetiteInject;

public class Koo {

//	@PetiteInject
//	public Koo(Joo joo) {
//		this.joo = joo;
//	}

	@PetiteInject
	public void injectMee(Joo joo, Joo joo2) {
		mjoo = joo;
		mjoo2 = joo2;
	}

	public Joo mjoo;
	public Joo mjoo2;


	@PetiteInject
	public Joo joo;

	@PetiteInject
	public Joo someNoJooName;

}
