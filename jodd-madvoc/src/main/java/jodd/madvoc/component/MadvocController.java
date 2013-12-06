// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.servlet.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

/**
 * Madvoc controller invokes actions for action path and renders action results.
 * It also builds action objects and result paths. It handles initialization of
 * interceptors and results.
 */
public class MadvocController {

	private static final Logger log = LoggerFactory.getLogger(MadvocController.class);

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ActionPathMapper actionPathMapper;

	@PetiteInject
	protected ActionPathRewriter actionPathRewriter;

	@PetiteInject
	protected InterceptorsManager interceptorsManager;

	@PetiteInject
	protected ServletContextInjector servletContextInjector;

	@PetiteInject
	protected ResultsManager resultsManager;

	@PetiteInject
	protected ResultMapper resultMapper;

	protected ServletContext applicationContext;

	/**
	 * Initializes controller by providing application context.
	 */
	public void init(ServletContext servletContext) {
		this.applicationContext = servletContext;
	}

	/**
	 * Returns application context set during the initialization.
	 */
	public ServletContext getApplicationContext() {
		return applicationContext;
	}

	// ---------------------------------------------------------------- invoke


	/**
	 * Invokes action registered to provided action path, Provides action chaining, by invoking the next action request.
	 * Returns <code>null</code> if action path is consumed and has been invoked by this controller; otherwise
	 * the action path string is returned (it might be different than original one, provided in arguments).
	 * On first invoke, initializes the action configuration before further proceeding.
	 *
	 * @see jodd.madvoc.component.ActionMethodParser#buildActionPath(String, String, String, String, String)
	 */
	public String invoke(String actionPath, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
		ActionRequest actionRequest = null;

		boolean characterEncodingSet = false;

		while (actionPath != null) {
			if (log.isDebugEnabled()) {
				log.debug("Action path: " + actionPath);
			}

			// build action path
			String httpMethod = servletRequest.getMethod().toUpperCase();
			actionPath = actionPathRewriter.rewrite(servletRequest, actionPath, httpMethod);

			// resolve action configuration
			ActionConfig actionConfig = resolveActionConfig(actionPath, httpMethod);
			if (actionConfig == null) {
				return actionPath;
			}
			if (log.isDebugEnabled()) {
				log.debug("Invoking action path '" + actionPath + "' using " + actionConfig.actionClass.getSimpleName());
			}

			// set character encoding
			if (!characterEncodingSet && madvocConfig.isApplyCharacterEncoding()) {

				String encoding = madvocConfig.getEncoding();

				if (encoding != null) {
					servletRequest.setCharacterEncoding(encoding);
					servletResponse.setCharacterEncoding(encoding);
				}

				characterEncodingSet = true;
			}

			// create action object
			Object action = createAction(actionConfig.actionClass);

			// create action request
			ActionRequest previousRequest = actionRequest;
			actionRequest = createActionRequest(actionPath, actionConfig, action, servletRequest, servletResponse);
			actionRequest.setPreviousActionRequest(previousRequest);

			// invoke and render
			actionRequest.invoke();

			actionPath = actionRequest.getNextActionPath();
		}
		return null;
	}


	// ---------------------------------------------------------------- render

