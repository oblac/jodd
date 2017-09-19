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
import jodd.madvoc.config.ManualMadvocConfigurator;
import jodd.madvoc.interceptor.EchoInterceptor;
import jodd.madvoc.result.TextResult;
import jodd.madvoc.fixtures.tst.BooAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManualRegistrationTest {

	public static class ManualRegistration extends ManualMadvocConfigurator {
		public void configure() {
			result(TextResult.class);
			action()
					.path("/hello")
					.mapTo(BooAction.class, "foo1")
					.bind();

			action()
					.path("/world")
					.mapTo(BooAction.class, "foo2")
					.interceptBy(EchoInterceptor.class)
					.bind();

			interceptor(EchoInterceptor.class).setPrefixIn("====> ");
		}
	}

	@Test
	public void testManualAction() {
		Madvoc madvoc = new Madvoc();
		madvoc.setMadvocConfiguratorClass(ManualRegistration.class);
		madvoc.startNewWebApplication(null);

		ActionsManager actionsManager = madvoc.getWebApplication().getComponent(ActionsManager.class);

		assertEquals(2, actionsManager.getActionsCount());

		ActionConfig actionConfig = actionsManager.lookup("/hello", "GET");
		assertNotNull(actionConfig);
		assertEquals(BooAction.class, actionConfig.getActionClass());
		assertEquals("foo1", actionConfig.actionClassMethod.getName());

		actionConfig = actionsManager.lookup("/world", "GET");
		assertNotNull(actionConfig);
		assertEquals(BooAction.class, actionConfig.getActionClass());
		assertEquals("foo2", actionConfig.actionClassMethod.getName());
	}
}
