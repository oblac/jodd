// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.runner.LoadingTestCollector;

import java.lang.reflect.Modifier;
import java.util.Enumeration;

/**
 * Jodd JUnit TestSuite.
 */
public class TestJodd {

	public static Test suite() {
		TestJodd test = new TestJodd();
		return test.processAllTests("Jodd Test Suite");
	}

	public TestSuite processAllTests(String testSuiteName) {
		TestSuite suite = new TestSuite(testSuiteName);

		Enumeration testClasses = new LoadingTestCollector().collectTests();

		while (testClasses.hasMoreElements()) {
			String testClassName = testClasses.nextElement().toString();
			Class testClass;
			try {
				testClass = Class.forName(testClassName);
				if (Modifier.isAbstract(testClass.getModifiers())) {
					continue;
				}
				if (testClass == this.getClass()) {
					continue;
				}
				if (acceptTestClass(testClass) == false) {
					continue;
				}
			} catch (ClassNotFoundException cnfex) {
				System.err.println("Test class not found:" + testClassName);
				continue;
			}
			suite.addTestSuite(testClass);
		}
		System.out.println("Total test cases: " + suite.countTestCases());

		return suite;
	}

	protected boolean acceptTestClass(Class testClass) {
		if (testClass == TestJoddFast.class) {
			return false;
		}
		return true;
	}
}
