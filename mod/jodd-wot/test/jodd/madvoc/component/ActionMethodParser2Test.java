// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.MadvocTestCase;
import jodd.madvoc.WebApplication;

public class ActionMethodParser2Test extends MadvocTestCase {

	public void testActionName() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser amp = webapp.getComponent(ActionMethodParser.class);

		assertEquals("foo", amp.convertClassNameToActionName("FooAction"));
		assertEquals("foo", amp.convertClassNameToActionName("Foo"));
		assertEquals("fooBoo", amp.convertClassNameToActionName("FooBooAction"));
		assertEquals("fooBoo", amp.convertClassNameToActionName("FooBooZoooo"));
	}

}

