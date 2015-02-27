// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.filter.ActionFilter;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;

/**
 * Petite-aware filters manager.
 */
public class PetiteFilterManager extends FiltersManager {

	@PetiteInject
	protected PetiteContainer petiteContainer;

	/**
	 * Acquires filter from Petite container.
	 */
	@Override
	protected <R extends ActionFilter> R createWrapper(Class<R> wrapperClass) {
		return petiteContainer.createBean(wrapperClass);
	}

}