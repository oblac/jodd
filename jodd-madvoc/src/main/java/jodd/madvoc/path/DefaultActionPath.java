// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.path;

import jodd.madvoc.ActionId;
import jodd.madvoc.ActionNames;
import jodd.util.StringPool;

import java.lang.reflect.Method;

/**
 * Default naming strategy.
 */
public class DefaultActionPath extends BaseNamingStrategy {

	public ActionId buildActionId(
			Class actionClass,
			Method actionMethod,
			ActionNames actionNames) {

		String packageActionPath = actionNames.getPackageActionPath();
		String classActionPath = actionNames.getClassActionPath();
		String methodActionPath = actionNames.getMethodActionPath();
		String extension = actionNames.getExtension();
		String httpMethod = actionNames.getHttpMethod();

		String actionPath = classActionPath;

		if (isAbsolutePath(methodActionPath)) {
			return createActionId(methodActionPath, httpMethod, actionNames);
		}

		if (methodActionPath != null) {
			if (extension != null) {		// add extension
				methodActionPath += '.' + extension;
			}
			if (classActionPath.endsWith(StringPool.SLASH) == false) {
				actionPath += StringPool.DOT;
			}
			actionPath += methodActionPath; // method separator
		} else {
			if (extension != null) {
				actionPath += '.' + extension;
			}
		}

		if (isAbsolutePath(actionPath)) {
			return createActionId(actionPath, httpMethod, actionNames);
		}

		if (packageActionPath != null) {
			actionPath = packageActionPath + actionPath;
		} else {
			actionPath = StringPool.SLASH + actionPath;
		}

		return createActionId(actionPath, httpMethod, actionNames);
	}

}