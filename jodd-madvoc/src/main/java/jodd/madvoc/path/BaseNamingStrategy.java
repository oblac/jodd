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
import jodd.util.StringUtil;

/**
 * Common base for {@link jodd.madvoc.path.ActionNamingStrategy} implementations.
 */
public abstract class BaseNamingStrategy implements ActionNamingStrategy {

	protected static final String PACKAGE_MACRO = "{:package}";
	protected static final String CLASS_MACRO = "{:class}";
	protected static final String METHOD_MACRO = "{:name}";
	protected static final String HTTPMETHOD_MACRO = "{:method}";

	/**
	 * Replaces action path macros in the path.
	 * If one of the provided paths is <code>null</code>
	 * it will not be replaced - so to emphasize the problem.
	 */
	protected String replaceActionNameMacros(String path, final ActionNames actionNames) {
		final String packageName = actionNames.packageName();
		final String className = actionNames.className();
		final String methodName = actionNames.methodName();
		final String httpMethod = actionNames.httpMethod();

		if (packageName != null) {
			path = StringUtil.replace(path, PACKAGE_MACRO, packageName);
		}
		if (className != null) {
			path = StringUtil.replace(path, CLASS_MACRO, className);
		}
		if (methodName != null) {
			path = StringUtil.replace(path, METHOD_MACRO, methodName);
		}
		if (httpMethod != null) {
			path = StringUtil.replace(path, HTTPMETHOD_MACRO, httpMethod);
		}

		return path;
	}

	/**
	 * Single point of {@link ActionDefinition} creation.
	 * Also performs the replacement of action path macros!
	 */
	protected ActionDefinition createActionDef(String path, String httpMethod, String resultBasePath, final ActionNames actionNames) {
		path = replaceActionNameMacros(path, actionNames);

		if (httpMethod != null) {
			httpMethod = replaceActionNameMacros(httpMethod, actionNames);
		}

		if (resultBasePath != null) {
			resultBasePath = replaceActionNameMacros(resultBasePath, actionNames);
		}

		return new ActionDefinition(path, httpMethod, resultBasePath);
	}

	/**
	 * Returns {@code true} if path is absolute.
	 */
	protected boolean isAbsolutePath(final String path) {
		if (path == null) {
			return false;
		}
		return path.startsWith(StringPool.SLASH);
	}

}