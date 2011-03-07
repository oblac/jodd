// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import junit.framework.Test;
import junit.framework.TestSuite;

public class IoTests extends TestSuite {

	public IoTests() {
		super("jodd.io test suite");
		addTestSuite(StreamUtilTest.class);
		addTestSuite(FileUtilTest.class);
		addTestSuite(FilepathScannerTest.class);
		addTestSuite(FindFileTest.class);
	}

	public static Test suite() {
		return new IoTests();
	}

}