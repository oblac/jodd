// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.petite.PetiteContainer;
import jodd.petite.resolver.ParamResolver;

/**
 * Specific non-scoped injector that injects Madvoc parameters (from madvoc.props to targets).
 * Invoked on creation of all singleton instances, like interceptors etc.
 * Used to configure various Madvoc classes that are created in lazy manner.
 */
public class MadvocParamsInjector {

	protected final ParamResolver madvocPetiteParamResolver;

	public MadvocParamsInjector(PetiteContainer madpc) {
		madvocPetiteParamResolver = madpc.getResolvers().getParamResolver();
	}

	public void inject(Object target) {
		String className = target.getClass().getName();

		String[] params = madvocPetiteParamResolver.resolve(className, true);

		for (String param : params) {
			Object value = madvocPetiteParamResolver.get(param);

			String propertyName = param.substring(className.length() + 1);

			BeanUtil.setDeclaredPropertySilent(target, propertyName, value);
		}
	}

}
