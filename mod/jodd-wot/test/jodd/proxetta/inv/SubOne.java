// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

public class SubOne {

	public void sub() {
		System.out.print("####from sub");
	}

	public void callSub() {
		sub();
	}
}
