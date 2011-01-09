// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci;

@Role
public class MyRole {

	private Entity self;

	public void doStuffInContext() {
		System.out.println(self.getFoo());
	}
}
