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
import jodd.madvoc.component.MadvocListener;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.madvoc.component.ServletContextProvider;
import jodd.props.Props;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Web application contains all configurations and holds all managers and controllers of one web application.
 * Custom implementations may override this class to enhance several different functionality.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class WebApp {

	private static Logger log;

	public static WebApp createWebApp() {
		return new WebApp();
	}

	// ---------------------------------------------------------------- builder

	protected ServletContext servletContext;
	private List<Props> propsList = new ArrayList<>();
	private List<Class> madvocComponents = new ArrayList<>();

	/**
	 * Defines params to load.
	 */
	public WebApp withParams(Props props) {
		propsList.add(props);
		return this;
	}

	/**
	 * Defines servlet context. Must be called in the web environment.
	 */
	public WebApp withServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		return this;
	}

	/**
	 * Registers additional Madvoc components that will be registered after default components.
	 */
	public WebApp withMadvocComponent(Class madvocComponent) {
		madvocComponents.add(madvocComponent);
		return this;
	}

	// ---------------------------------------------------------------- main components

	protected MadvocConfig madvocConfig;
	protected MadvocContainer madvocContainer;

	/**
	 * Returns {@link MadvocConfig Madvoc config} once web application is started.
	 */
	public MadvocConfig madvocConfig() {
		return madvocConfig;
	}

	/**
	 * Returns {@link MadvocContainer Madvoc container}.
	 */
	public MadvocContainer madvocContainer() {
		return madvocContainer;
	}


	// ---------------------------------------------------------------- lifecycle

	/**
	 * Initializes and starts web application.
	 */
	public WebApp start() {
		log = LoggerFactory.getLogger(WebApp.class);

		log.debug("Initializing Madvoc WebApp");

		madvocContainer = new MadvocContainer();
		madvocContainer.registerComponentInstance(madvocContainer);

		//// props
		for (Props props : propsList) {
			madvocContainer.defineParams(props);
		}
		propsList = null;

		//// config
		madvocContainer.registerComponent(MadvocConfig.class);
		madvocConfig = madvocContainer.requestComponent(MadvocConfig.class);

		configureMadvoc();

		//// components
		registerMadvocComponents();

		for (Class madvocComponent : madvocComponents) {
			madvocContainer.registerComponent(madvocComponent);
		}
		madvocComponents = null;

		//// listeners
		madvocContainer.fireEvent(MadvocListener.Init.class);

		initalized();

		madvocContainer.fireEvent(MadvocListener.Start.class);

		started();

		madvocContainer.fireEvent(MadvocListener.Ready.class);

		ready();

		return this;
	}

	/**
	 * Hook for manual Madvoc configuration.
	 */
	protected void configureMadvoc() {
	}

	/**
	 * Registers default Madvoc components.
	 */
	protected void registerMadvocComponents() {
		if (madvocContainer == null) {
			throw new MadvocException("Madvoc WebApp not initialized.");
		}

		log.debug("Registering Madvoc WebApp components");

		madvocContainer.registerComponentInstance(new ServletContextProvider(servletContext));

		madvocContainer.registerComponent(ActionMethodParamNameResolver.class);
		madvocContainer.registerComponent(ActionMethodParser.class);
		madvocContainer.registerComponent(ActionPathRewriter.class);
		madvocContainer.registerComponent(ActionPathMacroManager.class);
		madvocContainer.registerComponent(ActionsManager.class);
		madvocContainer.registerComponent(ContextInjectorComponent.class);
		madvocContainer.registerComponent(InjectorsManager.class);
		madvocContainer.registerComponent(InterceptorsManager.class);
		madvocContainer.registerComponent(FiltersManager.class);
		madvocContainer.registerComponent(MadvocController.class);
		madvocContainer.registerComponent(ResultsManager.class);
		madvocContainer.registerComponent(ResultMapper.class);
		madvocContainer.registerComponent(ScopeDataResolver.class);
	}

	/**
	 * Called when Madvoc is initialized, at the end of the {@link MadvocListener.Init INIT} phase.
	 */
	protected void initalized() {
	}
	/**
	 * Called when Madvoc is started, at the end of the {@link MadvocListener.Start START} phase.
	 */

	protected void started() {
	}

	/**
	 * Called when Madvoc is ready, at the end of the {@link MadvocListener.Ready READY} phase.
	 */
	protected void ready() {
	}

	/**
	 * Shutdows the web application. Triggers the STOP event.
	 */
	public void shutdown() {
		log.info("Madvoc shutting down...");

		madvocContainer.fireEvent(MadvocListener.Stop.class);
	}

}
