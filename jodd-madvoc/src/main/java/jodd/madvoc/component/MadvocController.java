// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.RenderWith;
import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.Result;
import jodd.petite.meta.PetiteInject;
import jodd.servlet.ServletUtil;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.typeconverter.TypeConverterManager;

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
	protected ActionsManager actionsManager;

	@PetiteInject
	protected ActionPathRewriter actionPathRewriter;

	@PetiteInject
	protected ResultsManager resultsManager;

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
			ActionConfig actionConfig = actionsManager.lookup(actionPath, httpMethod);
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
	 * Results may be objects that specify which action result will be used
	 * to render the result.
	 * <p>
	 * Result value may consist of two parts: type and value. Result type is optional and, if exists, it is separated
	 * by semi-colon from the value. If type is not specified
	 * then the default result type if still not defined. Result type defines which
	 * {@link ActionResult} should be used for rendering the value.
	 * <p>
	 * Result value is first checked against aliased values. Then, it is resolved and then passed
	 * to the founded {@link ActionResult}.
	 *
	 * @see ActionResult#render(jodd.madvoc.ActionRequest, Object)
	 */
	@SuppressWarnings("unchecked")
	public void render(ActionRequest actionRequest, Object resultObject) throws Exception {
		ActionResult actionResult;

		// [1] try to lookup the result class

		Class<? extends ActionResult> actionResultClass = null;

		if (resultObject != null && resultObject.getClass() != String.class) {
			// try annotation
			RenderWith renderWith = resultObject.getClass().getAnnotation(RenderWith.class);
			if (renderWith != null) {
				actionResultClass = renderWith.value();
			}
		} else if (resultObject == null) {
			Result result = actionRequest.getResult();
			if (result != null) {
				actionResultClass = result.getActionResult();
				resultObject = result.getResultValue();
				if (resultObject == null) {
					resultObject = result.value();
				}
			}
		}

		if (actionResultClass != null) {
			// result class is known, lookup the action result type
			actionResult = resultsManager.lookup(actionResultClass);

			if (actionResult == null) {
				// register action result if by any chance it wasn't registered yet
				actionResult = resultsManager.register(actionResultClass);
			}

		} else {
			// result class is not known, lookup it from returned string
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

			// result type still not defined, use default
			if (resultType == null) {
				resultType = madvocConfig.getDefaultResultType();
			}

			actionResult = resultsManager.lookup(resultType);
			if (actionResult == null) {
				throw new MadvocException("Action result not found: " + resultType);
			}

			// convert remaining of the string to result object
			try {
				Class targetClass = actionResult.getResultValueType();
				if (targetClass == String.class) {
					resultObject = resultValue;
				} else {
					resultObject = TypeConverterManager.convertType(resultValue, targetClass);
				}
			} catch (Exception ex) {
				resultObject = resultValue;
			}
		}

		// finally, invoke result
		if (madvocConfig.isPreventCaching()) {
			ServletUtil.preventCaching(actionRequest.getHttpServletResponse());
		}

		actionResult.render(actionRequest, resultObject);
	}

	// ---------------------------------------------------------------- create

	/**
	 * Creates new action object from {@link ActionConfig} using default constructor.
	 */
	protected Object createAction(Class actionClass) {
		try {
			return actionClass.newInstance();
		} catch (Exception ex) {
			throw new MadvocException("Invalid Madvoc action", ex);
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
	protected ActionRequest createActionRequest(
			String actionPath,
			ActionConfig actionConfig,
			Object action,
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {

		return new ActionRequest(this, actionPath, actionConfig, action, servletRequest, servletResponse);
	}

}