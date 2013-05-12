//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.config.MadvocConfigurator;
import jodd.madvoc.result.TextResult;
import jodd.madvoc.result.TextResultTest;
import jodd.petite.meta.PetiteInject;

public class MadvocTestConfig implements MadvocConfigurator {

	@PetiteInject
	ActionsManager actionsManager;
	@PetiteInject
	ResultsManager resultsManager;

	public void configure() {
		resultsManager.register(TextResult.class);

		actionsManager.register(TextResultTest.class, "madvocEncoding", "/textResultEncoding");
	}
}