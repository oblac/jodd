// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {

	protected boolean[] arr(boolean... v) {
		return v;
	}
	protected String[] arr(String... v) {
		return v;
	}
	protected int[] arri(int... v) {
		return v;
	}
	protected double[] arrd(double... v) {
		return v;
	}

}
