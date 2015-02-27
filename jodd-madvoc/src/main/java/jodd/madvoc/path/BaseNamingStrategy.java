// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.path;

import jodd.madvoc.ActionDef;
import jodd.madvoc.ActionNames;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Common base for {@link jodd.madvoc.path.ActionNamingStrategy} implementations.
 */
public abstract class BaseNamingStrategy implements ActionNamingStrategy {

	protected static final String PACKAGE_MACRO = "${:package}";
	protected static final String CLASS_MACRO = "${:class}";
	protected static final String METHOD_MACRO = "${:method}";
	protected static final String EXTENSION_MACRO = "${:ext}";
	protected static final String HTTPMETHOD_MACRO = "${:http-method}";

	/**
	 * Replaces action path macros in the path.
	 * If one of the provided paths is <code>null</code>
	 * it will not be replaced - so to emphasize the problem.
	 */
	protected String replaceActionNameMacros(String path, ActionNames actionNames) {
		String packageName = actionNames.getPackageName();
		String className = actionNames.getClassName();
		String methodName = actionNames.getMethodName();
		String extension = actionNames.getExtension();
		String httpMethod = actionNames.getHttpMethod();

		if (packageName != null) {
			path = StringUtil.replace(path, PACKAGE_MACRO, packageName);
		}
		if (className != null) {
			path = StringUtil.replace(path, CLASS_MACRO, className);
		}
		if (methodName != null) {
			path = StringUtil.replace(path, METHOD_MACRO, methodName);
		}
		if (extension != null) {
			path = StringUtil.replace(path, EXTENSION_MACRO, extension);
		}
		if (httpMethod != null) {
			path = StringUtil.replace(path, HTTPMETHOD_MACRO, httpMethod);
		}

		return path;
	}

	/**
	 * Single point of {@link jodd.madvoc.ActionDef} creation.
	 * Also performs the replacement of action path macros!
	 */
	protected ActionDef createActionDef(String path, String httpMethod, String resultBasePath, ActionNames actionNames) {
		path = replaceActionNameMacros(path, actionNames);

		if (httpMethod != null) {
			httpMethod = replaceActionNameMacros(httpMethod, actionNames);
		}

		if (resultBasePath != null) {
			resultBasePath = replaceActionNameMacros(resultBasePath, actionNames);
		}

		return new ActionDef(path, httpMethod, resultBasePath);
	}

	/**
	 * Returns <code>true</code> if path is absolute.
	 */
	protected boolean isAbsolutePath(String path) {
		if (path == null) {
			return false;
		}
		return path.startsWith(StringPool.SLASH);
	}
}