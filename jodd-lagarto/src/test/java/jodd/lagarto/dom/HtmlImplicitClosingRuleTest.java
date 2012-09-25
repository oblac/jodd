// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HtmlImplicitClosingRuleTest {

	@Test
	public void testTagP() {
		String content = "<body><p>para #1<p> para <b>#2</b></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><p>para #1</p><p> para <b>#2</b></p></body>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagPNoImpl() {
		String content = "<body><p>para #1<p> para <b>#2</b></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		lagartoDOMBuilder.setImpliedEndTags(false);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><p>para #1</p><p> para <b>#2</b></p></body>", doc.getHtml());
		assertEquals(2, lagartoDOMBuilder.getErrors().size());
	}

	@Test
	public void testTagDL() {
		String content = "<body><dl><dt>item #1<dd>desc #1<dt>item #2<dd>dec #2</dl></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><dl><dt>item #1</dt><dd>desc #1</dd><dt>item #2</dt><dd>dec #2</dd></dl></body>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagUL() {
		String content = "<body><ul>" +
				"<li>item1" +
				"<ul>" +
				"<li>item2" +
				"</ul>" +
				"</ul></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><ul><li>item1<ul><li>item2</li></ul></li></ul></body>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagOL() {
		String content = "<body><ol>" +
				"<li>item1" +
				"<ol>" +
				"<li>item2" +
				"</ol>" +
				"</ol></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><ol><li>item1<ol><li>item2</li></ol></li></ol></body>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagTRTD() {
		String content = "<table><tr><td>cell #1<td>cell #2<tr><td>cell #3<td>cell #4</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><tr><td>cell #1</td><td>cell #2</td></tr><tr><td>cell #3</td><td>cell #4</td></tr></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagTH() {
		String content = "<table><tr><th>cell #1<th>cell #2<tr><td>cell #3<td>cell #4</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><tr><th>cell #1</th><th>cell #2</th></tr><tr><td>cell #3</td><td>cell #4</td></tr></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagTBODY() {
		String content = "<table><tbody><tr><td>cell #1<td>cell #2<tr><td>cell #3<td>cell #4</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><tbody><tr><td>cell #1</td><td>cell #2</td></tr><tr><td>cell #3</td><td>cell #4</td></tr></tbody></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagTHEAD() {
		String content = "<table><thead><tr><td>col#1<td>col #2<tbody><tr><td>cell #1<td>cell #2<tr><td>cell #3<td>cell #4</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><thead><tr><td>col#1</td><td>col #2</td></tr></thead><tbody><tr><td>cell #1</td><td>cell #2</td></tr><tr><td>cell #3</td><td>cell #4</td></tr></tbody></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagTFOOT() {
		String content = "<table><tbody><tr><td>cell #1<td>cell #2<tr><td>cell #3<td>cell #4<tfoot><tr><td>sum #1<td>sum #2</table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><tbody><tr><td>cell #1</td><td>cell #2</td></tr><tr><td>cell #3</td><td>cell #4</td></tr></tbody><tfoot><tr><td>sum #1</td><td>sum #2</td></tr></tfoot></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagCOLGROUP1() {
		String content = "<table><colgroup><colgroup><thead></table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><colgroup></colgroup><colgroup></colgroup><thead></thead></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagCOLGROUP2() {
		String content = "<table><colgroup><colgroup><tbody></table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><colgroup></colgroup><colgroup></colgroup><tbody></tbody></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagCOLGROUP3() {
		String content = "<table><colgroup><col></colgroup><colgroup><tr><td></table>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<table><colgroup><col></colgroup><colgroup></colgroup><tr><td></td></tr></table>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testHEAD() {
		String content = "<html><head>head<body>body</html>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<html><head>head</head><body>body</body></html>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testBODY() {
		String content = "<html><body>body";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<html><body>body</body></html>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}

	@Test
	public void testTagOPTGROUP() {
		String content = "<form><select><optgroup><option>option#1</option><optgroup><option>option#2</option></select></form>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<form><select><optgroup><option>option#1</option></optgroup><optgroup><option>option#2</option></optgroup></select></form>", doc.getHtml());
		assertNull(lagartoDOMBuilder.getErrors());
	}


}
