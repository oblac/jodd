// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.petite;

import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.petite.PetiteContainer;

/**
 * Petite-aware results manager.
 */
public class PetiteResultsManager extends ResultsManager {

	@PetiteInject
	protected PetiteContainer petiteContainer;

	@Override
	protected ActionResult createResult(Class<? extends ActionResult> actionResultClass) {
		return petiteContainer.createBean(actionResultClass);
	}
}
