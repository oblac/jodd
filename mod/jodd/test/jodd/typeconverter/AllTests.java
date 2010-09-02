// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {
	public AllTests() {
		super("jodd.typeconverter test suite");
		addTestSuite(BigDecimalConverterTest.class);
		addTestSuite(BigIntegerConverterTest.class);
		addTestSuite(BooleanArrayConverterTest.class);
		addTestSuite(BooleanConverterTest.class);
		addTestSuite(IntegerConverterTest.class);
		addTestSuite(MutableIntegerConverterTest.class);
		addTestSuite(StringConverterTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}
	
}
