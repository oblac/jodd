// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionDef;
import jodd.madvoc.WebApplication;
import jodd.madvoc.macro.RegExpPathMacros;
import jodd.madvoc.macro.WildcardPathMacros;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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

		actionsManager.register(FooAction.class, "one", new ActionDef("/${one}"));

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

		actionsManager.register(FooAction.class, "one", new ActionDef("/${one}"));
		actionsManager.register(FooAction.class, "two", new ActionDef("/xxx-${two}"));

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

		actionsManager.register(FooAction.class, "one", new ActionDef("/yyy-${one}"));
		actionsManager.register(FooAction.class, "two", new ActionDef("/xxx-${two}"));

		assertEquals(2, actionsManager.getActionsCount());

		ActionConfig actionConfig = actionsManager.lookup("/foo", null);
		assertNull(actionConfig);

		actionConfig = actionsManager.lookup("/yyy-111", null);
		assertEquals("one", actionConfig.actionClassMethod.getName());

		actionConfig = actionsManager.lookup("/xxx-222", null);
		assertEquals("two", actionConfig.actionClassMethod.getName());

		try {
			actionsManager.register(FooAction.class, "two", new ActionDef("/xxx-${two}"));
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

		actionsManager.register(FooAction.class, "one", new ActionDef("/${one}"));
		actionsManager.register(FooAction.class, "one", new ActionDef("/dummy"));		// no macro
		actionsManager.register(FooAction.class, "two", new ActionDef("/${two}/${three}"));
		actionsManager.register(FooAction.class, "three", new ActionDef("/life/${three}"));

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
		madvocConfig.setPathMacroClass(RegExpPathMacros.class);

		actionsManager.register(FooAction.class, "one", new ActionDef("/${one:[ab]+}"));

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
		madvocConfig.setPathMacroClass(WildcardPathMacros.class);

		actionsManager.register(FooAction.class, "one", new ActionDef("/${one:a?a}"));

		ActionConfig actionConfig = actionsManager.lookup("/aaa", null);
		assertNotNull(actionConfig);

		actionConfig = actionsManager.lookup("/aab", null);
		assertNull(actionConfig);
	}
}
