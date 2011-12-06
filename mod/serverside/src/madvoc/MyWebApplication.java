// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc;

import jodd.madvoc.petite.PetiteWebApplication;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.servlet.upload.impl.AdaptiveFileUploadFactory;

import javax.servlet.ServletContext;
import java.util.Properties;

/**
 * Custom web application.
 */
public class MyWebApplication extends PetiteWebApplication {

	@Override
	protected void initWebApplication() {
		System.out.println("MyWebApplication.initWebApplication");
		super.initWebApplication();
	}

	@Override
	public void registerMadvocComponents() {
		System.out.println("MyWebApplication.registerMadvocComponents");
		super.registerMadvocComponents();
		registerComponent(MyMadvocConfig.class);
		registerComponent(MyRewriter.class);
	}

	@Override
	protected void init(MadvocConfig madvocConfig, ServletContext context) {
		System.out.println("MyWebApplication.init (" + madvocConfig.getClass().getSimpleName() + ')');
		super.init(madvocConfig, context);
		((AdaptiveFileUploadFactory) madvocConfig.getFileUploadFactory()).setBreakOnError(true);
	}


	@Override
	protected void defineParams(Properties properties) {
		System.out.println("MyWebApplication.initParams " + properties.size());
		super.defineParams(properties);
	}

	@Override
	protected void initActions(ActionsManager actionManager) {
		System.out.println("MyWebApplication.initActions");
		super.initActions(actionManager);
	}

	@Override
	protected void initResults(ResultsManager actionManager) {
		System.out.println("MyWebApplication.initResults");
		super.initResults(actionManager);
	}

	@Override
	public void configure(MadvocConfigurator configurator) {
		System.out.println("MyWebApplication.configure");
		super.configure(configurator);
	}

	@Override
	protected void destroy(MadvocConfig madvocConfig) {
		System.out.println("MyWebApplication.destroy");
		super.destroy(madvocConfig);
	}
}
