// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public class CalcImpl extends CalcImplBase implements Calc {

	public int calculate(int a, int b) {
		return a + b;
	}

	public double calculate(double a, double b) {
		return a + b;
	}

	public long calculate(long a, long b) {
		return a + b;
	}

	public float calculate(float a, float b) {
		return a + b;
	}

	public short calculate(short a, short b) {
		return (short) (a + b);
	}

	public byte calculate(byte a, byte b) {
		return (byte) (a + b);
	}

	public void customMethod() {
		System.out.println("custom");
	}

	public static void main(String[] args) {
		System.out.println(args);
	}
}
