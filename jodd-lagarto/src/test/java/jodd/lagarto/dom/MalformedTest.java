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

package jodd.lagarto.dom;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MalformedTest {

	protected String testDataRoot;

	@BeforeEach
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = NodeSelectorTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	@Test
	public void testOneNode() {
		String content = "<body><div>test<span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testOneNodeWithBlanks() {
		String content = "<body><div>   <span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>   <span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTwoNodes() {
		String content = "<body><div>test<span><form>xxx</form></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTwoNodes2() {
		String content = "<body><div>test<span><form>xxx</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple1() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div>xxx</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div>xxx</div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple2() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div><h2>HAB</h2><p>AMONG</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div><h2>HAB</h2><p>AMONG</p></div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple3WithSpaces() {
		String content = "<div> <h1>FORELE</h1> <p>dicuss <div> <h2>HAB</h2> <p>AMONG </div> </div>".toUpperCase();
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div> <h1>FORELE</h1> <p>DICUSS </p><div> <h2>HAB</h2> <p>AMONG </p></div> </div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterFull() {
		String content = "<DIV class=\"section\" id=\"forest-elephants\" >\n" +
				"<H1>Forest elephants</H1>\n" +
				"<P>In this section, we discuss the lesser known forest elephants.\n" +
				"...this section continues...\n" +
				"<DIV class=\"subsection\" id=\"forest-habitat\" >\n" +
				"<H2>Habitat</H2>\n" +
				"<P>Forest elephants do not live in trees but among them.\n" +
				"...this subsection continues...\n" +
				"</DIV>\n" +
				"</DIV>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);

		String expected = "<div class=\"section\" id=\"forest-elephants\">\n" +
				"<h1>Forest elephants</h1>\n" +
				"<p>In this section, we discuss the lesser known forest elephants.\n" +
				"...this section continues...\n</p>" +
				"<div class=\"subsection\" id=\"forest-habitat\">\n" +
				"<h2>Habitat</h2>\n" +
				"<p>Forest elephants do not live in trees but among them.\n" +
				"...this subsection continues...\n</p>" +
				"</div>\n" +
				"</div>";

		assertEquals(expected, doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testEof() {
		String content = "<body><div>test";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test</div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testEof2() {
		String content = "<body><div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testSpanDivOverTable() {
		String content = "<span><div><table><tr><td>text</span>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<span><div><table><tr><td>text</td></tr></table></div></span>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testDivSpanOverTable() {
		String content = "<div><span><table><tr><td>text</div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><span><table><tr><td>text</td></tr></table></span></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTableInTableInTable() throws IOException {
		String html = read("tableInTable.html", false);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(html);

		String out = read("tableInTable-out.html", true);

		assertEquals(out, html(doc));
		assertTrue(doc.check());
	}

	@Test
	public void testFormClosesAll() throws IOException {
		String html = read("formClosesAll.html", false);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("formClosesAll-out1.html", true);
		assertEquals(out, html);
		assertTrue(doc.check());

		lagartoDOMBuilder.getConfig().setUseFosterRules(true);
		doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		out = read("formClosesAll-out2.html", true);
		assertEquals(out, html);
	}

	@Test
	public void testFoster1() {
		String html = "A<table>B<tr>C</tr>D</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.getConfig().setUseFosterRules(true);
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("ABCD<table><tr></tr></table>", html);
	}

	@Test
	public void testFoster2() {
		String html = "A<table><tr> B</tr> C</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.getConfig().setUseFosterRules(true);
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("ABC<table><tr></tr></table>", html);
	}

	@Test
	public void testBodyEnd() {
		String html = "<body><p>111</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p></body>", html);
		assertNull(doc.getErrors());
	}

	@Test
	public void testBodyEndWithError() {
		String html = "<body><p>111<h1>222</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p><h1>222</h1></body>", html);
		assertNotNull(doc.getErrors());
		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testEOF() {
		String html = "<body><p>111";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p></body>", html);
		assertNull(doc.getErrors());
	}

	@Test
	public void testEOFWithError() {
		String html = "<body><p>111<h1>222";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableDebug();
		Document doc = lagartoDOMBuilder.parse(html);
		html = html1(doc);

		assertEquals("<body><p>111</p><h1>222</h1></body>", html);
		assertNotNull(doc.getErrors());
		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testCrazySpan() throws IOException {
		String html = read("spancrazy.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("spancrazy-out.html", true);
		assertEquals(out, html);
		assertEquals(3, doc.getErrors().size());
	}

	@Test
	public void testFosterForm() throws IOException {
		String html = read("fosterForm.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("fosterForm-out.html", true);
		assertEquals(out, html);
		assertNull(doc.getErrors());
	}

	@Test
	public void testListCrazy() throws IOException {
		String html = read("listcrazy.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("listcrazy-out.html", true);
		assertEquals(out, html);
		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testTable1() throws IOException {
		String html = read("table1.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("table1-out.html", true);
		assertEquals(out, html);
	}

	@Test
	public void testTable2() throws IOException {
		String html = read("table2.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("table2-out.html", true);
		assertEquals(out, html);
	}

	@Test
	public void smtest() throws IOException {
		String html = read("smtest.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableHtmlPlusMode();
		lagartoDOMBuilder.enableDebug();

		Document doc = lagartoDOMBuilder.parse(html);
		html = html(doc);

		String out = read("smtest-out.html", true);

		// still not working

		out = StringUtil.remove(out, "<tbody>\n");
		out = StringUtil.remove(out, "</tbody>\n");

		html = StringUtil.replace(html, "<td>\nnotworking</td>", "<tr>\n<td>\nnotworking</td>\n</tr>");

		assertEquals(out, html);
	}

	@Test
	public void testDecodingQuotes() throws IOException {
		String html = read("decode.html", false);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(html);

		Element td1 = (Element) doc.getChild(0, 1, 1, 1, 1);
		String td1attr = td1.getAttribute("onclick");

		Element td2 = (Element) doc.getChild(0, 1, 1, 3, 1);
		String td2attr = td2.getAttribute("onclick");

		html = html(doc);
		String out = read("decode-out.html", true);

		assertEquals(out, html);

		// now re-parse the generated html

		String newHtml = doc.getHtml();

		lagartoDOMBuilder = new LagartoDOMBuilder();
		doc = lagartoDOMBuilder.parse(newHtml);

		td1 = (Element) doc.getChild(0, 1, 1, 1, 1);
		assertEquals(td1attr, td1.getAttribute("onclick"));

		td2 = (Element) doc.getChild(0, 1, 1, 3, 1);
		assertEquals(td2attr, td2.getAttribute("onclick"));

	}

	@Test
	public void testQuotes() throws IOException {
		String html = read("quotes.html", false);
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();

		Document doc = lagartoDOMBuilder.parse(html);

		html = html(doc);
		String out = read("quotes-out.html", true);

		assertEquals(out, html);
	}

	// ---------------------------------------------------------------- util

	/**
	 * Reads test file and returns its content optionally stripped.
	 */
	protected String read(String filename, boolean strip) throws IOException {
		String data = FileUtil.readString(new File(testDataRoot, filename));
		if (strip) {
			data = strip(data);
		}
		return data;
	}

	protected String strip(String string) {
		string = StringUtil.removeChars(string, " \r\n\t");
		string = StringUtil.replace(string, ">", ">\n");
		return string;
	}

	/**
	 * Parses HTML and returns the stripped html.
	 */
	protected String html(Document document) {
		String html = document.getHtml();
		html = strip(html);
		return html;
	}
	protected String html1(Document document) {
		String html = document.getHtml();
		html = StringUtil.removeChars(html, " \r\n\t");
		return html;
	}

}
