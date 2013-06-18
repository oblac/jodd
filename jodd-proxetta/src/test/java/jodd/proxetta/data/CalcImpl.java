// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public class CalcImpl extends CalcImplBase implements Calc {

	public int calculate(int a, int b) {
		return a + b;
	}

	public void customMethod() {
		System.out.println("custom");
	}

	public static void main(String[] args) {
		System.out.println(args);
	}
}
