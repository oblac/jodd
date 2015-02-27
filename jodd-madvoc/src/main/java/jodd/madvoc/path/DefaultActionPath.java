// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.path;

import jodd.madvoc.ActionDef;
import jodd.madvoc.ActionNames;
import jodd.util.StringPool;

import java.lang.reflect.Method;

/**
 * Default naming strategy.
 */
public class DefaultActionPath extends BaseNamingStrategy {

	public ActionDef buildActionDef(
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
			return createActionDef(methodActionPath, httpMethod, methodActionPath, actionNames);
		}

		if (methodActionPath != null) {
			if (extension != null) {		// add extension
				methodActionPath += '.' + extension;
			}
			if (classActionPath.endsWith(StringPool.SLASH) == false) {
				actionPath += StringPool.DOT;
			}
			actionPath += methodActionPath;
		} else {
			if (extension != null) {
				actionPath += '.' + extension;
			}
		}

		if (isAbsolutePath(actionPath)) {
			return createActionDef(actionPath, httpMethod, actionPath, actionNames);
		}

		if (packageActionPath != null) {
			actionPath = packageActionPath + actionPath;
		} else {
			actionPath = StringPool.SLASH + actionPath;
		}

		return createActionDef(actionPath, httpMethod, actionPath, actionNames);
	}

}