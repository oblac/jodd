// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.log.impl.SimpleLoggerFactory;
import jodd.madvoc.petite.PetiteWebApplication;

public class MyWebApplication extends PetiteWebApplication {

	public MyWebApplication() {
		LoggerFactory.setLoggerFactory(new SimpleLoggerFactory(Logger.Level.DEBUG));
	}

}