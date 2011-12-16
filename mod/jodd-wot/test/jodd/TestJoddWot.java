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
public class TestJoddWot {

	public static Test suite() {
		TestSuite suite = new TestSuite("Jodd WOT Test Suite");

		Enumeration testClasses = new LoadingTestCollector().collectTests();

		while (testClasses.hasMoreElements()) {
			String testClassName = testClasses.nextElement().toString();
			System.out.println(testClassName);
			Class testClass;
			try {
				testClass = Class.forName(testClassName);
				if (Modifier.isAbstract(testClass.getModifiers())) {
					continue;
				}
				if (testClass == TestJoddWot.class) {
					continue;
				}
			} catch (ClassNotFoundException cnfex) {
				System.err.println("Test class not found:" + testClassName);
				continue;
			}
			suite.addTestSuite(testClass);
		}
		System.out.println("Total jodd-wot test cases: " + suite.countTestCases());

		return suite;
	}
}
