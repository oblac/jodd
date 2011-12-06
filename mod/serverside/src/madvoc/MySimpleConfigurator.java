// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.config.AutomagicMadvocConfigurator;

/**
 * Simple manual configuration.
 */
public class MySimpleConfigurator extends AutomagicMadvocConfigurator {

	@Override
	public void configure() {
		super.configure();
		System.out.println("MySimpleConfigurator.configure");

		// manual action configuration
		actionsManager.register(IncognitoRequest.class, "hello", "/incognito.html");

		// result aliasing
		madvocConfig.registerPathAlias("/hello.all", "/hi-all");
	}
}
