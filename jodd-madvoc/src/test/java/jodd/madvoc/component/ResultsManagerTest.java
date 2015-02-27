// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.ServletRedirectResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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