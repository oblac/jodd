// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import junit.framework.TestSuite;

/**
 * Faster test suite test. Convenient for quick tests in IntelliJ
 * with code-coverage turned on.
 */
public class TestJoddFast extends TestJodd {

	@SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
	public static TestSuite suite() {
		TestJoddFast test = new TestJoddFast();
		return test.processAllTests("Jodd FAST Test Suite");
	}

	@Override
	protected boolean acceptTestClass(Class testClass) {
		if (testClass == TestJodd.class) {
			return false;
		}
		String className = testClass.getName();
		if (className.equals("jodd.util.MutexTest")) {
			return false;
		}
		String packageName = testClass.getPackage().getName();
		if (packageName.equals("jodd.datetime")) {
			return false;
		}
		return true;
	}

}
