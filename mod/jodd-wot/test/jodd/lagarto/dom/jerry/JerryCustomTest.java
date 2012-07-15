// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

import jodd.lagarto.dom.DOMBuilderTagVisitor;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.util.StringUtil;
import junit.framework.TestCase;

public class JerryCustomTest extends TestCase {

	public static class CustomJerryParser extends Jerry.JerryParser {
		@Override
		protected LagartoDOMBuilder createLagartoDOMBuilder() {
			return new LagartoDOMBuilder() {
				@Override
				protected DOMBuilderTagVisitor createDOMDomBuilderTagVisitor() {
					return new DOMBuilderTagVisitor(this) {
						@Override
						public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, CharSequence comment) {
							String cc = expression.toString().trim();

							if (cc.equals("if !IE") == false) {
								enabled = cc.equals("endif");
							}
						}
					};
				}
			};
		}
	}

	public void testConditionalTags() {
		Jerry.JerryParser jerryParser = new CustomJerryParser();

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


	public static class MyJerry {
		public static Jerry jerry(CharSequence charSequence) {
			return new CustomJerryParser().parse(charSequence);
		}
	}

	public void testConditionalTags2() {
		Jerry doc = MyJerry.jerry(
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
}
