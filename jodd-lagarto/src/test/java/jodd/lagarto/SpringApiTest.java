// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.FileUtil;
import jodd.jerry.Jerry;
import jodd.jerry.JerryFunction;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

public class SpringApiTest {

	protected String testDataRoot;

	@Before
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = LagartoParserTest.class.getResource("data");
		testDataRoot = data.getFile();
	}

	@Test
	public void testPortletUtils() throws IOException {
		File file = new File(testDataRoot, "PortletUtils.html");
		String content = FileUtil.readString(file);

		Jerry.JerryParser jerryParser = new Jerry.JerryParser();
		//jerryParser.getDOMBuilder().setCalculatePosition(true);

		Jerry doc = jerryParser.parse(content);

		// parse
		doc.$("a").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				assertEquals("<a name=\"navbar_top\"><!-- --></a>", $this.get()[0].getHtml());
				return false;
			}
		});
	}

	@Test
	public void testAbstractFormController() throws IOException {
		File file = new File(testDataRoot, "AbstractFormController.html");
		String content = FileUtil.readString(file);

		Jerry.JerryParser jerryParser = new Jerry.JerryParser();
		//jerryParser.getDOMBuilder().setCalculatePosition(true);

		Jerry doc = jerryParser.parse(content);

		// parse
		doc.$("a").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				assertEquals("<a name=\"navbar_top\"><!-- --></a>", $this.get()[0].getHtml());
				return false;
			}
		});
	}

}
