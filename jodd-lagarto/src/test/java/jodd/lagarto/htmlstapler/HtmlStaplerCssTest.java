// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.htmlstapler;

import junit.framework.TestCase;

public class HtmlStaplerCssTest extends TestCase {

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
