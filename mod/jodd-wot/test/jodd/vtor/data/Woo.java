package jodd.vtor.data;

import jodd.vtor.constraint.AssertValid;

public class Woo {

	@AssertValid(profiles = "*")
	protected Zoo zoo = new Zoo();
}
