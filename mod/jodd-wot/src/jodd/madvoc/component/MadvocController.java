// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Madvoc controller invokes actions for action path and renders action results.
 * It also builds action objects and result paths. It handles intialization of
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
	protected ContextInjector contextInjector;

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
		ActionRequest request = null;

		String encoding = madvocConfig.getEncoding();
		if (encoding != null) {
			servletRequest.setCharacterEncoding(madvocConfig.getEncoding());
			servletResponse.setCharacterEncoding(madvocConfig.getEncoding());
		}

		while (actionPath != null) {
			log.debug("Action path: {}", actionPath);

			// build action path
			String httpMethod = servletRequest.getMethod().toUpperCase();
			actionPath = actionPathRewriter.rewrite(actionPath, servletRequest, httpMethod);

			// resolve action configuration
			ActionConfig actionConfig = resolveActionConfig(actionPath, httpMethod);
			if (actionConfig == null) {
				return actionPath;
			}
			if (log.isDebugEnabled()) {
				log.debug("Invoking action path {} using {}", actionPath, actionConfig.actionClass.getSimpleName());
			}

			// create action object
			Object action = createAction(actionConfig.actionClass);

			// create action request
			ActionRequest previousRequest = request;
			request = createActionRequest(actionConfig, action, servletRequest, servletResponse);
			request.setPreviousActionRequest(previousRequest);

			// invoke and render
			invokeAndRender(request);

			actionPath = request.getNextActionPath();
		}
		return null;
	}

	/**
	 * Invokes action request (interceptors and action method) and renders result.
	 */
	protected void invokeAndRender(ActionRequest request) throws Exception {
		Object resultValueObject = request.invoke();
		render(request, resultValueObject);
	}


	// ---------------------------------------------------------------- render

	/**
	 * Invokes a result after the action invocation.
	 * <p>
	 * Result value consist of two parts: type and value. Type is optional and, if exists, it is separated
	 * by semi-colon from the value. If type is not specified, the default result type is used. Type defines which
	 * {@link ActionResult} should be used for rendering the value.
	 * <p>
	 * Result value is first checked against aliased values. Then, it is resolved and then passed
	 * to the founded {@link ActionResult}.
	 *
	 * @see ActionResult#render(jodd.madvoc.ActionRequest, Object, String, String)
	 */
	public void render(ActionRequest req, Object resultObject) throws Exception {
		String resultValue = resultObject != null ? resultObject.toString() : null;

		String resultType = madvocConfig.getDefaultResultType();
		if (resultValue != null) {
			int columnIndex = resultValue.indexOf(':');
			if (columnIndex != -1) {
				resultType = resultValue.substring(0, columnIndex);
				resultValue = resultValue.substring(columnIndex + 1);
			}
		}

		ActionResult result = resultsManager.lookup(resultType);
		if (result == null) {
			throw new MadvocException("Unable to find action result type '" + resultType + "'.");
		}
		if (result.isInitialized() == false) {
			contextInjector.injectContext(result, req.getHttpServletRequest(), req.getHttpServletResponse());
			result.initialized();
			result.init();
		}
		if (madvocConfig.isPreventCaching()) {
			ServletUtil.preventCaching(req.getHttpServletResponse());
		}
		String resultPath = resultMapper.resolveResultPath(req.getActionConfig(), resultValue);
		result.render(req, resultObject, resultValue, resultPath);
	}

	// ---------------------------------------------------------------- create

	/**
	 * Resolves action config from action path and http method. Returns <code>null</code>
	 * if action config not found. Performs initialization of founded action configig,
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
	protected void initializeActionConfig(ActionConfig cfg) {
		Class<? extends ActionInterceptor>[] interceptorClasses = cfg.interceptorClasses;
		if (interceptorClasses == null) {
			interceptorClasses = madvocConfig.getDefaultInterceptors();
		}
		cfg.interceptors = interceptorsManager.resolveAll(interceptorClasses);
		for (ActionInterceptor interceptor : cfg.interceptors) {
			if (interceptor.isInitialized() == false) {
				contextInjector.injectContext(interceptor, applicationContext);
				interceptor.initialized();
				interceptor.init();
			}
		}
		cfg.initialized();
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new action object from {@link ActionConfig} using default contructor.
	 */
	protected Object createAction(Class actionClass) {
		try {
			return actionClass.newInstance();
		} catch (InstantiationException iex) {
			throw new MadvocException("Unable to create Madvoc action.", iex);
		} catch (IllegalAccessException iaex) {
			throw new MadvocException("Not enough rights to create Madvoc action.", iaex);
		}
	}

	/**
	 * Creates new action request.
	 * @param actionConfig		action configuration
	 * @param action			action object
	 * @param servletRequest	http request
	 * @param servletResponse	http response
	 * @return action request
	 */
	protected ActionRequest createActionRequest(ActionConfig actionConfig, Object action, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		return new ActionRequest(actionConfig, action, servletRequest, servletResponse);
	}


}
