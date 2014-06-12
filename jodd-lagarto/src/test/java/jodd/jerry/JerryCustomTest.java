// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jerry;

import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.util.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JerryCustomTest {

	@Test
	public void testConditionalTags() {
		Jerry.JerryParser jerry = new Jerry.JerryParser();

		((LagartoDOMBuilder) jerry.getDOMBuilder()).getConfig().setIgnoreComments(true);

		Jerry doc = jerry.parse(
				"<html>" +
						"    <!--[if lt IE 7]>  <body class=\"ie ie6 ie_lte_9 ie_lte_8 ie_lte_7\"> <![endif]-->\n" +
						"    <!--[if IE 7]>     <body class=\"ie ie7 ie_lte_9 ie_lte_8 ie_lte_7\"> <![endif]-->\n" +
						"    <!--[if IE 8]>     <body class=\"ie ie8 ie_lte_9 ie_lte_8\">  <![endif]-->\n" +
						"    <!--[if IE 9]>     <body class=\"ie ie9 ie_lte_9\">           <![endif]-->\n" +
						"    <!--[if gt IE 9]>  <body class=\"ie\">                        <![endif]-->\n" +
						"    <!--[if !IE]>xxx--><body><!--<![endif]-->\n" +
						"</body></html>"
		);
		String html = doc.html();
		html = StringUtil.removeChars(html, " \n\r");

		assertEquals("<html><body></body></html>", html);
	}

	@Test
	public void testConditionalTags2() {
		Jerry.JerryParser jerry = new Jerry.JerryParser();
		((LagartoDOMBuilder) jerry.getDOMBuilder()).getConfig()
				.setIgnoreComments(true)
				.setEnableConditionalComments(true)
				.setCondCommentIEVersion(8);

		Jerry doc = jerry.parse(
				"<html>" +
						"    <!--[if lt IE 7]>  <body class=\"ie ie6 ie_lte_9 ie_lte_8 ie_lte_7\"> <![endif]-->\n" +
						"    <!--[if IE 7]>     <body class=\"ie ie7 ie_lte_9 ie_lte_8 ie_lte_7\"> <![endif]-->\n" +
						"    <!--[if IE 8]>     <body class=\"ie ie8 ie_lte_9 ie_lte_8\">  <![endif]-->\n" +
						"    <!--[if IE 9]>     <body class=\"ie ie9 ie_lte_9\">           <![endif]-->\n" +
						"    <!--[if gt IE 9]>  <body class=\"ie\">                        <![endif]-->\n" +
						"    <!--[if !IE]>xxx--><body><!--<![endif]-->\n" +
						"</body></html>"
		);
		String html = doc.html();
		html = StringUtil.removeChars(html, " \n\r");

		assertEquals("<html><bodyclass=\"ieie8ie_lte_9ie_lte_8\"></body></html>", html);
	}


	@Test
	public void testConditionalTags3() {
		Jerry.JerryParser jerry = new Jerry.JerryParser();
		((LagartoDOMBuilder) jerry.getDOMBuilder()).getConfig()
				.setIgnoreComments(true)
				.setEnableConditionalComments(true)
				.setCondCommentIEVersion(10);

		Jerry doc = jerry.parse(
				"<html>" +
						"    <!--[if lt IE 7]>  <body class=\"ie ie6 ie_lte_9 ie_lte_8 ie_lte_7\"> <![endif]-->\n" +
						"    <!--[if IE 7]>     <body class=\"ie ie7 ie_lte_9 ie_lte_8 ie_lte_7\"> <![endif]-->\n" +
						"    <!--[if IE 8]>     <body class=\"ie ie8 ie_lte_9 ie_lte_8\">      <![endif]-->\n" +
						"    <!--[if IE 9]>     <body class=\"ie ie9 ie_lte_9\">           <![endif]-->\n" +
						"    <!--[if gt IE 9]>  <body class=\"ie\">                    <![endif]-->\n" +
						"    <!--[if !IE]><!--> <body> <!--<![endif]--> \n" +
						"</body></html>"
		);
		String html = doc.html();
		html = StringUtil.removeChars(html, " \n\r");

		assertEquals("<html><bodyclass=\"ie\"></body></html>", html);
	}
}
