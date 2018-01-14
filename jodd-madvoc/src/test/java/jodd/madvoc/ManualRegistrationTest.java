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

import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.fixtures.tst.BooAction;
import jodd.madvoc.interceptor.EchoInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManualRegistrationTest {

	public static class ManualRegistration extends MadvocRouter {
		@Override
		public void start() {
			route()
				.path("/hello")
				.mapTo(BooAction.class, "foo1")
				.bind();

			route()
				.path("/world")
				.mapTo(BooAction.class, "foo2")
				.interceptBy(EchoInterceptor.class)
				.bind();

			interceptor(EchoInterceptor.class, i -> i.setPrefixIn("====> "));     // additional interceptor configuration
		}
	}

	@Test
	void testManualAction_asComponent() {
		WebApp webApp = WebApp
			.createWebApp()
			.registerComponent(ManualRegistration.class)
			.start();

		ActionsManager actionsManager = webApp.madvocContainer().requestComponent(ActionsManager.class);

		assertEquals(2, actionsManager.getActionsCount());

		ActionRuntime actionRuntime = actionsManager.lookup("GET", MadvocUtil.splitPathToChunks("/hello"));
		assertNotNull(actionRuntime);
		assertEquals(BooAction.class, actionRuntime.getActionClass());
		assertEquals("foo1", actionRuntime.getActionClassMethod().getName());

		actionRuntime = actionsManager.lookup("GET", MadvocUtil.splitPathToChunks("/world"));
		assertNotNull(actionRuntime);
		assertEquals(BooAction.class, actionRuntime.getActionClass());
		assertEquals("foo2", actionRuntime.getActionClassMethod().getName());
	}

	@Test
	void testManualAction_asArgument() {
		WebApp webApp = WebApp
			.createWebApp()
			.start(madvoc -> madvoc
				.route()
					.path("/hello")
					.mapTo(BooAction.class, "foo1")
					.bind()
				.route()
					.path("/world")
					.mapTo(BooAction.class, "foo2")
					.interceptBy(EchoInterceptor.class)
					.bind()
				.interceptor(EchoInterceptor.class, i->i.setPrefixIn("====> ")
				)
			);

		ActionsManager actionsManager = webApp.madvocContainer().requestComponent(ActionsManager.class);

		assertEquals(2, actionsManager.getActionsCount());

		ActionRuntime actionRuntime = actionsManager.lookup("GET", MadvocUtil.splitPathToChunks("/hello"));
		assertNotNull(actionRuntime);
		assertEquals(BooAction.class, actionRuntime.getActionClass());
		assertEquals("foo1", actionRuntime.getActionClassMethod().getName());

		actionRuntime = actionsManager.lookup("GET", MadvocUtil.splitPathToChunks("/world"));
		assertNotNull(actionRuntime);
		assertEquals(BooAction.class, actionRuntime.getActionClass());
		assertEquals("foo2", actionRuntime.getActionClassMethod().getName());
	}
}