	/**
	 * Invokes a result after the action invocation.
	 * <p>
	 * Result value consist of two parts: type and value. Result type is optional and, if exists, it is separated
	 * by semi-colon from the value. If type is not specified, the annotation value will be used first,
	 * and then the default result type if still not defined. Result type defines which
	 * {@link ActionResult} should be used for rendering the value.
	 * <p>
	 * Result value is first checked against aliased values. Then, it is resolved and then passed
	 * to the founded {@link ActionResult}.
	 *
	 * @see ActionResult#render(jodd.madvoc.ActionRequest, Object, String, String)
	 */
	public void render(ActionRequest actionRequest, Object resultObject) throws Exception {
		String resultValue = resultObject != null ? resultObject.toString() : null;
		String resultType = null;

		// first check result value
		if (resultValue != null) {
			int columnIndex = resultValue.indexOf(':');

			if (columnIndex != -1) {
				resultType = resultValue.substring(0, columnIndex);

				resultValue = resultValue.substring(columnIndex + 1);
			}
		}

		// result type still not set, read config
		if (resultType == null) {
			resultType = actionRequest.getActionConfig().getResultType();

			// result type still not defined, use default
			if (resultType == null) {
				resultType = madvocConfig.getDefaultResultType();
			}
		}

		ActionResult result = resultsManager.lookup(resultType);
		if (result == null) {
			throw new MadvocException("Unable to find action result type: " + resultType);
		}
		if (result.isInitialized() == false) {
			initializeResult(result, actionRequest);
		}
		if (madvocConfig.isPreventCaching()) {
			ServletUtil.preventCaching(actionRequest.getHttpServletResponse());
		}
		String resultPath = resultMapper.resolveResultPath(actionRequest.getActionConfig(), resultValue);
		result.render(actionRequest, resultObject, resultValue, resultPath);
	}

	/**
	 * Initializes action result.
	 */
	protected void initializeResult(ActionResult result, ActionRequest actionRequest) {
		HttpServletRequest httpServletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse httpServletResponse = actionRequest.getHttpServletResponse();

		servletContextInjector.injectContext(result, httpServletRequest, httpServletResponse);

		result.initialized();
		result.init();
	}

	// ---------------------------------------------------------------- create

	/**
	 * Resolves action config from action path and http method. Returns <code>null</code>
	 * if action config not found. Performs initialization of founded action config,
	 * if necessary.
	 */
	protected ActionConfig resolveActionConfig(String actionPath, String httpMethod) {
		ActionConfig actionConfig = actionPathMapper.resolveActionConfig(actionPath, httpMethod);
		if (actionConfig != null) {
			if (actionConfig.initialized == false) {
				initializeActionConfig(actionConfig);
			}
		}
		return actionConfig;
	}

	/**
	 * Initializes action configuration on first use. Resolves all interceptors and injects context parameters.
	 */
	// todo remove this moment on action config creation to early warn about missconfiguration
	protected void initializeActionConfig(ActionConfig actionConfig) {
		Class<? extends ActionInterceptor>[] interceptorClasses = actionConfig.interceptorClasses;
		if (interceptorClasses == null) {
			interceptorClasses = madvocConfig.getDefaultInterceptors();
		}

		if (interceptorClasses != null) {

			ActionInterceptor[] allInterceptors = interceptorsManager.resolveAll(interceptorClasses);

			actionConfig.filters = interceptorsManager.extractActionFilters(allInterceptors);
			actionConfig.interceptors = interceptorsManager.extractActionInterceptor(allInterceptors);

			for (ActionInterceptor interceptor : actionConfig.filters) {
				if (interceptor.isInitialized() == false) {
					initializeInterceptor(interceptor);
				}
			}

			for (ActionInterceptor interceptor : actionConfig.interceptors) {
				if (interceptor.isInitialized() == false) {
					initializeInterceptor(interceptor);
				}
			}
		}

		actionConfig.initialized();
	}

	/**
	 * Initializes action interceptor.
	 */
	protected void initializeInterceptor(ActionInterceptor interceptor) {
		servletContextInjector.injectContext(interceptor, applicationContext);

		interceptor.initialized();
		interceptor.init();
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new action object from {@link ActionConfig} using default constructor.
	 */
	protected Object createAction(Class actionClass) {
		try {
			return actionClass.newInstance();
		} catch (Exception ex) {
			throw new MadvocException("Unable to create Madvoc action.", ex);
		}
	}

	/**
	 * Creates new action request.
	 * @param actionPath		action path
	 * @param actionConfig		action configuration
	 * @param action			action object
	 * @param servletRequest	http request
	 * @param servletResponse	http response
	 * @return action request
	 */
	protected ActionRequest createActionRequest(String actionPath, ActionConfig actionConfig, Object action, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		return new ActionRequest(this, actionPath, actionConfig, action, servletRequest, servletResponse);
	}

}