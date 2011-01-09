// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.testdata2;

import jodd.util.testdata.C;

public class D extends C {

	public D() {
		super.setProtected();
		super.setPublic();
		this.setProtected();
		this.setPublic();
	}
}
