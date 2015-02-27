// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.tst.BooAction;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActionResultTest extends MadvocTestCase {

	@Test
	public void testResolveResultPath() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);

		String path = "/boo.foo.html";
		ResultPath resultPath = resultMapper.resolveResultPath(path, "ok");
		assertEquals("/boo.foo.html.ok", resultPath.getPathValue());
		assertEquals("/boo.foo.html", resultPath.getPath());
		assertEquals("ok", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "doo.ok");
		assertEquals("/boo.foo.html.doo.ok", resultPath.getPathValue());
		assertEquals("/boo.foo.html", resultPath.getPath());
		assertEquals("doo.ok", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "#ok");
		assertEquals("/boo.foo.ok", resultPath.getPathValue());
		assertEquals("/boo.foo.ok", resultPath.getPath());
		assertNull(resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "#.ok");
		assertEquals("/boo.foo.ok", resultPath.getPathValue());
		assertEquals("/boo.foo", resultPath.getPath());
		assertEquals("ok", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "#.ok.do");
		assertEquals("/boo.foo.ok.do", resultPath.getPathValue());
		assertEquals("/boo.foo", resultPath.getPath());
		assertEquals("ok.do", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "##ok");
		assertEquals("/boo.ok", resultPath.getPathValue());
		assertEquals("/boo.ok", resultPath.getPath());
		assertNull(resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "##.ok");
		assertEquals("/boo.ok", resultPath.getPathValue());
		assertEquals("/boo", resultPath.getPath());
		assertEquals("ok", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "##ok.do");
		assertEquals("/boo.ok.do", resultPath.getPathValue());
		assertEquals("/boo.ok.do", resultPath.getPath());
		assertNull(resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "##ok..do");
		assertEquals("/boo.ok.do", resultPath.getPathValue());
		assertEquals("/boo.ok", resultPath.getPath());
		assertEquals("do", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "#");
		assertEquals("/boo.foo", resultPath.getPath());
		assertNull(resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, null);
		assertEquals("/boo.foo.html", resultPath.getPath());
		assertNull(resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "/xxx");
		assertEquals("/xxx", resultPath.getPath());
		assertNull(resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "/xxx.ext");
		assertEquals("/xxx.ext", resultPath.getPath());
		assertNull(resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "/xxx..ext");
		assertEquals("/xxx", resultPath.getPath());
		assertEquals("ext", resultPath.getValue());

		path = "/boo.html";
		resultPath = resultMapper.resolveResultPath(path, "ok");
		assertEquals("/boo.html", resultPath.getPath());
		assertEquals("ok", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "#.ok");
		assertEquals("/boo", resultPath.getPath());
		assertEquals("ok", resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "##ok");
		assertEquals("/ok", resultPath.getPath());
		assertEquals(null, resultPath.getValue());

		resultPath = resultMapper.resolveResultPath(path, "##.ok");
		assertEquals("/", resultPath.getPath());
		assertEquals("ok", resultPath.getValue());
	}

	@Test
	public void testMethodWithPrefix() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();
		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		MadvocConfig madvocConfig = webapp.getComponent(MadvocConfig.class);
		madvocConfig.setResultPathPrefix("/WEB-INF");

		String path = "/boo.foo";

		ResultPath resultPath = resultMapper.resolveResultPath(path, "ok");
		assertEquals("/WEB-INF/boo.foo.ok", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "doo.ok");
		assertEquals("/WEB-INF/boo.foo.doo.ok", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "#ok");
		assertEquals("/WEB-INF/boo.ok", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "#");
		assertEquals("/WEB-INF/boo", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "#doo.ok");
		assertEquals("/WEB-INF/boo.doo.ok", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, null);
		assertEquals("/WEB-INF/boo.foo", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "/xxx");
		assertEquals("/xxx", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "/xxx.ext");
		assertEquals("/xxx.ext", resultPath.getPathValue());
	}

	@Test
	public void testAlias() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		actionsManager.register(BooAction.class, "foo5");
		actionsManager.registerPathAlias("ok", "xxx.jsp");
		actionsManager.registerPathAlias("sok", "zzz");

		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);

		String path = "/boo.foo.html";

		ResultPath resultPath = resultMapper.resolveResultPath(path, "/<ok>?foo=1");
		assertEquals("/xxx.jsp?foo=1", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "<sok>");
		assertEquals("/boo.foo.html.zzz", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "#<sok>");
		assertEquals("/boo.foo.zzz", resultPath.getPathValue());

		resultPath = resultMapper.resolveResultPath(path, "<dude>?foo=1");
		assertEquals("/xxx.html?foo=1", resultPath.getPathValue());
	}

	@Test
	public void testAlias2() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		actionsManager.register(BooAction.class, "foo2");
		actionsManager.registerPathAlias("/boo.foo2.xxx", "/aliased");

		ResultMapper resultMapper = webapp.getComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.getComponent(ActionMethodParser.class);

		ActionConfig cfg = parse(actionMethodParser, "tst.BooAction#foo2");
		String path = cfg.getActionPath();

		String resultPath = resultMapper.resolveResultPathString(path, null);
		assertEquals("/aliased", resultPath);
	}

	@Test
	public void testAlias3() {
		WebApplication webapp = new WebApplication(true);
		webapp.registerMadvocComponents();

		ActionsManager actionsManager = webapp.getComponent(ActionsManager.class);
		actionsManager.register(BooAction.class, "foo2");

		assertEquals("/boo.foo2.xxx", actionsManager.lookup(BooAction.class.getName() + "#foo2").actionPath);
	}

}