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
import jodd.madvoc.component.ActionConfigManager;
import jodd.madvoc.component.ActionMethodParamNameResolver;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionPathRewriter;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.AsyncActionExecutor;
import jodd.madvoc.component.ContextInjectorComponent;
import jodd.madvoc.component.FileUploader;
import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.MadvocComponentLifecycle;
import jodd.madvoc.component.MadvocComponentLifecycle.Init;
import jodd.madvoc.component.MadvocComponentLifecycle.Ready;
import jodd.madvoc.component.MadvocComponentLifecycle.Start;
import jodd.madvoc.component.MadvocContainer;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.MadvocEncoding;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.component.RootPackages;
import jodd.madvoc.component.ScopeDataInspector;
import jodd.madvoc.component.ScopeResolver;
import jodd.madvoc.component.ServletContextProvider;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.RestAction;
import jodd.props.Props;
import jodd.util.ClassConsumer;
import jodd.util.function.Consumers;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Web application contains all configurations and holds all managers and controllers of one web application.
 * Custom implementations may override this class to enhance several different functionality.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class WebApp {

	private static Logger log;

	private static final String WEBAPP_ATTR = WebApp.class.getName();

	public static WebApp createWebApp() {
		return new WebApp();
	}

	/**
	 * Returns <code>WebApp</code> instance from servlet context.
	 * May return <code>null</code> indicating <code>WebApp</code>
	 * is not yet initialized.
	 */
	public static WebApp get(final ServletContext servletContext) {
		return (WebApp) servletContext.getAttribute(WEBAPP_ATTR);
	}


	// ---------------------------------------------------------------- builder

	protected ServletContext servletContext;
	private List<Props> propsList = new ArrayList<>();
	private List<Map<String, Object>> paramsList = new ArrayList<>();
	private List<ClassConsumer> madvocComponents = new ArrayList<>();
	private List<Object> madvocComponentInstances = new ArrayList<>();
	private Consumers<MadvocRouter> madvocRouterConsumers = Consumers.empty();

	/**
	 * Defines params to load.
	 */
	public WebApp withParams(final Props props) {
		propsList.add(props);
		return this;
	}

	public WebApp withParams(final Map<String, Object> params) {
		paramsList.add(params);
		return this;
	}

	/**
	 * Defines servlet context. Must be called in the web environment.
	 */
	public WebApp bindServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
		this.servletContext.setAttribute(WEBAPP_ATTR, this);
		return this;
	}

	/**
	 * Registers additional Madvoc components after the registration of default components.
	 */
	public WebApp registerComponent(final Class<?> madvocComponent) {
		Objects.requireNonNull(madvocComponent);
		madvocComponents.add(ClassConsumer.of(madvocComponent));
		return this;
	}

	public <T> WebApp registerComponent(final Class<T> madvocComponent, final Consumer<T> componentConsumer) {
		Objects.requireNonNull(madvocComponent);
		madvocComponents.add(ClassConsumer.of(madvocComponent, componentConsumer));
		return this;
	}

	/**
	 * Registers Madvoc component <i>instance</i>. Use with caution, as injection of
	 * components registered after this will fail.
	 */
	public WebApp registerComponent(final Object madvocComponent) {
		Objects.requireNonNull(madvocComponent);
		madvocComponentInstances.add(madvocComponent);
		return this;
	}

	/**
	 * Configures the action configurations.
	 */
	public <A extends ActionConfig> WebApp withActionConfig(final Class<A> actionConfigType, final Consumer<A> actionConfigConsumer) {
		withRegisteredComponent(ActionConfigManager.class, acm -> acm.with(actionConfigType, actionConfigConsumer));
		return this;
	}

	// ---------------------------------------------------------------- main components

	protected final MadvocContainer madvocContainer;
	protected Consumers<MadvocContainer> componentConfigs = Consumers.empty();

	public WebApp() {
		madvocContainer = new MadvocContainer();
		madvocContainer.registerComponentInstance(madvocContainer);
	}

	/**
	 * Returns {@link MadvocContainer Madvoc container} that maintain all Madvoc components.
	 */
	public MadvocContainer madvocContainer() {
		return madvocContainer;
	}

	/**
	 * Configures a component. While the signature is the same as for {@link #registerComponent(Class, Consumer)}
	 * this method does not register component, just operates on an already registered one.
	 */
	public <T> WebApp withRegisteredComponent(final Class<T> madvocComponent, final Consumer<T> componentConsumer) {
		if (componentConfigs == null) {
			// component is already configured
			final T component = madvocContainer.lookupComponent(madvocComponent);
			if (component == null) {
				throw new MadvocException("Component not found: " + madvocComponent.getName());
			}
			componentConsumer.accept(component);
		}
		else {
			componentConfigs.add(madvocContainer -> {
				final T component = madvocContainer.lookupComponent(madvocComponent);
				if (component == null) {
					throw new MadvocException("Component not found: " + madvocComponent.getName());
				}
				componentConsumer.accept(component);
			});
		}
		return this;
	}

	/**
	 * Defines a route manually using {@link MadvocRouter}.
	 */
	public WebApp router(final Consumer<MadvocRouter> madvocAppConsumer) {
		madvocRouterConsumers.add(madvocAppConsumer);
		return this;
	}

	// ---------------------------------------------------------------- lifecycle

	/**
	 * Initializes and starts web application.
	 */
	public WebApp start(final Consumer<MadvocRouter> madvocRouterConsumer) {
		madvocRouterConsumers.add(madvocRouterConsumer);
		return start();
	}

	/**
	 * Initializes and starts web application.
	 */
	public WebApp start() {
		log = LoggerFactory.getLogger(WebApp.class);

		log.debug("Initializing Madvoc WebApp");

		//// params & props
		for (final Map<String, Object> params : paramsList) {
			madvocContainer.defineParams(params);
		}
		for (final Props props : propsList) {
			madvocContainer.defineParams(props);
		}
		propsList = null;


		//// components
		registerMadvocComponents();

		madvocComponents.forEach(
			madvocComponent -> madvocContainer.registerComponent(madvocComponent.type(), madvocComponent.consumer()));
		madvocComponents = null;

		madvocComponentInstances.forEach(madvocContainer::registerComponentInstance);
		madvocComponentInstances = null;

		configureDefaults();


		//// listeners
		madvocContainer.fireEvent(Init.class);

		//// component configuration
		componentConfigs.accept(madvocContainer);
		componentConfigs = null;

		initialized();

		madvocContainer.fireEvent(Start.class);

		if (!madvocRouterConsumers.isEmpty()) {

			final MadvocRouter madvocRouter = MadvocRouter.create();

			madvocContainer.registerComponentInstance(madvocRouter);

			madvocRouterConsumers.accept(madvocRouter);
		}
		madvocRouterConsumers = null;

		started();

		madvocContainer.fireEvent(Ready.class);

		ready();

		return this;
	}

	/**
	 * Configure defaults.
	 */
	protected void configureDefaults() {
		final ActionConfigManager actionConfigManager =
			madvocContainer.lookupComponent(ActionConfigManager.class);

		actionConfigManager.registerAnnotation(Action.class);
		actionConfigManager.registerAnnotation(RestAction.class);
	}

	/**
	 * Registers default Madvoc components.
	 */
	protected void registerMadvocComponents() {
		if (madvocContainer == null) {
			throw new MadvocException("Madvoc WebApp not initialized.");
		}

		log.debug("Registering Madvoc WebApp components");

		madvocContainer.registerComponent(MadvocEncoding.class);

		madvocContainer.registerComponentInstance(new ServletContextProvider(servletContext));

		madvocContainer.registerComponent(ActionConfigManager.class);
		madvocContainer.registerComponent(ActionMethodParamNameResolver.class);
		madvocContainer.registerComponent(ActionMethodParser.class);
		madvocContainer.registerComponent(ActionPathRewriter.class);
		madvocContainer.registerComponent(ActionsManager.class);
		madvocContainer.registerComponent(ContextInjectorComponent.class);
		madvocContainer.registerComponent(InterceptorsManager.class);
		madvocContainer.registerComponent(FiltersManager.class);
		madvocContainer.registerComponent(MadvocController.class);
		madvocContainer.registerComponent(RootPackages.class);
		madvocContainer.registerComponent(ResultsManager.class);
		madvocContainer.registerComponent(ResultMapper.class);
		madvocContainer.registerComponent(ScopeResolver.class);
		madvocContainer.registerComponent(ScopeDataInspector.class);
		madvocContainer.registerComponent(AsyncActionExecutor.class);
		madvocContainer.registerComponent(FileUploader.class);
	}

	/**
	 * Called when Madvoc is initialized, at the end of the {@link Init INIT} phase.
	 * @see MadvocComponentLifecycle
	 */
	protected void initialized() {
	}

	/**
	 * Called when Madvoc is started, at the end of the {@link Start START} phase.
	 * @see MadvocComponentLifecycle
	 */
	protected void started() {
	}

	/**
	 * Called when Madvoc is ready, at the end of the {@link Ready READY} phase.
	 * @see MadvocComponentLifecycle
	 */
	protected void ready() {
	}

	/**
	 * Shutdows the web application. Triggers the STOP event.
	 * @see MadvocComponentLifecycle
	 */
	public void shutdown() {
		log.info("Madvoc shutting down...");

		madvocContainer.fireEvent(MadvocComponentLifecycle.Stop.class);
	}

}
