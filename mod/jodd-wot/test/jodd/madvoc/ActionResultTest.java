// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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



		cfg = parse(actionMethodParser, "test.BooAction#foo1");
		assertEquals("/boo.xxx.html", cfg.actionPath);
		
		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.xxx.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo.xxx", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "/xxx.ext");
		assertEquals("/xxx.ext", resultPath);



		cfg = parse(actionMethodParser, "test.BooAction#foo2");
		assertEquals("/boo.foo2.xxx", cfg.actionPath);

		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.foo2.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo.foo2", resultPath);
		


		cfg = parse(actionMethodParser, "test.BooAction#foo3");
		assertEquals("/boo.html", cfg.actionPath);

		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#");
		assertEquals("/", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#doo.ok");
		assertEquals("/doo.ok", resultPath);



		cfg = parse(actionMethodParser, "test.BooAction#foo4");
		assertEquals("/xxx", cfg.actionPath);

		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/xxx.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/xxx", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#");
		assertEquals("/", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#doo.ok");
		assertEquals("/doo.ok", resultPath);


		
		cfg = parse(actionMethodParser, "test.BooAction#foo5");
		assertEquals("/xxx.html", cfg.actionPath);

		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/xxx.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/xxx", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#doo.ok");
		assertEquals("/doo.ok", resultPath);



		cfg = parse(actionMethodParser, "test.BooAction#foo8");
		assertEquals("/boo.foo8", cfg.actionPath);

		resultPath = resultMapper.resolveResultPath(cfg, ".ok");
		assertEquals("/boo.foo8.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "ok");
		assertEquals("/boo.ok", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, ".");
		assertEquals("/boo.foo8", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, null);
		assertEquals("/boo", resultPath);
		resultPath = resultMapper.resolveResultPath(cfg, "#ok");
		assertEquals("/ok", resultPath);
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
}