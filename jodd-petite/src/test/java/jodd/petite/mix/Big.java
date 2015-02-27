// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.mix;

import jodd.petite.meta.PetiteInject;

public class Big {

	@PetiteInject
	private Small small;

	public Small getSmall() {
		return small;
	}

	public void setSmall(Small small) {
		this.small = small;
	}

}
