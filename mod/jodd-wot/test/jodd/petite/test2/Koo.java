// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.test2;

import jodd.petite.meta.PetiteInject;

public class Koo {

	@PetiteInject
	public Koo(Joo joo) {
		this.joojoo = joo;
	}
	public Joo joojoo;

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
