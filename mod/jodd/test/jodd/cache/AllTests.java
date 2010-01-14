// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.cache test suite");
		addTestSuite(LFUCacheTest.class);
		addTestSuite(LRUCacheTest.class);
		addTestSuite(FIFOCacheTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}
