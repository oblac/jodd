// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor.data;

import jodd.vtor.constraint.AssertValid;

public class Woo {

	@AssertValid(profiles = "*")
	protected Zoo zoo = new Zoo();
}
