// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import jodd.bean.BeanTests;
import jodd.cache.CacheTests;
import jodd.format.FormatTests;
import jodd.introspector.IntrospectorTests;
import jodd.io.IoTests;
import jodd.props.PropsTests;
import jodd.servlet.ServletTests;
import jodd.typeconverter.TypeConverterTests;
import jodd.util.UtilFastTests;
import junit.framework.TestSuite;

/**
 * Faster test suite test. Convenient for quick tests in IntelliJ
 * with code-coverage turned on.
 */
public class TestJoddFast {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Jodd Java Library Test Suite");
		suite.addTest(BeanTests.suite());
		suite.addTest(CacheTests.suite());
		suite.addTest(IntrospectorTests.suite());
		suite.addTest(IoTests.suite());
		suite.addTest(FormatTests.suite());
		suite.addTest(PropsTests.suite());
		suite.addTest(ServletTests.suite());
		suite.addTest(UtilFastTests.suite());
		suite.addTest(TypeConverterTests.suite());
		suite.addTest(PropsTests.suite());
		return suite;
	}
}
