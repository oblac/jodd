// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.Test;
import junit.framework.TestSuite;
import jodd.util.collection.IntHashMapTest;
import jodd.util.collection.PrimitivearraysTest;
import jodd.util.collection.CompositeIteratorTest;
import jodd.util.ref.ReferenceCollectionsTest;

public class AllTests extends TestSuite {

	public AllTests() {
		super("jodd.util test suite");
		addTestSuite(IntHashMapTest.class);
		addTestSuite(StringUtilTest.class);
		addTestSuite(StackQueueTest.class);
		addTestSuite(MutexTest.class);
		addTestSuite(ReflectUtilTest.class);
		addTestSuite(ObjectUtilTest.class);
		addTestSuite(ArraysUtilTest.class);
		addTestSuite(WildcardTest.class);
		addTestSuite(ReferenceCollectionsTest.class);
		addTestSuite(PrimitivearraysTest.class);
		addTestSuite(ClassLoaderUtilTest.class);
		addTestSuite(LocaleUtilTest.class);
		addTestSuite(CompositeIteratorTest.class);
		addTestSuite(CharUtilTest.class);
		addTestSuite(LoopIteratorTest.class);
		addTestSuite(CsvUtilTest.class);
	}

	public static Test suite() {
		return new AllTests();
	}

}