// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PropsTests extends TestSuite {

	public PropsTests() {
		super("jodd.props test suite");
		addTestSuite(PropsTest.class);
	}

	public static Test suite() {
		return new PropsTests();
	}

}