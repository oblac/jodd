// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

public class Two {

	public Two() {
	}

	public Two(String state) {
		this.state = state;
	}

	public int invvirtual(String what) {
		System.out.print("invoke virtual " + what);
		return -17;
	}

	public static int invstatic(String what) {
		System.out.print("invoke static! " + what);
		return -13;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void printState() {
		System.out.print("state = " + state);
	}

	private String state;
}