// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.component.MadvocController;
import jodd.petite.meta.PetiteInject;

public class PetiteMadvocController extends MadvocController {

	@PetiteInject
	protected PetiteMadvocComponent petiteMadvocComponent;

	/**
	 * Acquires action from Petite container.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	protected Object createAction(Class actionClass) {
		return petiteMadvocComponent.getPetiteContainer().createBean(actionClass);
	}

}
