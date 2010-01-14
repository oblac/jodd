// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {
	public AllTests() {
		super("jodd.typeconverter test suite");
		addTestSuite(IntegerConverterTest.class);
		addTestSuite(MutableIntegerTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}
	
}
