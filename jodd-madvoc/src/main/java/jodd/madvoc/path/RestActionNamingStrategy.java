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

package jodd.madvoc.path;

import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionNames;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.scope.MadvocContext;
import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.net.HttpMethod;

import java.lang.reflect.Method;

/**
 * Naming strategy for REST resources.
 */
public class RestActionNamingStrategy extends BaseNamingStrategy {

	@In @MadvocContext
	protected ActionsManager actionsManager;

	protected boolean includeMethodActionPath = true;

	@Override
	public ActionDefinition buildActionDef(final Class actionClass, final Method actionMethod, final ActionNames actionNames) {

		final String packageActionPath = actionNames.packageActionPath();
		final String classActionPath = actionNames.classActionPath();

		String methodActionPath = includeMethodActionPath ? actionNames.methodActionPath() : null;
		String httpMethod = actionNames.httpMethod();

		if (httpMethod == null) {
			httpMethod = resolveHttpMethodFromMethodName(actionMethod.getName());
		}

		String actionPath = classActionPath;
		String resultPath = classActionPath;

		if (isAbsolutePath(methodActionPath)) {
			return createActionDef(methodActionPath, httpMethod, methodActionPath, actionNames);
		}

		if (methodActionPath != null) {
			if (httpMethod == null && methodActionPath.startsWith(actionsManager.getPathMacroSeparators()[0])) {
				methodActionPath = actionMethod.getName() + StringPool.SLASH + methodActionPath;
			}

			if (!classActionPath.endsWith(StringPool.SLASH)) {
				actionPath += StringPool.SLASH;
				resultPath += StringPool.SLASH;
			}

			actionPath += methodActionPath;

			if (httpMethod != null) {
				resultPath += httpMethod.toLowerCase();
			} else {
				resultPath += actionNames.methodName();
			}
		}

		if (isAbsolutePath(actionPath)) {
			return createActionDef(actionPath, httpMethod, resultPath, actionNames);
		}

		if (packageActionPath != null) {
			actionPath = packageActionPath + actionPath;
			resultPath = packageActionPath + resultPath;
		} else {
			actionPath = StringPool.SLASH + actionPath;
			resultPath = StringPool.SLASH + resultPath;
		}

		return createActionDef(actionPath, httpMethod, resultPath, actionNames);
	}

	/**
	 * Resolves HTTP method name from method name.
	 * If method name or first camel-case word of a method equals to
	 * a HTTP method, it will be used as that HTTP methods.
	 */
	protected String resolveHttpMethodFromMethodName(final String methodName) {
		int i = 0;

		while (i < methodName.length()) {
			if (CharUtil.isUppercaseAlpha(methodName.charAt(i))) {
				break;
			}
			i++;
		}

		final String name = methodName.substring(0, i).toUpperCase();

		for (final HttpMethod httpMethod : HttpMethod.values()) {
			if (httpMethod.equalsName(name)) {
				return httpMethod.name();
			}
		}

		return null;
	}

}