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

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static jodd.jerry.Jerry.jerry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JerryTest {
	protected String testDataRoot;

	@Before
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = JerryTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	@Test
	public void testText1() {
		String html = readFile("text1.html");
		String text = jerry(html).$("div.demo-container").text();

		text = StringUtil.remove(text, "\r\n").trim();
		text = StringUtil.compressChars(text, ' ');
		assertEquals("Demonstration Box list item 1 list item 2", text);
	}

	@Test
	public void testHtml1() {
		String html = readFile("html1.html");
		String text = jerry(html).$("div.demo-container").html();

		assertEquals("<div class=\"demo-box\">Demonstration Box</div>", text.trim());
	}

	@Test
	public void testHtml2() {
		String html = readFile("html2.html");
		String htmlOK = readFile("html2-ok.html");

		Jerry doc = jerry(html);
		Jerry p = doc.$("p:first");
		String htmlContent = p.html();
		p.text(htmlContent);

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testHtml3() {
		String html = readFile("html3.html");
		String htmlOK = readFile("html3-ok.html");

		Jerry doc = jerry(html);
		doc.$("div.demo-container").html("<p>All new content. <em>You bet!</em></p>");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testHtml4() {
		String html = readFile("html4.html");
		String htmlOK = readFile("html4-ok.html");

		Jerry doc = jerry(html);
		doc.$("div").html("<span class='red'>Hello <b>Again</b></span>");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testHtml5() {
		String html = readFile("html5.html");
		String htmlOK = readFile("html5-ok.html");

		Jerry doc = jerry(html);
		doc.$("div").html("<b>Wow!</b> Such excitement...");
		doc.$("div b").append("!!!").css("color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testAdd() {
		String html = readFile("add.html");
		String htmlOK = readFile("add-ok.html");

		Jerry $ = jerry(html).$("div").css("border", "2px solid red")
				.add("p")
				.css("background", "yellow");

		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testEnd() {
		String html = readFile("end.html");
		String htmlOK = readFile("end-ok.html");

		Jerry $ = jerry(html).$("p").find("span").end().css("border", "2px red solid");
		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testNot() {
		String html = readFile("not.html");
		String htmlOK = readFile("not-ok.html");

		Jerry $ = jerry(html).$("div").not(".green, #blueone").css("border-color", "red");
		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testFirst() {
		String html = readFile("first.html");
		String htmlOK = readFile("first-ok.html");

		Jerry $ = jerry(html).$("p span").first().addClass("highlight");
		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testLast() {
		String html = readFile("last.html");
		String htmlOK = readFile("last-ok.html");

		Jerry $ = jerry(html).$("p span").last().addClass("highlight");
		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testAddClass() {
		String html = readFile("addClass.html");
		String htmlOK = readFile("addClass-ok.html");

		Jerry $ = jerry(html).$("p:last").addClass("selected");
		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testPseudoLast() {
		String html = readFile("pseudoLast.html");
		String htmlOK = readFile("pseudoLast-ok.html");

		Jerry $ = jerry(html).$("tr:last").css("background-color", "yellow", "font-weight", "bolder");
		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testPseudoFirst() {
		String html = readFile("pseudoFirst.html");
		String htmlOK = readFile("pseudoFirst-ok.html");

		Jerry $ = jerry(html).$("tr:first").css("font-style", "italic");
		assertEquals(htmlOK, actualHtml($));
	}

	@Test
	public void testPseudoButton() {
		String html = readFile("pseudoButton.html");
		String htmlOK = readFile("pseudoButton-ok.html");

		Jerry doc = jerry(html);
		Jerry input = doc.$(":button").css("background", "yellow", "border", "3px red solid");
		doc.$("div").text("For this type jQuery found " + input.length() + ".").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoCheckbox() {
		String html = readFile("pseudoCheckbox.html");
		String htmlOK = readFile("pseudoCheckbox-ok.html");

		Jerry doc = jerry(html);
		Jerry input = doc.$("form input:checkbox").wrap("<span></span>").parent().css("background", "yellow", "border", "3px red solid");
		doc.$("div").text("For this type jQuery found " + input.length() + ".").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoFile() {
		String html = readFile("pseudoFile.html");
		String htmlOK = readFile("pseudoFile-ok.html");

		Jerry doc = jerry(html);
		Jerry input = doc.$("form input:file").css("background", "yellow", "border", "3px red solid");
		doc.$("div").text("For this type jQuery found " + input.length() + ".").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoInput() {
		String html = readFile("pseudoInput.html");
		String htmlOK = readFile("pseudoInput-ok.html");

		Jerry doc = jerry(html);
		Jerry allInputs = doc.$(":input");
		Jerry formChildren = doc.$("form > *");
		doc.$("#messages").text("Found " + allInputs.length() + " inputs and the form has " +
				formChildren.length() + " children.");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoHeader() {
		String html = readFile("pseudoHeader.html");
		String htmlOK = readFile("pseudoHeader-ok.html");

		Jerry doc = jerry(html);
		doc.$(":header").css("background", "#ccc", "color", "blue");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoParent() {
		String html = readFile("pseudoParent.html");
		String htmlOK = readFile("pseudoParent-ok.html");

		Jerry doc = jerry(html);
		doc.$("td:parent").css("background", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoSelected() {
		String html = readFile("pseudoSelected.html");
		String htmlOK = readFile("pseudoSelected-ok.html");

		Jerry doc = jerry(html);
		final StringBuilder str = new StringBuilder();
		doc.$("select option:selected").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				str.append($this.text()).append(' ');
				return true;
			}
		});
		doc.$("div").text(str.toString());
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoEq() {
		String html = readFile("pseudoEq.html");
		String htmlOK = readFile("pseudoEq-ok.html");

		Jerry doc = jerry(html);
		doc.$("td:eq(2)").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoEq2() {
		String html = readFile("pseudoEq2.html");
		String htmlOK = readFile("pseudoEq2-ok.html");

		Jerry doc = jerry(html);
		doc.$("ul.nav li:eq(1)").css("backgroundColor", "#ff0");

		doc.$("ul.nav").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				$this.find("li:eq(1)").css("fontStyle", "italic");
				return true;
			}
		});

		doc.$("ul.nav li:nth-child(2)").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoEven() {
		String html = readFile("pseudoEven.html");
		String htmlOK = readFile("pseudoEven-ok.html");

		Jerry doc = jerry(html);
		doc.$("tr:even").css("background-color", "#bbbbff");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoGt() {
		String html = readFile("pseudoGt.html");
		String htmlOK = readFile("pseudoGt-ok.html");

		Jerry doc = jerry(html);
		doc.$("td:gt(4)").css("text-decoration", "line-through");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoContains() {
		String html = readFile("pseudoContains.html");
		String htmlOK = readFile("pseudoContains-ok.html");

		Jerry doc = jerry(html);
		doc.$("div:contains('John')").css("text-decoration", "underline");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testPseudoContains2() {
		String html = readFile("pseudoContains.html");
		String htmlOK = readFile("pseudoContains-ok.html");

		Jerry doc = jerry(html);
		doc.$("div:contains(John)").css("text-decoration", "underline");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testNext() {
		String html = readFile("next.html");
		String htmlOK = readFile("next-ok.html");

		Jerry doc = jerry(html);
		doc.$("button[disabled]").next().text("this button is disabled");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testEq() {
		String html = readFile("eq.html");
		String htmlOK = readFile("eq-ok.html");

		Jerry doc = jerry(html);
		doc.$("li").eq(1).css("background-color", "red");
		doc.$("li").eq(-1).css("background-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testSibling() {
		String html = readFile("sibling.html");
		String htmlOK = readFile("sibling-ok.html");

		Jerry doc = jerry(html);
		doc.$("li.third-item").siblings().css("background-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testSibling2() {
		String html = readFile("sibling2.html");
		String htmlOK = readFile("sibling2-ok.html");

		Jerry doc = jerry(html);
		int len = doc.$(".hilite").siblings().css("color", "red").length();
		doc.$("b").text(String.valueOf(len));

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testRemove() {
		String html = readFile("remove.html");
		String htmlOK = readFile("remove-ok.html");

		Jerry doc = jerry(html);
		doc.$("p").remove();

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testEmpty() {
		String html = readFile("empty.html");
		String htmlOK = readFile("empty-ok.html");

		Jerry doc = jerry(html);
		doc.$("p").empty();

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testBefore() {
		String html = readFile("before.html");
		String htmlOK = readFile("before-ok.html");

		Jerry doc = jerry(html);
		doc.$("p").before("<b>Hello</b>");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testIs() {
		String html = readFile("is.html");
		String htmlOK = readFile("is-ok.html");

		Jerry doc = jerry(html);
		doc.$("div").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				if ($this.is(":first-child")) {
					$this.text("Its the first div.");
				} else if ($this.is(".blue,.red")) {
					$this.text("Its a blue or red div.");
				} else if ($this.is(":contains(Peter)")) {
					$this.text("Its Peter!");
				} else {
					$this.html("Its nothing <em>special</em>.");
				}
				return true;
			}
		});

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testIs2() {
		String html = readFile("is2.html");
		String htmlOK = readFile("is2-ok.html");

		Jerry doc = jerry(html);
		boolean isFormParent = doc.$("input[type='checkbox']").parent().is("form");
		doc.$("div").text("isFormParent = " + isFormParent);

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testIs3() {
		String html = readFile("is3.html");
		String htmlOK = readFile("is3-ok.html");

		Jerry doc = jerry(html);
		boolean isFormParent = doc.$("input[type='checkbox']").parent().is("form");
		doc.$("div").text("isFormParent = " + isFormParent);

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testFilter() {
		String html = readFile("filter.html");
		String htmlOK = readFile("filter-ok.html");

		Jerry doc = jerry(html);
		doc.$("li").filter(":even").css("background-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testFilter2() {
		String html = readFile("filter2.html");
		String htmlOK = readFile("filter2-ok.html");

		Jerry doc = jerry(html);
		doc.$("li").filter(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				return Jerry.$("strong", $this).length() == 1;
			}
		}).css("background-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testFilter2_2() {
		String html = readFile("filter2.html");
		String htmlOK = readFile("filter2-ok2.html");

		Jerry doc = jerry(html);
		doc.$("li").filter(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				return index % 3 == 2;
			}
		}).css("background-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testFilter3() {
		String html = readFile("filter3.html");
		String htmlOK = readFile("filter3-ok.html");

		Jerry doc = jerry(html);
		doc.$("div").css("background", "#c8ebcc")
				.filter(".middle")
				.css("border-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testFilter4() {
		String html = readFile("filter4.html");
		String htmlOK = readFile("filter4-ok.html");

		Jerry doc = jerry(html);
		doc.$("div").css("background", "#b4b0da")
				.filter(new JerryFunction() {
					public boolean onNode(Jerry $this, int index) {
						return index == 1 || $this.attr("id").equals("fourth");
					}
				})
				.css("border", "3px double red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	@Test
	public void testForm() {
		String html = readFile("form.html");

		Jerry doc = jerry(html);

		final Map<String, String[]> params = new HashMap<>();

		doc.form("#myform", new JerryFormHandler() {
			public void onForm(Jerry form, Map<String, String[]> parameters) {
				params.putAll(parameters);
			}
		});

		assertEquals(6, params.size());

		assertEquals("text!", params.get("n_text")[0]);
		assertEquals("password!", params.get("n_password")[0]);
		assertEquals("on", params.get("n_checkbox1")[0]);
		assertEquals("check1!", params.get("n_checkbox2")[0]);
		assertEquals("check2!", params.get("n_checkbox2")[1]);
		assertEquals("sel2!", params.get("n_select")[0]);
		assertEquals("sel3!", params.get("n_select")[1]);
		assertEquals("textarea!", params.get("n_textarea")[0]);
	}

	@Test
	public void testHtmlAll() {
		Jerry j = Jerry.jerry("<div><span>1</span></div><div><span>2</span></div>");

		assertEquals(j.htmlAll(true), j.htmlAll(false));

		j = j.$("div");
		assertEquals("<span>1</span>", j.html());
		assertEquals("<span>1</span><span>2</span>", j.htmlAll(false));
		assertEquals("<div><span>1</span></div><div><span>2</span></div>", j.htmlAll(true));

		Jerry j2 = j.find("span");
		assertEquals(2, j2.length());

		assertEquals("12", j2.text());
		assertEquals("1", j2.html());
		assertEquals("12", j2.htmlAll(false));
		assertEquals("<span>1</span><span>2</span>", j2.htmlAll(true));
	}

	@Test
	public void testCamelCaseClassesIssue() {
		Jerry j = Jerry.jerry("<div id='d'></div>");

		j.$("#d").css("background-color", "red");

		assertEquals("<div id=\"d\" style=\"background-color:red;\"></div>", j.html());

		j.$("#d").css("background-color", "");

		assertEquals("<div id=\"d\" style=\"\"></div>", j.html());

		j.$("#d").addClass("fooBar");

		assertEquals("<div id=\"d\" style=\"\" class=\"fooBar\"></div>", j.html());

		assertTrue(j.$("#d").hasClass("fooBar"));
		assertFalse(j.$("#d").hasClass("foo-bar"));

		j.$("#d").addClass("foo-bar");
		assertEquals("<div id=\"d\" style=\"\" class=\"fooBar foo-bar\"></div>", j.html());

		j.$("#d").toggleClass("foo-bar", "fooBar");

		assertEquals("<div id=\"d\" style=\"\" class=\"\"></div>", j.html());
	}

	// ---------------------------------------------------------------- tools

	private String actualHtml(Jerry $) {
		return $.root().get(0).getHtml();
	}

	private String readFile(String fileName) {
		try {
			return FileUtil.readString(new File(testDataRoot, fileName));
		} catch (IOException ignore) {
			return null;
		}
	}

}
