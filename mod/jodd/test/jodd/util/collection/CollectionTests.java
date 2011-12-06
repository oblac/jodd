// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CollectionTests extends TestSuite {

	public CollectionTests() {
		super("jodd.util.collection test suite");
		addTestSuite(IntHashMapTest.class);
		addTestSuite(ArrayEnumerationTest.class);
		addTestSuite(CompositeIteratorTest.class);
		addTestSuite(PrimitivearraysTest.class);
	}

	public static Test suite() {
		return new CollectionTests();
	}
}
