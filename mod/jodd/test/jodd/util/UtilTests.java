// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.util.collection.SortedArrayListTest;
import junit.framework.Test;

public class UtilTests extends UtilFastTests {

	public UtilTests() {
		super();
		addTestSuite(MutexTest.class);
		addTestSuite(SortedArrayListTest.class);
	}

	@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
	public static Test suite() {
		return new UtilTests();
	}

}