// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

import jodd.util.StringUtil;
import junit.framework.TestCase;

public class JerryCustomTest extends TestCase {

	public void testConditionalTags() {
		Jerry.JerryParser jerryParser = new Jerry.JerryParser();

		Jerry doc = jerryParser.parse(
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

	public void testConditionalTags2() {
		Jerry doc = Jerry.jerry(
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

		assertEquals("<html><body></body></html>", html);
	}

	public void testConditionalTags3() {

		Jerry.JerryParser jerry  = Jerry.jerry();

		jerry.getDOMBuilder().setConditionalCommentExpression("if gt IE 9");

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
