package jodd.petite.mix;

import jodd.petite.meta.PetiteInject;

public class Big2 {

	private Small small;

	public Small getSmall() {
		return small;
	}

	@PetiteInject
	public void setSmall(Small small) {
		this.small = small;
	}

}
