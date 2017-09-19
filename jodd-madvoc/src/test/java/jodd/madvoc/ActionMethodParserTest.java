// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.macro.RegExpPathMacros;
import jodd.madvoc.fixtures.tst.Boo1Action;
import jodd.madvoc.fixtures.tst.Boo2Action;
import jodd.madvoc.fixtures.tst.Boo3Action;
import jodd.madvoc.fixtures.tst.BooAction;
import jodd.madvoc.fixtures.tst2.Boo4Action;
import jodd.madvoc.fixtures.tst2.Boo5Action;
import jodd.madvoc.fixtures.tst2.ReAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActionMethodParserTest extends MadvocTestCase {

	@Test
	public void testDefaultMethods() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo");
		assertEquals("/boo.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#view");
		assertEquals("/boo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#execute");
		assertEquals("/boo.html", cfg.actionPath);

	}

	@Test
	public void testMethod() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo");
		assertNotNull(cfg);
		assertEquals(BooAction.class, cfg.actionClass);
		assertEquals("/boo.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo1");
		assertEquals("/boo.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo2");
		assertEquals("/boo.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo3");
		assertEquals("/boo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo4");
		assertEquals("/xxx", cfg.actionPath);
		assertNull(cfg.actionMethod);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo41");
		assertEquals("/xxx", cfg.actionPath);
		assertEquals("DELETE", cfg.actionMethod);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);

		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		assertEquals("/xxx.html", actionsManager.lookupPathAlias("dude"));
	}

	@Test
	public void testMethodWithPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo");
		assertNotNull(cfg);
		assertEquals(BooAction.class, cfg.actionClass);
		assertEquals("/fixtures/tst/boo.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo1");
		assertEquals("/fixtures/tst/boo.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo2");
		assertEquals("/fixtures/tst/boo.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo3");
		assertEquals("/fixtures/tst/boo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo4");
		assertEquals("/xxx", cfg.actionPath);
		assertNull(cfg.actionMethod);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo41");
		assertEquals("/xxx", cfg.actionPath);
		assertEquals("DELETE", cfg.actionMethod);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo6");
		assertEquals("/fixtures/tst/boo.qfoo62.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo7");
		assertEquals("/foo7.html", cfg.actionPath);
	}


	@Test
	public void testClasses() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst.Boo1Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo1Action.class, cfg.actionClass);
		assertEquals("/boo1.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo2Action.class, cfg.actionClass);
		assertEquals("/bbb.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo1");
		assertEquals("/bbb.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo2");
		assertEquals("/bbb.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo3");
		assertEquals("/bbb.html", cfg.actionPath);

	}

	@Test
	public void testClassesWithPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst.Boo1Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo1Action.class, cfg.actionClass);
		assertEquals("/fixtures/tst/boo1.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo2Action.class, cfg.actionClass);
		assertEquals("/fixtures/tst/bbb.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo1");
		assertEquals("/fixtures/tst/bbb.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo2");
		assertEquals("/fixtures/tst/bbb.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo2Action#foo3");
		assertEquals("/fixtures/tst/bbb.html", cfg.actionPath);

	}

	@Test
	public void testClassesWithoutPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst.Boo3Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo3Action.class, cfg.actionClass);
		assertEquals("/bbb.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo3Action#foo1");
		assertEquals("/bbb.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo3Action#foo2");
		assertEquals("/bbb.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst.Boo3Action#foo3");
		assertEquals("/bbb.html", cfg.actionPath);

	}

	@Test
	public void testPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst2.Boo4Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo4Action.class, cfg.actionClass);
		assertEquals("/ttt/www.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst2.Boo4Action#foo1");
		assertEquals("/ttt/www.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst2.Boo4Action#foo2");
		assertEquals("/ttt/www.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst2.Boo4Action#foo3");
		assertEquals("/ttt/www.html", cfg.actionPath);

	}

	@Test
	public void testNoPackage() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst2.Boo5Action#foo");
		assertNotNull(cfg);
		assertEquals(Boo5Action.class, cfg.actionClass);
		assertEquals("/www.foo.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst2.Boo5Action#foo1");
		assertEquals("/www.xxx.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst2.Boo5Action#foo2");
		assertEquals("/www.foo2.xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst2.Boo5Action#foo3");
		assertEquals("/www.html", cfg.actionPath);

	}

	@Test
	public void testEndSlashClassName() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst2.ReAction#hello");
		assertNotNull(cfg);
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/hello.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst2.ReAction#macro");
		assertNotNull(cfg);
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/user/${id}/macro.html", cfg.actionPath);
	}

	@Test
	public void testMacros() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		actionsManager.register(ReAction.class, "macro");
		ActionConfig cfg = actionsManager.lookup("/re/user/173/macro.html", "GET");

		assertNotNull(cfg);
		ActionConfigSet set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/user/${id}/macro.html", cfg.actionPath);
		assertEquals(4, set.deep);
		assertEquals(1, set.actionPathMacros.getMacrosCount());
		assertEquals("id", set.actionPathMacros.getNames()[0]);
		assertNull(set.actionPathMacros.getPatterns()[0]);


		actionsManager.register(ReAction.class, "macro2");
		cfg = actionsManager.lookup("/re/user/image/173/png/macro2.html", "GET");

		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/user/image/${id}/${fmt}/macro2.html", cfg.actionPath);
		assertEquals(6, set.deep);
		assertEquals(2, set.actionPathMacros.getMacrosCount());
		assertEquals("id", set.actionPathMacros.getNames()[0]);
		assertEquals("fmt", set.actionPathMacros.getNames()[1]);

		actionsManager.register(ReAction.class, "macro3");
		cfg = actionsManager.lookup("/re/users/173/macro3", "POST");

		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/users/${id}/macro3", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);
		assertEquals(4, set.deep);
		assertEquals(1, set.actionPathMacros.getMacrosCount());
		assertEquals("id", set.actionPathMacros.getNames()[0]);

		cfg = actionsManager.lookup("/re/user/index.html", "GET");
		assertNull(cfg);

		cfg = actionsManager.lookup("/re/user/index/reindex/macro.html", "GET");
		assertNull(cfg);

		cfg = actionsManager.lookup("/re/users/173/macro3", "GET");
		assertNull(cfg);

		assertEquals(3, actionsManager.getActionsCount());
	}

	@Test
	public void testMacrosWildcards() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		actionsManager.register(ReAction.class, "wild1");
		actionsManager.register(ReAction.class, "wild2");

		ActionConfig cfg = actionsManager.lookup("/re/ild123cat", "GET");
		assertNull(cfg);

		cfg = actionsManager.lookup("/re/wild123cat", "GET");
		assertNull(cfg);

		cfg = actionsManager.lookup("/re/wild123cat.html", "GET");
		assertNotNull(cfg);
		ActionConfigSet set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/wild${id}cat.html", cfg.actionPath);
		assertEquals(2, set.deep);
		assertEquals(1, set.actionPathMacros.getMacrosCount());
		assertEquals("id", set.actionPathMacros.getNames()[0]);

		cfg = actionsManager.lookup("/re/wild123dog.html", "GET");
		assertNull(cfg);

		cfg = actionsManager.lookup("/re/wild123dog.html", "POST");
		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/wild${id}dog.html", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);
		assertEquals(2, set.deep);
		assertEquals(1, set.actionPathMacros.getMacrosCount());
		assertEquals("id", set.actionPathMacros.getNames()[0]);

		assertEquals(2, actionsManager.getActionsCount());
	}

	@Test
	public void testMacrosDups() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);

		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());
		madvocConfig.setPathMacroClass(RegExpPathMacros.class);

		actionsManager.register(ReAction.class, "duplo1");
		actionsManager.register(ReAction.class, "duplo2");

		ActionConfig cfg = actionsManager.lookup("/re/duplo/123", "GET");
		assertNotNull(cfg);
		ActionConfigSet set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/duplo/${id:^[0-9]+}", cfg.actionPath);
		assertEquals(3, set.deep);
		assertEquals(1, set.actionPathMacros.getMacrosCount());
		assertEquals("id", set.actionPathMacros.getNames()[0]);

		cfg = actionsManager.lookup("/re/duplo/aaa", "GET");
		assertNotNull(cfg);
		set = cfg.getActionConfigSet();
		assertEquals(ReAction.class, cfg.actionClass);
		assertEquals("/re/duplo/${sid}", cfg.actionPath);
		assertEquals(3, set.deep);
		assertEquals(1, set.actionPathMacros.getMacrosCount());
		assertEquals("sid", set.actionPathMacros.getNames()[0]);

		assertEquals(2, actionsManager.getActionsCount());
	}

	@Test
	public void testMarkerClass() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		RootPackages rootPackages = madvocConfig.getRootPackages();

		String thisPackageName = this.getClass().getPackage().getName();

		assertNull(rootPackages.getPackageActionPath(thisPackageName + ".fixtures.tst3"));
		ActionConfig cfg = parse(actionMethodParser, "fixtures.tst3.JohnAction#hello");
		assertEquals("/root", rootPackages.getPackageActionPath(thisPackageName + ".fixtures.tst3"));
		assertEquals("/root/john.hello.html", cfg.actionPath);

		cfg = parse(actionMethodParser, "fixtures.tst3.JimAction#hello");
		assertEquals("/my-root/jim.my-hello.html", cfg.actionPath);

		assertNull(rootPackages.getPackageActionPath(thisPackageName + ".fixtures.tst3.lvl1"));
		cfg = parse(actionMethodParser, "fixtures.tst3.lvl1.EmaAction#hello");
		assertEquals("/root/lvl1/ema.hello.html", cfg.actionPath);
		assertEquals("/root/lvl1", rootPackages.getPackageActionPath(thisPackageName + ".fixtures.tst3.lvl1"));

		assertNull(rootPackages.getPackageActionPath(thisPackageName + ".fixtures.tst3.lvl2"));
		cfg = parse(actionMethodParser, "fixtures.tst3.lvl2.DidyAction#hello");
		assertEquals("/gig/didy.hello.html", cfg.actionPath);
		assertEquals("/gig", rootPackages.getPackageActionPath(thisPackageName + ".fixtures.tst3.lvl2"));

	}

	@Test
	public void testZqq() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.getRootPackages().addRootPackageOf(this.getClass());

		actionsManager.register(ReAction.class, "zqq1");
		actionsManager.register(ReAction.class, "zqq2");

		ActionConfig cfg = actionsManager.lookup("/config/dba.delete_multi", "GET");
		assertNotNull(cfg);

		assertEquals("/${entityName}/dba.delete_multi", cfg.getActionPath());
	}

}
