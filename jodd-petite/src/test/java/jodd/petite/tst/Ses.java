// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.tst;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteDestroyMethod;
import jodd.petite.scope.SessionScope;

@PetiteBean(scope = SessionScope.class)
public class Ses {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@PetiteDestroyMethod
	public void ciao() {
		value = "-" + value;
	}
}