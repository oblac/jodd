// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test2;

import jodd.petite.meta.PetiteInject;

public class Koo {

//	@PetiteInject
//	public Koo(Joo joo) {
//		this.joo = joo;
//	}

//	@PetiteInject()
//	public void injectMee(Joo joo) {
//		mjoo = joo;
//	}

	public Joo mjoo;


	@PetiteInject
	public Joo joo;

	@PetiteInject
	public Joo someNoJooName;

}
