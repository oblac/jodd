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

package jodd.jerry;

import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
