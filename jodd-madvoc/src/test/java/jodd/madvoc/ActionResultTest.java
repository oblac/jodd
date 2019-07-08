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
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.ResultPath;
import jodd.madvoc.fixtures.tst.BooAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActionResultTest extends MadvocTestCase {

	@Test
	void testResolveResultPath() {
		WebApp webapp = new WebApp();
		webapp.start();
		
		ResultMapper resultMapper = webapp.madvocContainer.lookupComponent(ResultMapper.class);

		String path = "/boo.foo.html";
		ResultPath resultPath = resultMapper.resolveResultPath(path, "ok");
		assertEquals("/boo.foo.html.ok", resultPath.pathValue());
		assertEquals("/boo.foo.html", resultPath.path());
		assertEquals("ok", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "doo.ok");
		assertEquals("/boo.foo.html.doo.ok", resultPath.pathValue());
		assertEquals("/boo.foo.html", resultPath.path());
		assertEquals("doo.ok", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "#ok");
		assertEquals("/boo.foo.ok", resultPath.pathValue());
		assertEquals("/boo.foo.ok", resultPath.path());
		assertNull(resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "#.ok");
		assertEquals("/boo.foo.ok", resultPath.pathValue());
		assertEquals("/boo.foo", resultPath.path());
		assertEquals("ok", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "#.ok.do");
		assertEquals("/boo.foo.ok.do", resultPath.pathValue());
		assertEquals("/boo.foo", resultPath.path());
		assertEquals("ok.do", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "##ok");
		assertEquals("/boo.ok", resultPath.pathValue());
		assertEquals("/boo.ok", resultPath.path());
		assertNull(resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "##.ok");
		assertEquals("/boo.ok", resultPath.pathValue());
		assertEquals("/boo", resultPath.path());
		assertEquals("ok", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "##ok.do");
		assertEquals("/boo.ok.do", resultPath.pathValue());
		assertEquals("/boo.ok.do", resultPath.path());
		assertNull(resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "##ok..do");
		assertEquals("/boo.ok.do", resultPath.pathValue());
		assertEquals("/boo.ok", resultPath.path());
		assertEquals("do", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "#");
		assertEquals("/boo.foo", resultPath.path());
		assertNull(resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, null);
		assertEquals("/boo.foo.html", resultPath.path());
		assertNull(resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "/xxx");
		assertEquals("/xxx", resultPath.path());
		assertNull(resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "/xxx.ext");
		assertEquals("/xxx.ext", resultPath.path());
		assertNull(resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "/xxx..ext");
		assertEquals("/xxx", resultPath.path());
		assertEquals("ext", resultPath.value());

		path = "/boo.html";
		resultPath = resultMapper.resolveResultPath(path, "ok");
		assertEquals("/boo.html", resultPath.path());
		assertEquals("ok", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "#.ok");
		assertEquals("/boo", resultPath.path());
		assertEquals("ok", resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "##ok");
		assertEquals("/ok", resultPath.path());
		assertEquals(null, resultPath.value());

		resultPath = resultMapper.resolveResultPath(path, "##.ok");
		assertEquals("/", resultPath.path());
		assertEquals("ok", resultPath.value());
	}

	@Test
	void testMethodWithPrefix() {
		final WebApp webapp = new WebApp();
		webapp.start();

		final ResultMapper resultMapper = webapp.madvocContainer().lookupComponent(ResultMapper.class);
		resultMapper.setResultPathPrefix("/WEB-INF");

		String path = "/boo.foo";

		ResultPath resultPath = resultMapper.resolveResultPath(path, "ok");
		assertEquals("/WEB-INF/boo.foo.ok", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "doo.ok");
		assertEquals("/WEB-INF/boo.foo.doo.ok", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "#ok");
		assertEquals("/WEB-INF/boo.ok", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "#");
		assertEquals("/WEB-INF/boo", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "#doo.ok");
		assertEquals("/WEB-INF/boo.doo.ok", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, null);
		assertEquals("/WEB-INF/boo.foo", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "/xxx");
		assertEquals("/xxx", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "/xxx.ext");
		assertEquals("/xxx.ext", resultPath.pathValue());
	}

	@Test
	void testAlias() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);
		actionsManager.registerAction(BooAction.class, "foo5", null);
		actionsManager.registerPathAlias("ok", "xxx.jsp");
		actionsManager.registerPathAlias("sok", "zzz");

		ResultMapper resultMapper = webapp.madvocContainer().lookupComponent(ResultMapper.class);

		String path = "/boo.foo.html";

		ResultPath resultPath = resultMapper.resolveResultPath(path, "/<ok>?foo=1");
		assertEquals("/xxx.jsp?foo=1", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "<sok>");
		assertEquals("/boo.foo.html.zzz", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "#<sok>");
		assertEquals("/boo.foo.zzz", resultPath.pathValue());

		resultPath = resultMapper.resolveResultPath(path, "<dude>?foo=1");
		assertEquals("/xxx.html?foo=1", resultPath.pathValue());
	}

	@Test
	void testAlias2() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);
		actionsManager.registerAction(BooAction.class, "foo2", null);
		actionsManager.registerPathAlias("/boo.foo2.xxx", "/aliased");

		ResultMapper resultMapper = webapp.madvocContainer().lookupComponent(ResultMapper.class);
		ActionMethodParser actionMethodParser = webapp.madvocContainer().lookupComponent(ActionMethodParser.class);

		ActionRuntime cfg = parse(actionMethodParser, "fixtures.tst.BooAction#foo2");
		String path = cfg.getActionPath();

		String resultPath = resultMapper.resolveResultPathString(path, null);
		assertEquals("/aliased", resultPath);
	}

	@Test
	void testAlias3() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);
		actionsManager.registerAction(BooAction.class, "foo2", null);

		assertEquals("/boo.foo2.xxx", actionsManager.lookup(BooAction.class.getName() + "#foo2").getActionPath());
	}

}
