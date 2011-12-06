// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionPathMapper;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.test.Boo1Action;
import jodd.madvoc.test.Boo2Action;
import jodd.madvoc.test.Boo3Action;
import jodd.madvoc.test.BooAction;
import jodd.madvoc.test2.Boo4Action;
import jodd.madvoc.test2.Boo5Action;
import jodd.madvoc.test2.ReAction;

public class ActionMethodParserTest extends MadvocTestCase {

	public void testDefaultMethods() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo");
		assertEquals("/boo.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#view");
		assertEquals("/boo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#execute");
		assertEquals("/boo.html", cfg.actionPath);

	}

	public void testMethod() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo");
		assertNotNull(cfg);
		assertEquals(BooAction.class, cfg.actionClass);
		assertEquals("/boo.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo1");
		assertEquals("/boo.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo2");
		assertEquals("/boo.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo3");
		assertEquals("/boo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo4");
		assertEquals("/xxx", cfg.actionPath);
		assertNull(cfg.actionMethod);

		cfg = parse(actionMethodParser, "test.BooAction#foo41");
		assertEquals("/xxx", cfg.actionPath);
		assertEquals("DELETE", cfg.actionMethod);

		cfg = parse(actionMethodParser, "test.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);

	    MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		assertEquals("/xxx.html", madvocConfig.lookupPathAlias("dude"));
	}

	public void testMethodWithPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo");
		assertNotNull(cfg);
		assertEquals(BooAction.class, cfg.actionClass);
		assertEquals("/test/boo.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo1");
		assertEquals("/test/boo.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo2");
		assertEquals("/test/boo.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo3");
		assertEquals("/test/boo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo4");
		assertEquals("/xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo41");
		assertEquals("/xxx", cfg.actionPath);
		assertEquals("DELETE", cfg.actionMethod);

		cfg = parse(actionMethodParser, "test.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);

		cfg = parse(actionMethodParser, "test.BooAction#foo6");
		assertEquals("/test/boo.qfoo62.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo7");
		assertEquals("/foo7.html", cfg.actionPath);
	}


	public void testClasses() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.Boo1Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo1Action.class, cfg.actionClass);
		assertEquals("/boo1.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo2Action.class, cfg.actionClass);
		assertEquals("/bbb.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo1");
		assertEquals("/bbb.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo2");
		assertEquals("/bbb.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo3");
		assertEquals("/bbb.html", cfg.actionPath);

	}

	public void testClassesWithPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "test.Boo1Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo1Action.class, cfg.actionClass);
		assertEquals("/test/boo1.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo2Action.class, cfg.actionClass);
		assertEquals("/test/bbb.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo1");
		assertEquals("/test/bbb.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo2");
		assertEquals("/test/bbb.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo2Action#foo3");
		assertEquals("/test/bbb.html", cfg.actionPath);

	}

	public void testClassesWithoutPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "test.Boo3Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo3Action.class, cfg.actionClass);
		assertEquals("/bbb.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo3Action#foo1");
		assertEquals("/bbb.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo3Action#foo2");
		assertEquals("/bbb.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.Boo3Action#foo3");
		assertEquals("/bbb.html", cfg.actionPath);

	}


	public void testPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "test2.Boo4Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo4Action.class, cfg.actionClass);
		assertEquals("/ttt/www.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test2.Boo4Action#foo1");
		assertEquals("/ttt/www.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test2.Boo4Action#foo2");
		assertEquals("/ttt/www.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test2.Boo4Action#foo3");
		assertEquals("/ttt/www.html", cfg.actionPath);

	}

	public void testNoPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "test2.Boo5Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo5Action.class, cfg.actionClass);
		assertEquals("/www.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test2.Boo5Action#foo1");
		assertEquals("/www.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test2.Boo5Action#foo2");
		assertEquals("/www.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test2.Boo5Action#foo3");
		assertEquals("/www.html", cfg.actionPath);

	}

	public void testEndSlashClassName() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "test2.ReAction#hello");
		assertNotNull(cfg);
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/hello.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "test2.ReAction#macro");
		assertNotNull(cfg);
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/user/${id}/macro.html", cfg.actionPath);
	}

	public void testMacros() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		ActionPathMapper actionPathMapper = webapp.getComponent(ActionPathMapper.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		actionsManager.register(ReAction.class, "macro");
		ActionConfig cfg = actionPathMapper.resolveActionConfig("/re/user/173/macro.html", "GET");

		assertNotNull(cfg);
		ActionConfigSet set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/user/${id}/macro.html", cfg.actionPath);
		assertEquals(1, set.actionPathMacros.length);
		assertEquals(2, set.actionPathMacros[0].ndx);
		assertEquals("id", set.actionPathMacros[0].name);


		actionsManager.register(ReAction.class, "macro2");
		cfg = actionPathMapper.resolveActionConfig("/re/user/image/173/png/macro2.html", "GET");

		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/user/image/${id}/${fmt}/macro2.html", cfg.actionPath);
		assertEquals(2, set.actionPathMacros.length);
		assertEquals(3, set.actionPathMacros[0].ndx);
		assertEquals("id", set.actionPathMacros[0].name);
		assertEquals(4, set.actionPathMacros[1].ndx);
		assertEquals("fmt", set.actionPathMacros[1].name);

		actionsManager.register(ReAction.class, "macro3");
		cfg = actionPathMapper.resolveActionConfig("/re/users/173/macro3", "POST");

		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/users/${id}/macro3", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);
		assertEquals(1, set.actionPathMacros.length);
		assertEquals(2, set.actionPathMacros[0].ndx);
		assertEquals("id", set.actionPathMacros[0].name);


		cfg = actionPathMapper.resolveActionConfig("/re/user/index.html", "GET");
		assertNull(cfg);

		cfg = actionPathMapper.resolveActionConfig("/re/user/index/reindex/macro.html", "GET");
		assertNull(cfg);

		cfg = actionPathMapper.resolveActionConfig("/re/users/173/macro3", "GET");
		assertNull(cfg);

		assertEquals(3, actionsManager.getActionsCount());
	}

	public void testMacrosWildcards() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		ActionPathMapper actionPathMapper = webapp.getComponent(ActionPathMapper.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		actionsManager.register(ReAction.class, "wild1");
		actionsManager.register(ReAction.class, "wild2");

		ActionConfig cfg = actionPathMapper.resolveActionConfig("/re/ild123cat", "GET");
		assertNull(cfg);

		cfg = actionPathMapper.resolveActionConfig("/re/wild123cat", "GET");
		assertNull(cfg);

		cfg = actionPathMapper.resolveActionConfig("/re/wild123cat.html", "GET");
		assertNotNull(cfg);
		ActionConfigSet set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/wild${id}cat.html", cfg.actionPath);
		assertEquals(1, set.actionPathMacros.length);
		assertEquals(1, set.actionPathMacros[0].ndx);
		assertEquals("id", set.actionPathMacros[0].name);
		assertEquals("wild", set.actionPathMacros[0].left);
		assertEquals("cat.html", set.actionPathMacros[0].right);

		cfg = actionPathMapper.resolveActionConfig("/re/wild123dog.html", "GET");
		assertNull(cfg);

		cfg = actionPathMapper.resolveActionConfig("/re/wild123dog.html", "POST");
		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/wild${id}dog.html", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);
		assertEquals(1, set.actionPathMacros.length);
		assertEquals(1, set.actionPathMacros[0].ndx);
		assertEquals("id", set.actionPathMacros[0].name);
		assertEquals("wild", set.actionPathMacros[0].left);
		assertEquals("dog.html", set.actionPathMacros[0].right);

		assertEquals(2, actionsManager.getActionsCount());
	}

	public void testMacrosDups() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		ActionPathMapper actionPathMapper = webapp.getComponent(ActionPathMapper.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setRootPackageOf(this.getClass());

		actionsManager.register(ReAction.class, "duplo1");
		actionsManager.register(ReAction.class, "duplo2");

		ActionConfig cfg = actionPathMapper.resolveActionConfig("/re/duplo/123", "GET");
		assertNotNull(cfg);
		ActionConfigSet set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/duplo/${id:^[0-9]+}", cfg.actionPath);
		assertEquals(1, set.actionPathMacros.length);
		assertEquals(2, set.actionPathMacros[0].ndx);
		assertEquals("id", set.actionPathMacros[0].name);

		cfg = actionPathMapper.resolveActionConfig("/re/duplo/aaa", "GET");
		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/duplo/${sid}", cfg.actionPath);
		assertEquals(1, set.actionPathMacros.length);
		assertEquals(2, set.actionPathMacros[0].ndx);
		assertEquals("sid", set.actionPathMacros[0].name);

		assertEquals(2, actionsManager.getActionsCount());
	}

}
