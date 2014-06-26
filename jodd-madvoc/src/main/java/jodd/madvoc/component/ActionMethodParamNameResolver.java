// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.Jodd;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.util.StringUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves method parameter names.
 */
public class ActionMethodParamNameResolver {

	private static final Logger log = LoggerFactory.getLogger(ActionMethodParamNameResolver.class);

	protected Map<Method, String[]> paramNames = new HashMap<Method, String[]>();

	/**
	 * Returns method parameter names.
	 */
	public String[] resolveParamNames(Method actionClassMethod) {
		String[] names = paramNames.get(actionClassMethod);

		if (names != null) {
			return names;
		}

		if (!Jodd.isModuleLoaded(Jodd.PROXETTA)) {
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

		paramNames.put(actionClassMethod, names);

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