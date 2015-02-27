// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.tst5;

import jodd.petite.meta.PetiteInject;

public class Sun {

	@PetiteInject
	Planet planet;

	@Override
	public String toString() {
		return "Sun{" + planet + '}';
	}

}