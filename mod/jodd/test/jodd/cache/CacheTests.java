// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.cache;

import junit.framework.TestSuite;
import junit.framework.Test;

public class CacheTests extends TestSuite {

	public CacheTests() {
		super("jodd.cache test suite");
		addTestSuite(LFUCacheTest.class);
		addTestSuite(LRUCacheTest.class);
		addTestSuite(FIFOCacheTest.class);
		addTestSuite(ConcurrencyTest.class);
	}

	public static Test suite() {
		return new CacheTests();
	}

}
