// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.component.ActionMethodParamNameResolver;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionPathMacroManager;
import jodd.madvoc.component.ActionPathRewriter;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.ContextInjectorComponent;
import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.component.InjectorsManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.MadvocContainer;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.madvoc.component.ServletContextProvider;
import jodd.madvoc.config.MadvocConfigurator;

import javax.servlet.ServletContext;

/**
 * Web application contains all configurations and holds all managers and controllers of one web application.
 * Custom implementations may override this class to enhance several different functionality.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class WebApplication {

	private static Logger log;

	protected MadvocContainer mcc;

	/**
	 * Initializes web application. Invoked very first.
	 */
	public MadvocContainer init() {
		log = LoggerFactory.getLogger(WebApplication.class);
		log.debug("Initializing Madvoc web application");

		mcc = new MadvocContainer();

		return mcc;
	}

	public void registerMadvocComponents() {
		this.registerMadvocComponents(null);
	}

	/**
	 * Registers default Madvoc components.
	 * Invoked before {@link #init(MadvocConfig , ServletContext) madvoc initialization}.
	 */
	public void registerMadvocComponents(ServletContext servletContext) {
		if (mcc == null) {
			throw new MadvocException("WebApp not initialized. Call init() first!");
		}
		log.debug("Registering Madvoc components");

		mcc.registerComponent(ActionMethodParamNameResolver.class);
		mcc.registerComponent(ActionMethodParser.class);
		mcc.registerComponent(ActionPathRewriter.class);
		mcc.registerComponent(ActionPathMacroManager.class);
		mcc.registerComponent(ActionsManager.class);
		mcc.registerComponent(ContextInjectorComponent.class);
		mcc.registerComponent(InjectorsManager.class);
		mcc.registerComponent(InterceptorsManager.class);
		mcc.registerComponent(FiltersManager.class);
		mcc.registerComponent(MadvocConfig.class);
		mcc.registerComponent(MadvocController.class);
		mcc.registerComponent(ResultsManager.class);
		mcc.registerComponent(ResultMapper.class);
		mcc.registerComponent(ScopeDataResolver.class);
		mcc.registerComponent(new ServletContextProvider(servletContext));
	}

	// ---------------------------------------------------------------- lifecycle

	/**
	 * Initializes web application custom configuration.
	 * When running web application out from container,
	 * <code>servletContext</code> may be <code>null</code>.
	 * todo remove!
	 */
	@Deprecated
	protected void init(MadvocConfig madvocConfig, ServletContext servletContext) {
		log.debug("Initializing Madvoc");
	}

	/**
	 * Invoked on web application destroy.
	 * todo remove!
	 */
	@Deprecated
	protected void destroy(MadvocConfig madvocConfig) {
		log.debug("Destroying Madvoc");
	}

	// ---------------------------------------------------------------- configurator

	/**
	 * Adds configurator to Madvoc container and invokes configuration.
	 * todo fix this, make it better, remove this here.
	 */
	public void configure(MadvocConfigurator configurator) {
		log.debug("Configuring Madvoc");

		mcc.registerComponent(configurator);

		configurator.configure();
	}
}
