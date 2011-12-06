// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DecoraParserTests extends TestSuite {

	public DecoraParserTests() {
		super("jodd.decora.parser test suite");
		addTestSuite(DecoraParserTest.class);
	}

	public static Test suite() {
		return new DecoraParserTests();
	}
}
