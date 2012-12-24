// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.htmlstapler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlStaplerCssTest {

	@Test
	public void testRelativeCssUrls() {


		HtmlStaplerBundlesManager htmlStapler = new HtmlStaplerBundlesManager("/", "", HtmlStaplerBundlesManager.Strategy.RESOURCES_ONLY);

		assertEquals(
				"background: url('/absolute/path')",
				htmlStapler.fixCssRelativeUrls("background: url(/absolute/path)", "css/"));

		assertEquals(
				"background: url('../css/relative/path')",
				htmlStapler.fixCssRelativeUrls("background: url(relative/path)", "css/"));
	}
}
