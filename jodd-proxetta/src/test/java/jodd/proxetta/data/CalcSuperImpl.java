// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public class CalcSuperImpl implements CalcSuper {

	public void superhi() {
		System.out.println("superhi");
	}

	public void hello() {
		System.out.println("hello");
	}

	public int calculate(int a, int b) {
		return a + b;
	}

	public void ola() {
		System.out.println("ola!");
	}

	public int maybe(int a, int b) {
		return a + b - (a - b);
	}
}
