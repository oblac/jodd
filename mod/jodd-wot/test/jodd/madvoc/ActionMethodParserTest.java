// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.test.Boo1Action;
import jodd.madvoc.test.Boo2Action;
import jodd.madvoc.test.Boo3Action;
import jodd.madvoc.test.BooAction;
import jodd.madvoc.test2.Boo4Action;
import jodd.madvoc.test2.Boo5Action;

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

		cfg = parse(actionMethodParser, "test.BooAction#foo41");
		assertEquals("/xxx", cfg.actionPath);

		cfg = parse(actionMethodParser, "test.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);
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

		cfg = parse(actionMethodParser, "test.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);

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

}
