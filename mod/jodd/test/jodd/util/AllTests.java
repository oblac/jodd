// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.Test;
import junit.framework.TestSuite;
import jodd.util.collection.IntHashMapTest;
import jodd.util.collection.PrimitivearraysTest;
import jodd.util.collection.CompositeIteratorTest;
import jodd.util.ref.ReferenceCollectionsTest;

public class AllTests extends AllTestsFast {

	public AllTests() {
		super();
		addTestSuite(MutexTest.class);
	}

	@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
	public static Test suite() {
		return new AllTests();
	}

}