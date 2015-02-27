// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.inv;

public class ThreeImpl implements Three {

	public void invinterface(String what) {
		System.out.println((new StringBuilder()).append("*** ").append(what).toString());
	}
}