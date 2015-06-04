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

import jodd.madvoc.ActionDef;
import jodd.madvoc.ActionNames;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.util.CharUtil;
import jodd.util.StringPool;

import java.lang.reflect.Method;

/**
 * Naming strategy for REST resources.
 */
public class RestResourcePath extends BaseNamingStrategy {

	@In(scope = ScopeType.CONTEXT)
	protected MadvocConfig madvocConfig;

	public ActionDef buildActionDef(Class actionClass, Method actionMethod, ActionNames actionNames) {

		String packageActionPath = actionNames.getPackageActionPath();
		String classActionPath = actionNames.getClassActionPath();
		String methodActionPath = actionNames.getMethodActionPath();
		String httpMethod = actionNames.getHttpMethod();

		if (httpMethod == null) {
			httpMethod = resolveHttpMethodFromMethodName(actionMethod.getName());
		}

		String actionPath = classActionPath;
		String resultPath = classActionPath;

		if (isAbsolutePath(methodActionPath)) {
			return createActionDef(methodActionPath, httpMethod, methodActionPath, actionNames);
		}

		if (methodActionPath != null) {
			if (httpMethod == null && methodActionPath.startsWith(madvocConfig.getPathMacroSeparators()[0])) {
				methodActionPath = actionMethod.getName() + StringPool.SLASH + methodActionPath;
			}

			if (classActionPath.endsWith(StringPool.SLASH) == false) {
				actionPath += StringPool.SLASH;
				resultPath += StringPool.SLASH;
			}

			actionPath += methodActionPath;

			if (httpMethod != null) {
				resultPath += httpMethod.toLowerCase();
			} else {
				resultPath += actionNames.getMethodName();
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
	protected String resolveHttpMethodFromMethodName(String methodName) {
		int i = 0;

		while (i < methodName.length()) {
			if (CharUtil.isUppercaseAlpha(methodName.charAt(i))) {
				break;
			}
			i++;
		}

		String name = methodName.substring(0, i).toUpperCase();

		if (name.equals(Action.CONNECT)) {
			return Action.CONNECT;
		}

		if (name.equals(Action.DELETE)) {
			return Action.DELETE;
		}

		if (name.equals(Action.GET)) {
			return Action.GET;
		}

		if (name.equals(Action.HEAD)) {
			return Action.HEAD;
		}

		if (name.equals(Action.OPTIONS)) {
			return Action.OPTIONS;
		}

		if (name.equals(Action.PATCH)) {
			return Action.PATCH;
		}

		if (name.equalsIgnoreCase(Action.POST)) {
			return Action.POST;
		}

		if (name.equals(Action.PUT)) {
			return Action.PUT;
		}

		if (name.equals(Action.TRACE)) {
			return Action.TRACE;
		}

		return null;
	}

}