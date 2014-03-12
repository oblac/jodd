// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.meta.In;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MethodArgumentsTest {

	public static class FooAction {

		public void hello(
				@In long id
		) {
			System.out.println(id);
		}

	}

	@Test
	public void testActionMethodWithArgument() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		actionsManager.register(FooAction.class, "hello");

		ActionConfig actionConfig = actionsManager.lookup("/foo.hello.html", "GET");

		assertNotNull(actionConfig);
	}
}