// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.paramo;

import junit.framework.TestSuite;
import junit.framework.Test;

public class ParamoTests extends TestSuite {

	public ParamoTests() {
		super("jodd.paramo test suite");
		addTestSuite(ParamoTest.class);
	}

	public static Test suite() {
		return new ParamoTests();
	}

}
