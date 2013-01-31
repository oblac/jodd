// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.WebApplication;
import jodd.madvoc.macro.RegExpPathMacro;
import jodd.madvoc.macro.WildcardPathMacro;
import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ActionsManagerTest {

	public static class FooAction {
		public void one() {
		}
		public void two() {
		}
		public void three() {
		}
	}

	@Test
	public void testActionPathMacros1() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		actionsManager.register(FooAction.class, "one", "/${one}");

		ActionConfig actionConfig = actionsManager.lookup("/foo", null);
		assertNotNull(actionConfig);

		actionConfig = actionsManager.lookup("/foo/boo", null);
		assertNull(actionConfig);
		actionConfig = actionsManager.lookup("/foo/boo/zoo", null);
		assertNull(actionConfig);
	}

	@Test
	public void testActionPathMacros2() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		actionsManager.register(FooAction.class, "one", "/${one}");
		actionsManager.register(FooAction.class, "two", "/xxx-${two}");

		ActionConfig actionConfig = actionsManager.lookup("/foo", null);
		assertEquals("one", actionConfig.actionClassMethod.getName());

		actionConfig = actionsManager.lookup("/foo/boo", null);
		assertNull(actionConfig);

		actionConfig = actionsManager.lookup("/xxx-foo", null);
		assertEquals("two", actionConfig.actionClassMethod.getName());	// best match!

	}

	@Test
	public void testActionPathMacros3() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		actionsManager.register(FooAction.class, "one", "/yyy-${one}");
		actionsManager.register(FooAction.class, "two", "/xxx-${two}");

		assertEquals(2, actionsManager.getActionsCount());

		ActionConfig actionConfig = actionsManager.lookup("/foo", null);
		assertNull(actionConfig);

		actionConfig = actionsManager.lookup("/yyy-111", null);
		assertEquals("one", actionConfig.actionClassMethod.getName());

		actionConfig = actionsManager.lookup("/xxx-222", null);
		assertEquals("two", actionConfig.actionClassMethod.getName());

		try {
			actionsManager.register(FooAction.class, "two", "/xxx-${two}");
			Assert.fail();
		} catch (Exception ex) {
			// ignore
		}
	}

	@Test
	public void testActionPathMacros4() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		actionsManager.register(FooAction.class, "one", "/${one}");
		actionsManager.register(FooAction.class, "one", "/dummy");		// no macro
		actionsManager.register(FooAction.class, "two", "/${two}/${three}");
		actionsManager.register(FooAction.class, "three", "/life/${three}");

		ActionConfig actionConfig = actionsManager.lookup("/foo", null);
		assertEquals("one", actionConfig.actionClassMethod.getName());

 		actionConfig = actionsManager.lookup("/scott/ramonna", null);
		assertEquals("two", actionConfig.actionClassMethod.getName());

		actionConfig = actionsManager.lookup("/life/universe", null);
		assertEquals("three", actionConfig.actionClassMethod.getName());

		actionConfig = actionsManager.lookup("/scott/ramonna/envy", null);
		assertNull(actionConfig);

		actionConfig = actionsManager.lookup("/life/universe/else", null);
		assertNull(actionConfig);
	}

	@Test
	public void testActionPathMacrosRegexp() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setPathMacroClass(RegExpPathMacro.class);

		actionsManager.register(FooAction.class, "one", "/${one:[ab]+}");

		ActionConfig actionConfig = actionsManager.lookup("/a", null);
		assertNotNull(actionConfig);

		actionConfig = actionsManager.lookup("/ac", null);
		assertNull(actionConfig);
	}

	@Test
	public void testActionPathMacrosWildcard() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setPathMacroClass(WildcardPathMacro.class);

		actionsManager.register(FooAction.class, "one", "/${one:a?a}");

		ActionConfig actionConfig = actionsManager.lookup("/aaa", null);
		assertNotNull(actionConfig);

		actionConfig = actionsManager.lookup("/aab", null);
		assertNull(actionConfig);
	}
}
