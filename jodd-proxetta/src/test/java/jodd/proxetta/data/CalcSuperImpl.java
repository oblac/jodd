// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

	public void ola() {
		System.out.println("ola!");
	}

	public int maybe(int a, int b) {
		return a + b - (a - b);
	}
}
