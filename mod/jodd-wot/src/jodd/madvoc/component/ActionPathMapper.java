// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.petite.meta.PetiteInject;
import jodd.petite.meta.PetiteInitMethod;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocUtil;
import jodd.util.StringUtil;
import jodd.util.StringPool;

/**
 * Resolves action configuration from action path on each request.
 * Should be built with great performances.
 *
 * @see ActionMethodParser
 */
public class ActionPathMapper {

	protected static final String FALLBACK_ACTION_METHOD_NAME = "view";
	protected String defaultMethodName1;
	protected String defaultMethodName2;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInitMethod(order = 1, firstOff = true)
	void actionPathMapperInit() {
		String[] d = madvocConfig.getDefaultActionMethodNames();
		String method1 = null;
		String method2 = null;
		if (d != null) {
			if (d.length > 0) {
				method1 = d[0];
			}
			if (d.length > 1) {
				method2 = d[1];
			}
		}
		defaultMethodName1 = method1 == null ? FALLBACK_ACTION_METHOD_NAME : method1;
		defaultMethodName2 = method2 == null ? defaultMethodName1 : method2;
	}

	// ----------------------------------------------------------------

	/**
	 * Lookups action configuration from action path and resolves
	 * unregistered paths. Lookup may be optionally disabled if mapping is not used.
	 */
	protected ActionConfig lookupActionConfig(String actionPath) {
		ActionConfig cfg = actionsManager.lookup(actionPath);
		if ((cfg == null) && madvocConfig.isActionPathMappingEnabled()) {
			String packageRoot = madvocConfig.getRootPackage();
			if (packageRoot != null) {
				registerActionPath(actionPath, packageRoot);
				cfg = actionsManager.lookup(actionPath);
			}
		}
		if ((cfg == null) && madvocConfig.getSupplementAction() != null) {
			registerSupplementAction(actionPath);
			cfg = actionsManager.lookup(actionPath);
		}
		return cfg;
	}

	/**
	 * Performs action config lookup from action path and http request method.
	 */
	public ActionConfig resolveActionConfig(String actionPath, String method) {
		ActionConfig actionConfig = null;
		if (method != null) {
			actionConfig = lookupActionConfig(actionPath + StringPool.HASH + method);
		}
		if (actionConfig == null) {
			actionConfig = lookupActionConfig(actionPath);
		}
		return actionConfig;
	}

	/**
	 * Tries to registers action path using {@link #mapActionPathToSignature(String, String) CoC mapping}. 
	 */
	protected void registerActionPath(String actionPath, String packageRoot) {
		String signature = mapActionPathToSignature(actionPath, packageRoot);
		try {
			actionsManager.register(signature, actionPath);
		} catch(MadvocException mex) {
			//ignore
		}
	}

	/**
	 * Maps action path to method signature. Actually, just parse the action path and
	 * calls the {@link #buildSignature(String, String, String, String, String)}.
	 */
	protected String mapActionPathToSignature(String actionPath, String packageRoot) {

		String httpMethod = MadvocUtil.extractHttpMethodFromActionPath(actionPath);
		if (httpMethod != null) {
			actionPath = actionPath.substring(0, actionPath.length() - httpMethod.length() - 1);
		}

		// package
		int slashNdx = actionPath.lastIndexOf('/');
		if (slashNdx > 0) {
			int start = actionPath.startsWith(StringPool.SLASH) ? 1 : 0;
			packageRoot += '.' + actionPath.substring(start, slashNdx).replace('/', '.');
		}

		String className;
		String methodName = null;
		String extension = null;
		int dotNdx = actionPath.indexOf('.', slashNdx);
		if (dotNdx != -1) {
			className = actionPath.substring(slashNdx + 1, dotNdx);
			int extNdx = actionPath.indexOf('.', dotNdx + 1);
			if (extNdx != -1) {
				methodName = actionPath.substring(dotNdx + 1, extNdx);
				extension = actionPath.substring(extNdx + 1);
			} else {
				methodName = null;
				extension = actionPath.substring(dotNdx + 1);
			}
		} else {
			className = actionPath.substring(slashNdx + 1);
		}

		return buildSignature(packageRoot, className, methodName, extension, httpMethod);
	}

	/**
	 * Builds action method signature based on provided names.
	 * Http method is ignored.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected String buildSignature(String packageName, String className, String methodName, String extension, String httpMethod) {
		if (methodName == null) {
			methodName = defaultMethodName1;
		}
		if (extension != null) {
			if (madvocConfig.getDefaultExtension().equals(extension) == false) {
				methodName += StringUtil.capitalize(extension);
			}
		} else {
			methodName = defaultMethodName2;
		}
		return packageName + StringPool.DOT + StringUtil.capitalize(className) + StringPool.HASH + methodName;
	}


	/**
	 * Registers supplement action for all actions that ends with default extension.
	 */
	protected void registerSupplementAction(String actionPath) {
		if (madvocConfig.getSupplementAction() != null) {
			if (actionPath.endsWith('.' + madvocConfig.getDefaultExtension())) {
				actionsManager.register(madvocConfig.getSupplementAction(), defaultMethodName1, actionPath);
			}
		}
	}

}
