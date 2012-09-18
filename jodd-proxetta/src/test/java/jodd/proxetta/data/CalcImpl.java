// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public class CalcImpl implements Calc {

	public void hello() {
		System.out.println("calculator");
	}

	public int calculate(int a, int b) {
		return a + b;
	}

	public void customMethod() {
		System.out.println("custom");
	}
}
