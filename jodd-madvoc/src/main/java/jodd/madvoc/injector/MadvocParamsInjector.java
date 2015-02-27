// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeData;
import jodd.madvoc.component.MadvocConfig;
import jodd.petite.ParamManager;
import jodd.petite.PetiteContainer;

/**
 * Specific non-scoped injector that injects Madvoc parameters (from madvoc.props to targets).
 * Invoked on creation of all singleton instances, like interceptors etc.
 * Used to configure various Madvoc classes that are created in lazy manner.
 */
public class MadvocParamsInjector implements ContextInjector<PetiteContainer> {

	protected final MadvocConfig madvocConfig;

	public MadvocParamsInjector(MadvocConfig madvocConfig) {
		this.madvocConfig = madvocConfig;
	}

	/**
	 * Injects all matching parameters to target instance.
	 * Matching parameters are named as given base name.
	 * @param scopeData scope data is not used!
	 */
	public void injectContext(Target target, ScopeData[] scopeData, PetiteContainer madpc) {
		Class targetType = target.resolveType();
		String baseName = targetType.getName();

		ParamManager madvocPetiteParamManager = madpc.getParamManager();

		String[] params = madvocPetiteParamManager.resolve(baseName, true);

		for (String param : params) {
			Object value = madvocPetiteParamManager.get(param);

			String propertyName = param.substring(baseName.length() + 1);

			target.writeValue(propertyName, value, false);
		}
	}

}