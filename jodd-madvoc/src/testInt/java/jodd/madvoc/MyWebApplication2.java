// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.log.impl.SimpleLoggerFactory;

public class MyWebApplication2 extends WebApplication {

	public MyWebApplication2() {
		LoggerFactory.setLoggerFactory(new SimpleLoggerFactory(Logger.Level.DEBUG));
	}
}