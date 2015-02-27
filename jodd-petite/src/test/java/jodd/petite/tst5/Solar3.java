// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.tst5;

import jodd.petite.meta.PetiteProvider;

public class Solar3 {

	@PetiteProvider			// default name: "planet"
	public static Planet planetProvider() {
		return new Planet();
	}

}