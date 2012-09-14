// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.testdata;

public class B extends A {
	public B () {
		this.setDefault();
		this.setProtected();
		this.setPublic();
		
		super.setDefault();
		super.setProtected();
		super.setPublic();
	}
}
