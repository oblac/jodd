// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

import javax.servlet.ServletContext;

public class HtmlStaplerCssTest extends TestCase {

	public void testRelativeCssUrls() {

		ServletContext servletContext = mock(ServletContext.class);
		when(servletContext.getRealPath(anyString())).thenReturn("");

		HtmlStaplerBundlesManager htmlStapler = new HtmlStaplerBundlesManager(servletContext, HtmlStaplerBundlesManager.Strategy.RESOURCES_ONLY);

		assertEquals(
				"background: url('/absolute/path')",
				htmlStapler.fixCssRelativeUrls("background: url(/absolute/path)", "css/"));

		assertEquals(
				"background: url('css/relative/path')",
				htmlStapler.fixCssRelativeUrls("background: url(relative/path)", "css/"));
	}
}
