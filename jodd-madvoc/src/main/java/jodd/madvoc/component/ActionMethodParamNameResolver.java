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

package jodd.madvoc.component;

import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.JoddMadvoc;
import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.util.StringUtil;

import java.lang.reflect.Method;

/**
 * Resolves method parameter names.
 * Does not cache anything, as it is going to be called once per each method.
 * One method will and should not be passed twice to this class!
 */
public class ActionMethodParamNameResolver {

	private static final Logger log = LoggerFactory.getLogger(ActionMethodParamNameResolver.class);

	/**
	 * Returns method parameter names.
	 */
	public String[] resolveParamNames(Method actionClassMethod) {
		String[] names;

		if (!JoddMadvoc.useProxetta) {
			if (log.isWarnEnabled()) {
				log.warn("Unable to resolve method names, using type short names instead. Add Proxetta to resolve this.");
			}

			names = convertTypeNames(actionClassMethod);
		}
		else {
			MethodParameter[] methodParameters = Paramo.resolveParameters(actionClassMethod);
			names = new String[methodParameters.length];

			for (int i = 0; i < methodParameters.length; i++) {
				names[i] = methodParameters[i].getName();
			}
		}

		return names;
	}

	/**
	 * Converts method type short names into names.
	 */
	protected String[] convertTypeNames(Method actionClassMethod) {
		Class[] types = actionClassMethod.getParameterTypes();

		String[] names = new String[types.length];


		for (int i = 0; i < types.length; i++) {
			Class type = types[i];

			names[i] = StringUtil.uncapitalize(type.getSimpleName());
		}

		return names;
	}

}