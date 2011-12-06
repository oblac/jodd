// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.component.MadvocController;
import jodd.petite.meta.PetiteInject;
import jodd.petite.PetiteContainer;

/**
 * Petite-aware Madvoc controller.
 */
public class PetiteMadvocController extends MadvocController {

	@PetiteInject
	protected PetiteContainer petiteContainer;

	/**
	 * Acquires action from Petite container.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	protected Object createAction(Class actionClass) {
		return petiteContainer.createBean(actionClass);
	}

}
