// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestSuite;
import junit.framework.Test;
import jodd.util.collection.IntHashMapTest;
import jodd.util.collection.PrimitivearraysTest;
import jodd.util.collection.CompositeIteratorTest;
import jodd.util.ref.ReferenceCollectionsTest;

/**
 * Faster test version for
 */
public class AllTestsFast extends TestSuite {

	public AllTestsFast() {
		super("jodd.util test suite");
		addTestSuite(IntHashMapTest.class);
		addTestSuite(StringUtilTest.class);
		addTestSuite(StackQueueTest.class);
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
		addTestSuite(TextUtilTest.class);
	}

	public static Test suite() {
		return new AllTestsFast();
	}
}