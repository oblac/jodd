// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.util.ref.ReferenceCollectionsTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Faster test version for util package.
 */
public class UtilFastTests extends TestSuite {

	public UtilFastTests() {
		super("jodd.util test suite");
		addTestSuite(StringUtilTest.class);
		addTestSuite(StackQueueTest.class);
		addTestSuite(ReflectUtilTest.class);
		addTestSuite(ObjectUtilTest.class);
		addTestSuite(ArraysUtilTest.class);
		addTestSuite(WildcardTest.class);
		addTestSuite(ReferenceCollectionsTest.class);
		addTestSuite(ClassLoaderUtilTest.class);
		addTestSuite(LocaleUtilTest.class);
		addTestSuite(CharUtilTest.class);
		addTestSuite(LoopIteratorTest.class);
		addTestSuite(CsvUtilTest.class);
		addTestSuite(TextUtilTest.class);
		addTestSuite(PropertiesUtilTest.class);
		addTestSuite(Base64Test.class);
		addTestSuite(Base32Test.class);
		addTestSuite(PrettyStringBuilderTest.class);
		addTestSuite(StringBandTest.class);
		addTestSuite(BinarySearchTest.class);
		addTestSuite(MathUtilTest.class);
	}

	public static Test suite() {
		return new UtilFastTests();
	}
}