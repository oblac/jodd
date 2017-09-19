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

import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.ServletRedirectResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResultsManagerTest {

	@Test
	public void testDuplicateResults1() {
		ResultsManager resultsManager = new ResultsManager() {
			@Override
			protected void initializeResult(ActionResult result) {
			}

			@Override
			protected boolean resultMayReplaceExistingOne(Class<? extends ActionResult> actionResultClass) {
				if (actionResultClass.getName().contains("Test")) {
					return true;
				}
				return super.resultMayReplaceExistingOne(actionResultClass);
			}
		};

		resultsManager.register(new ServletRedirectResult());	// new
		resultsManager.register(new ServletRedirectResult());	// ignore
		resultsManager.register(new MyRedirect1());				// replace

		assertNull(resultsManager.allResults.get(ServletRedirectResult.class));
		assertEquals(MyRedirect1.class, resultsManager.stringResults.get("redirect").getClass());

		resultsManager.register(new MyRedirect2());				// replace
		assertEquals(MyRedirect2.class, resultsManager.stringResults.get("redirect").getClass());

		assertEquals(1, resultsManager.allResults.size());
	}

	@Test
	public void testDuplicateResults2() {
		ResultsManager resultsManager = new ResultsManager() {
			@Override
			protected void initializeResult(ActionResult result) {
			}
			@Override
			protected boolean resultMayReplaceExistingOne(Class<? extends ActionResult> actionResultClass) {
				if (actionResultClass.getName().contains("Test")) {
					return true;
				}
				return super.resultMayReplaceExistingOne(actionResultClass);
			}
		};

		resultsManager.register(new MyRedirect1());				// register
		resultsManager.register(new MyRedirect1());				// ignore
		resultsManager.register(new ServletRedirectResult());	// ignore

		assertNull(resultsManager.allResults.get(ServletRedirectResult.class));
		assertEquals(MyRedirect1.class, resultsManager.stringResults.get("redirect").getClass());

		resultsManager.register(new MyRedirect2());				// ignore
		assertEquals(MyRedirect2.class, resultsManager.stringResults.get("redirect").getClass());

		assertEquals(1, resultsManager.allResults.size());
	}

	public static class MyRedirect1 extends ServletRedirectResult {
	}
	public static class MyRedirect2 extends ServletRedirectResult {
	}
}
