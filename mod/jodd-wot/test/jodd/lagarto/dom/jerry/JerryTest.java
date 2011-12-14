// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static jodd.lagarto.dom.jerry.Jerry.jerry;

public class JerryTest extends TestCase {
	protected String testDataRoot;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		if (testDataRoot != null) {
			return;
		}
		URL data = JerryTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	public void testText1() {
		String html = readFile("text1.html");
		String text = jerry(html).$("div.demo-container").text();

		text = StringUtil.remove(text, "\r\n").trim();
		text = StringUtil.compressChars(text, ' ');
		assertEquals("Demonstration Box list item 1 list item 2", text);
	}

	public void testHtml1() {
		String html = readFile("html1.html");
		String text = jerry(html).$("div.demo-container").html();

		assertEquals("<div class=\"demo-box\">Demonstration Box</div>", text.trim());
	}

	public void testHtml2() {
		String html = readFile("html2.html");
		String htmlOK = readFile("html2-ok.html");
		
		Jerry doc = jerry(html);
		Jerry p = doc.$("p:first");
		String htmlContent = p.html();
		p.text(htmlContent);

		assertEquals(htmlOK, actualHtml(doc));
	}
	
	public void testHtml3() {
		String html = readFile("html3.html");
		String htmlOK = readFile("html3-ok.html");

		Jerry doc = jerry(html);
		doc.$("div.demo-container").html("<p>All new content. <em>You bet!</em></p>");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testHtml4() {
		String html = readFile("html4.html");
		String htmlOK = readFile("html4-ok.html");

		Jerry doc = jerry(html);
		doc.$("div").html("<span class='red'>Hello <b>Again</b></span>");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testHtml5() {
		String html = readFile("html5.html");
		String htmlOK = readFile("html5-ok.html");

		Jerry doc = jerry(html);
		doc.$("div").html("<b>Wow!</b> Such excitement...");
		doc.$("div b").append("!!!").css("color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testAdd() {
		String html = readFile("add.html");
		String htmlOK = readFile("add-ok.html");

		Jerry $ = jerry(html).$("div").css("border", "2px solid red")
				.add("p")
				.css("background", "yellow");

		assertEquals(htmlOK, actualHtml($));
	}

	public void testEnd() {
		String html = readFile("end.html");
		String htmlOK = readFile("end-ok.html");

		Jerry $ = jerry(html).$("p").find("span").end().css("border", "2px red solid");
		assertEquals(htmlOK, actualHtml($));
	}

	public void testNot() {
		String html = readFile("not.html");
		String htmlOK = readFile("not-ok.html");

		Jerry $ = jerry(html).$("div").not(".green, #blueone").css("border-color", "red");
		assertEquals(htmlOK, actualHtml($));
	}

	public void testFirst() {
		String html = readFile("first.html");
		String htmlOK = readFile("first-ok.html");

		Jerry $ = jerry(html).$("p span").first().addClass("highlight");
		assertEquals(htmlOK, actualHtml($));
	}

	public void testLast() {
		String html = readFile("last.html");
		String htmlOK = readFile("last-ok.html");

		Jerry $ = jerry(html).$("p span").last().addClass("highlight");
		assertEquals(htmlOK, actualHtml($));
	}

	public void testAddClass() {
		String html = readFile("addClass.html");
		String htmlOK = readFile("addClass-ok.html");

		Jerry $ = jerry(html).$("p:last").addClass("selected");
		assertEquals(htmlOK, actualHtml($));
	}

	public void testPseudoLast() {
		String html = readFile("pseudoLast.html");
		String htmlOK = readFile("pseudoLast-ok.html");

		Jerry $ = jerry(html).$("tr:last").css("background-color", "yellow", "font-weight", "bolder");
		assertEquals(htmlOK, actualHtml($));
	}

	public void testPseudoFirst() {
		String html = readFile("pseudoFirst.html");
		String htmlOK = readFile("pseudoFirst-ok.html");

		Jerry $ = jerry(html).$("tr:first").css("font-style", "italic");
		assertEquals(htmlOK, actualHtml($));
	}

	public void testPseudoButton() {
		String html = readFile("pseudoButton.html");
		String htmlOK = readFile("pseudoButton-ok.html");

		Jerry doc = jerry(html);
		Jerry input = doc.$(":button").css("background", "yellow", "border", "3px red solid");
		doc.$("div").text("For this type jQuery found " + input.length() + ".").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testPseudoCheckbox() {
		String html = readFile("pseudoCheckbox.html");
		String htmlOK = readFile("pseudoCheckbox-ok.html");

		Jerry doc = jerry(html);
		Jerry input = doc.$("form input:checkbox").wrap("<span></span>").parent().css("background", "yellow", "border", "3px red solid");
		doc.$("div").text("For this type jQuery found " + input.length() + ".").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testPseudoFile() {
		String html = readFile("pseudoFile.html");
		String htmlOK = readFile("pseudoFile-ok.html");

		Jerry doc = jerry(html);
		Jerry input = doc.$("form input:file").css("background", "yellow", "border", "3px red solid");
		doc.$("div").text("For this type jQuery found " + input.length() + ".").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

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

	public void testPseudoHeader() {
		String html = readFile("pseudoHeader.html");
		String htmlOK = readFile("pseudoHeader-ok.html");

		Jerry doc = jerry(html);
		doc.$(":header").css("background", "#ccc", "color", "blue");
		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testPseudoParent() {
		String html = readFile("pseudoParent.html");
		String htmlOK = readFile("pseudoParent-ok.html");

		Jerry doc = jerry(html);
		doc.$("td:parent").css("background", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

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

	public void testPseudoEq() {
		String html = readFile("pseudoEq.html");
		String htmlOK = readFile("pseudoEq-ok.html");

		Jerry doc = jerry(html);
		doc.$("td:eq(2)").css("color", "red");
		assertEquals(htmlOK, actualHtml(doc));
	}

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

		doc.$("ul.nav li:nth-child(2)").css("color","red");
		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testPseudoEven() {
		String html = readFile("pseudoEven.html");
		String htmlOK = readFile("pseudoEven-ok.html");

		Jerry doc = jerry(html);
		doc.$("tr:even").css("background-color", "#bbbbff");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testPseudoGt() {
		String html = readFile("pseudoGt.html");
		String htmlOK = readFile("pseudoGt-ok.html");

		Jerry doc = jerry(html);
		doc.$("td:gt(4)").css("text-decoration", "line-through");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testNext() {
		String html = readFile("next.html");
		String htmlOK = readFile("next-ok.html");

		Jerry doc = jerry(html);
		doc.$("button[disabled]").next().text("this button is disabled");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testEq() {
		String html = readFile("eq.html");
		String htmlOK = readFile("eq-ok.html");

		Jerry doc = jerry(html);
		doc.$("li").eq(1).css("background-color", "red");
		doc.$("li").eq(-1).css("background-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testSibling() {
		String html = readFile("sibling.html");
		String htmlOK = readFile("sibling-ok.html");

		Jerry doc = jerry(html);
		doc.$("li.third-item").siblings().css("background-color", "red");

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testSibling2() {
		String html = readFile("sibling2.html");
		String htmlOK = readFile("sibling2-ok.html");

		Jerry doc = jerry(html);
		int len = doc.$(".hilite").siblings().css("color", "red").length();
		doc.$("b").text(String.valueOf(len));

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testRemove() {
		String html = readFile("remove.html");
		String htmlOK = readFile("remove-ok.html");

		Jerry doc = jerry(html);
		doc.$("p").remove();

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testEmpty() {
		String html = readFile("empty.html");
		String htmlOK = readFile("empty-ok.html");

		Jerry doc = jerry(html);
		doc.$("p").empty();

		assertEquals(htmlOK, actualHtml(doc));
	}

	public void testBefore() {
		String html = readFile("before.html");
		String htmlOK = readFile("before-ok.html");

		Jerry doc = jerry(html);
		doc.$("p").before("<b>Hello</b>");

		assertEquals(htmlOK, actualHtml(doc));
	}

	// ---------------------------------------------------------------- tools

	private String actualHtml(Jerry $) {
		return $.root().get(0).getHtml();
	}

	private String readFile(String fileName) {
		try {
			return FileUtil.readString(new File(testDataRoot, fileName));
		} catch (IOException ioex) {
			return null;
		}
	}

}
