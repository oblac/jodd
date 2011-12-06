// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.test.BooAction;

public class ActionResultTest extends MadvocTestCase {

	public void testMethod() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo");
		assertEquals("/boo.foo.html", cfg.actionPath);

		String resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.foo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "doo.ok");
		assertEquals("/boo.foo.doo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/boo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#");
		assertEquals("/boo", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#doo.ok");
		assertEquals("/boo.doo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo.foo", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "/xxx");
		assertEquals("/xxx", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "/xxx.ext");
		assertEquals("/xxx.ext", resultPath);
	}

	public void testMethod1() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo1");
		assertEquals("/boo.xxx.html", cfg.actionPath);
		
		String resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.xxx.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo.xxx", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "/xxx.ext");
		assertEquals("/xxx.ext", resultPath);

	}

	public void testMethod2() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo2");
		assertEquals("/boo.foo2.xxx", cfg.actionPath);
		assertTrue(cfg.isPathEndsWithExtension());

		String resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.foo2.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo.foo2", resultPath);
	}

	public void testMethod3() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo3");
		assertEquals("/boo.html", cfg.actionPath);

		String resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#");
		assertEquals("/", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#doo.ok");
		assertEquals("/doo.ok", resultPath);
	}

	public void testMethod4() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo4");
		assertEquals("/xxx", cfg.actionPath);
		assertNull(cfg.actionMethod);

		String resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/xxx.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/xxx", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#");
		assertEquals("/", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#doo.ok");
		assertEquals("/doo.ok", resultPath);
	}

	public void testMethod5() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);
		assertEquals("POST", cfg.actionMethod);

		String resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/xxx.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/xxx", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#doo.ok");
		assertEquals("/doo.ok", resultPath);
	}

	public void testMethod8() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig config = webapp.getComponent(MadvocConfig.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo8");
		assertEquals("/boo.foo8", cfg.actionPath);
		assertNull(cfg.actionPathExtension);
		assertFalse(cfg.isPathEndsWithExtension());

		for (int i = 0; i < 2; i++) {

			config.setStrictExtensionStripForResultPath(i != 0);

			// extension is null, no stripping!
			String resultPath = resultMapper.resolveResultPath(cfg, ".ok");
			assertEquals("/boo.foo8.ok", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, "ok");
			assertEquals("/boo.foo8.ok", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, ".");
			assertEquals("/boo.foo8", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, null);
			assertEquals("/boo.foo8", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, "#ok");
			assertEquals("/boo.ok", resultPath);
		}

	}

	public void testMethod81() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig config = webapp.getComponent(MadvocConfig.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo81");
		assertEquals("/boo.foo81", cfg.actionPath);
		assertEquals("html", cfg.actionPathExtension);
		assertFalse(cfg.isPathEndsWithExtension());

		config.setStrictExtensionStripForResultPath(false);

		// different extension, stripping
		String resultPath = resultMapper.resolveResultPath(cfg, ".ok");
		assertEquals("/boo.foo81.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, ".");
		assertEquals("/boo.foo81", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);

		config.setStrictExtensionStripForResultPath(true);

		// different extension!!! NO stripping (like it is NULL!)
		resultPath = resultMapper.resolveResultPath(cfg, ".ok");
		assertEquals("/boo.foo81.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.foo81.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, ".");
		assertEquals("/boo.foo81", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo.foo81", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/boo.ok", resultPath);
	}

	public void testMethod82() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig config = webapp.getComponent(MadvocConfig.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo82");
		assertEquals("/boo.foo82.html", cfg.actionPath);
		assertEquals("html", cfg.actionPathExtension);
		assertTrue(cfg.isPathEndsWithExtension());

		for (int i = 0; i < 2; i++) {

			config.setStrictExtensionStripForResultPath(i != 0);

			// default, same extension stripping
			String resultPath = resultMapper.resolveResultPath(cfg, ".ok");
			assertEquals("/boo.foo82.html.ok", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, "ok");
			assertEquals("/boo.foo82.ok", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, ".");
			assertEquals("/boo.foo82.html", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, null);
			assertEquals("/boo.foo82", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, "#ok");
			assertEquals("/boo.ok", resultPath);

		}
	}

	public void testMethod83() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);
		MadvocConfig config = webapp.getComponent(MadvocConfig.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo83");
		assertEquals("/boo.foo83.json", cfg.actionPath);
		assertEquals("json", cfg.actionPathExtension);
		assertTrue(cfg.isPathEndsWithExtension());

		for (int i = 0; i < 2; i++) {

			config.setStrictExtensionStripForResultPath(i != 0);

			// default, same extension stripping
			String resultPath = resultMapper.resolveResultPath(cfg, ".ok");
			assertEquals("/boo.foo83.json.ok", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, "ok");
			assertEquals("/boo.foo83.ok", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, ".");
			assertEquals("/boo.foo83.json", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, null);
			assertEquals("/boo.foo83", resultPath);
			resultPath = resultMapper.resolveResultPath(cfg, "#ok");
			assertEquals("/boo.ok", resultPath);
		}

	}


	public void testAlias() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		MadvocConfig config = webapp.getComponent(MadvocConfig.class);
		config.registerPathAlias("ok", "xxx.jsp");
		config.registerPathAlias("sok", "zzz");

		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		actionsManager.register(BooAction.class, "foo5");

		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo");
		assertEquals("/boo.foo.html", cfg.actionPath);

		String resultPath = resultMapper.resolveResultPath(cfg, "/%ok%?foo=1");
		assertEquals("/xxx.jsp?foo=1", resultPath);

		resultPath = resultMapper.resolveResultPath(cfg, "%sok%");
		assertEquals("/boo.foo.zzz", resultPath);

		resultPath = resultMapper.resolveResultPath(cfg, "#%sok%");
		assertEquals("/boo.zzz", resultPath);

		resultPath = resultMapper.resolveResultPath(cfg, "%dude%?foo=1");
		assertEquals("/xxx.html?foo=1", resultPath);

	}

	public void testAlias2() {

		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		MadvocConfig config = webapp.getComponent(MadvocConfig.class);
		config.registerPathAlias("/boo.foo2", "/aliased");

		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		actionsManager.register(BooAction.class, "foo2");

		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo2");

		String resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/aliased", resultPath);

	}

	public void testAlias3() {

		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		MadvocConfig config = webapp.getComponent(MadvocConfig.class);
		config.setCreateDefaultAliases(true);

		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		actionsManager.register(BooAction.class, "foo2");

		assertEquals("/boo.foo2.xxx", config.lookupPathAlias(BooAction.class.getName() + "#foo2"));

	}

	public void testBack() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "test.BooAction#foo");
		assertEquals("/boo.foo.html", cfg.actionPath);

		String resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/boo.ok", resultPath);

		resultPath = resultMapper.resolveResultPath(cfg, "#[method].ok");
		assertEquals("/boo.foo.ok", resultPath);

		resultPath = resultMapper.resolveResultPath(cfg, "##[class].[method].ok");
		assertEquals("/boo.foo.ok", resultPath);

	}

}