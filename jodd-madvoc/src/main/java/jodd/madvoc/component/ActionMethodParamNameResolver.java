// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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