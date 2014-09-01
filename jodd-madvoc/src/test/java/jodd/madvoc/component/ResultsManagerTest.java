// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.result.ActionResult;
import jodd.madvoc.result.ServletRedirectResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ResultsManagerTest {

	@Test
	public void testDuplicateResults1() {
		ResultsManager resultsManager = new ResultsManager() {
			@Override
			protected void initializeResult(ActionResult result) {
			}
		};

		resultsManager.register(new ServletRedirectResult());
		resultsManager.register(new ServletRedirectResult());
		resultsManager.register(new MyRedirect1());

		assertNull(resultsManager.lookup(ServletRedirectResult.class));
		assertEquals(MyRedirect1.class, resultsManager.lookup("redirect").getClass());

		resultsManager.register(new MyRedirect2());
		assertEquals(MyRedirect2.class, resultsManager.lookup("redirect").getClass());

		assertEquals(1, resultsManager.allResults.size());
	}

	@Test
	public void testDuplicateResults2() {
		ResultsManager resultsManager = new ResultsManager() {
			@Override
			protected void initializeResult(ActionResult result) {
			}
		};

		resultsManager.register(new MyRedirect1());
		resultsManager.register(new MyRedirect1());
		resultsManager.register(new ServletRedirectResult());

		assertNotNull(resultsManager.lookup(ServletRedirectResult.class));
		assertEquals(ServletRedirectResult.class, resultsManager.lookup("redirect").getClass());

		resultsManager.register(new MyRedirect2());
		assertEquals(MyRedirect2.class, resultsManager.lookup("redirect").getClass());

		assertEquals(1, resultsManager.allResults.size());
	}

	public static class MyRedirect1 extends ServletRedirectResult {
	}
	public static class MyRedirect2 extends ServletRedirectResult {
	}
}