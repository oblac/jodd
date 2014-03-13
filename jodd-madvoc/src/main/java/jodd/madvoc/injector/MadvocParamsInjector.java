// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.petite.ParamManager;
import jodd.petite.PetiteContainer;

/**
 * Specific non-scoped injector that injects Madvoc parameters (from madvoc.props to targets).
 * Invoked on creation of all singleton instances, like interceptors etc.
 * Used to configure various Madvoc classes that are created in lazy manner.
 */
public class MadvocParamsInjector implements ContextInjector<String> {

	protected final ParamManager madvocPetiteParamManager;

	public MadvocParamsInjector(PetiteContainer madpc) {
		madvocPetiteParamManager = madpc.getParamManager();
	}

	/**
	 * Injects all matching parameters to target instance.
	 * Matching parameters are named as given base name.
	 */
	public void injectContext(Object target, String baseName) {
		String[] params = madvocPetiteParamManager.resolve(baseName, true);

		for (String param : params) {
			Object value = madvocPetiteParamManager.get(param);

			String propertyName = param.substring(baseName.length() + 1);

			BeanUtil.setDeclaredProperty(target, propertyName, value);
		}
	}

}