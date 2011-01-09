// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.config.AutomagicMadvocConfigurator;
import jodd.madvoc.component.MadvocConfig;
import jodd.petite.meta.PetiteInject;

/**
 * Simple manual configuration.
 */
public class MySimpleConfigurator extends AutomagicMadvocConfigurator {

	@PetiteInject
	MadvocConfig madvocConfig;

	@Override
	public void configure() {
		super.configure();
		System.out.println("MySimpleConfigurator.configure");

		// manual action configuration
		actionsManager.register(IncognitoRequest.class, "hello", "/incognito.html");

		// result aliasing
		madvocConfig.registerResultAlias("/hello.all", "/hi-all");
	}
}
