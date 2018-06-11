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

import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionNames;
import jodd.util.StringPool;

import java.lang.reflect.Method;

/**
 * Default naming strategy.
 */
public class DefaultActionPathNamingStrategy extends BaseNamingStrategy {

	@Override
	public ActionDefinition buildActionDef(
		final Class actionClass,
		final Method actionMethod,
		final ActionNames actionNames) {

		final String packageActionPath = actionNames.packageActionPath();
		final String classActionPath = actionNames.classActionPath();
		final String methodActionPath = actionNames.methodActionPath();
		final String httpMethod = actionNames.httpMethod();

		// if method path is an absolute path, use it right away

		if (isAbsolutePath(methodActionPath)) {
			return createActionDef(methodActionPath, httpMethod, methodActionPath, actionNames);
		}

		String actionPath = classActionPath;

		if (methodActionPath != null) {
			if (!classActionPath.endsWith(StringPool.SLASH)) {
				actionPath += StringPool.DOT;
			}
			actionPath += methodActionPath;
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