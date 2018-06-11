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

package jodd.madvoc.component;

import jodd.madvoc.MadvocUtil;
import jodd.madvoc.WebApp;
import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.macro.RegExpPathMacros;
import jodd.madvoc.macro.WildcardPathMacros;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

class ActionsManagerTest {

	public static class FooAction {
		public void one() {
		}
		public void two() {
		}
		public void three() {
		}
	}

	@Test
	void testActionPathMacros1() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);

		actionsManager.registerAction(FooAction.class, "one", new ActionDefinition("/{one}"));

		ActionRuntime actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/foo"));
		assertNotNull(actionRuntime);

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/foo/boo"));
		assertNull(actionRuntime);
		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/foo/boo/zoo"));
		assertNull(actionRuntime);
	}

	@Test
	void testActionPathMacros2() {
		WebApp webapp = new WebApp();
		webapp.start();
		
		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);

		actionsManager.registerAction(FooAction.class, "two", new ActionDefinition("/xxx-{two}"));
		actionsManager.registerAction(FooAction.class, "one", new ActionDefinition("/{one}"));

		ActionRuntime actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/foo"));
		assertEquals("one", actionRuntime.getActionClassMethod().getName());

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/foo/boo"));
		assertNull(actionRuntime);

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/xxx-foo"));
		assertEquals("two", actionRuntime.getActionClassMethod().getName());	// best match!

	}

	@Test
	void testActionPathMacros3() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);

		actionsManager.registerAction(FooAction.class, "one", new ActionDefinition("/yyy-{one}"));
		actionsManager.registerAction(FooAction.class, "two", new ActionDefinition("/xxx-{two}"));

		assertEquals(2, actionsManager.getActionsCount());

		ActionRuntime actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/foo"));
		assertNull(actionRuntime);

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/yyy-111"));
		assertEquals("one", actionRuntime.getActionClassMethod().getName());

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/xxx-222"));
		assertEquals("two", actionRuntime.getActionClassMethod().getName());

		try {
			actionsManager.registerAction(FooAction.class, "two", new ActionDefinition("/xxx-{two}"));
			fail("error");
		} catch (Exception ex) {
			// ignore
		}
	}

	@Test
	void testActionPathMacros4() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);

		actionsManager.registerAction(FooAction.class, "one", new ActionDefinition("/dummy"));		// no macro
		actionsManager.registerAction(FooAction.class, "one", new ActionDefinition("/{one}"));
		actionsManager.registerAction(FooAction.class, "three", new ActionDefinition("/life/{three}"));
		actionsManager.registerAction(FooAction.class, "two", new ActionDefinition("/{two}/{three}"));

		ActionRuntime actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/foo"));
		assertEquals("one", actionRuntime.getActionClassMethod().getName());

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/scott/ramonna"));
		assertEquals("two", actionRuntime.getActionClassMethod().getName());

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/life/universe"));
		assertEquals("three", actionRuntime.getActionClassMethod().getName());

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/scott/ramonna/envy"));
		assertNull(actionRuntime);

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/life/universe/else"));
		assertNull(actionRuntime);
	}

	@Test
	void testActionPathMacrosRegexp() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);
		actionsManager.setPathMacroClass(RegExpPathMacros.class);

		actionsManager.registerAction(FooAction.class, "one", new ActionDefinition("/{one:[ab]+}"));

		ActionRuntime actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/a"));
		assertNotNull(actionRuntime);

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/ac"));
		assertNull(actionRuntime);
	}

	@Test
	void testActionPathMacrosWildcard() {
		WebApp webapp = new WebApp();
		webapp.start();

		ActionsManager actionsManager = webapp.madvocContainer().lookupComponent(ActionsManager.class);
		actionsManager.setPathMacroClass(WildcardPathMacros.class);

		actionsManager.registerAction(FooAction.class, "one", new ActionDefinition("/{one:a?a}"));

		ActionRuntime actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/aaa"));
		assertNotNull(actionRuntime);

		actionRuntime = actionsManager.routes.lookup(null, MadvocUtil.splitPathToChunks("/aab"));
		assertNull(actionRuntime);
	}
}
