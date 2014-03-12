// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;

import java.lang.reflect.Method;

/**
 * Simple wrapper over Paramo tool.
 */
public class ActionParameterNamesResolver {

	/**
	 * Returns argument names for action method. Returns <code>null</code>
	 * when method has no arguments.
	 */
	public String[] resolveActionParameterNames(Method actionMethod) {
		MethodParameter[] methodParams = Paramo.resolveParameters(actionMethod);

		if (methodParams == null || methodParams.length == 0) {
			return null;
		}

		String[] names = new String[methodParams.length];

		for (int i = 0; i < methodParams.length; i++) {
			names[i] = methodParams[i].getName();
		}

		return names;
	}

}