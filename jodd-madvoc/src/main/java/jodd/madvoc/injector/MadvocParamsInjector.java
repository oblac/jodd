// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.petite.ParamManager;
import jodd.petite.PetiteContainer;

/**
 * Specific non-scoped injector that injects Madvoc parameters (from madvoc.props to targets).
 * Invoked on creation of all singleton instances, like interceptors etc.
 * Used to configure various Madvoc classes that are created in lazy manner.
 */
public class MadvocParamsInjector {

	protected final ParamManager madvocPetiteParamManager;

	public MadvocParamsInjector(PetiteContainer madpc) {
		madvocPetiteParamManager = madpc.getParamManager();
	}

	public void inject(Object target) {
		String className = target.getClass().getName();

		String[] params = madvocPetiteParamManager.resolve(className, true);

		for (String param : params) {
			Object value = madvocPetiteParamManager.get(param);

			String propertyName = param.substring(className.length() + 1);

			BeanUtil.setDeclaredPropertySilent(target, propertyName, value);
		}
	}

}