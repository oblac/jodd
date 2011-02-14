// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta;

import junit.framework.TestSuite;
import junit.framework.Test;

public class ProxettaTests extends TestSuite {

	public ProxettaTests() {
		super("jodd.proxetta test suite");
		addTestSuite(SubclassTest.class);
		addTestSuite(BigClassTest.class);
		addTestSuite(AbstractsTest.class);
		addTestSuite(MethrefTest.class);
		addTestSuite(InvReplTest.class);
	}

	public static Test suite() {
		return new ProxettaTests();
	}

}