// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public class SmallSkinnyZoe {

	public void publicMethod() {
		System.out.println("SmallSkinnyZoe.publicMethod");
	}

	@Action
	@PetiteInject
	@Transaction
	public void superPublicMethod() {
		System.out.println("SmallSkinnyZoe.superPublicMethod");
	}

	protected void superProtectedMethod() {
		System.out.println("SmallSkinnyZoe.superProtectedMethod");
	}

	void superPackageMethod() {
		System.out.println("SmallSkinnyZoe.superPackageMethod");
	}
}
