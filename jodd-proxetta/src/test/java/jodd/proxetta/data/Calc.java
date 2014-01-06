// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.data;

public interface Calc {

	void hello();

	int calculate(int a, int b);

	double calculate(double a, double b);

	long calculate(long a, long b);

	float calculate(float a, float b);

	short calculate(short a, short b);

	byte calculate(byte a, byte b);
}
