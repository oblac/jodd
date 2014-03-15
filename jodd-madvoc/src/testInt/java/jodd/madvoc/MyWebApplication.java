// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.log.impl.SimpleLoggerFactory;
import jodd.madvoc.action.HelloAction;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.madvoc.injector.BaseScopeInjector;
import jodd.madvoc.petite.PetiteWebApplication;

import javax.servlet.ServletContext;
import java.util.HashSet;

public class MyWebApplication extends PetiteWebApplication {

	public MyWebApplication() {
		LoggerFactory.setLoggerFactory(new SimpleLoggerFactory(Logger.Level.DEBUG));
	}

	@Override
	public void registerMadvocComponents() {
		super.registerMadvocComponents();

		registerComponent(MyRewriter.class);
	}

	@Override
	protected void init(MadvocConfig madvocConfig, ServletContext servletContext) {
		super.init(madvocConfig, servletContext);

		madvocConfig.getRootPackages().addRootPackageOf(HelloAction.class);
	}

	@Override
	public void configure(MadvocConfigurator configurator) {
		super.configure(configurator);

		BaseScopeInjector.set = new HashSet<String>();
	}
}