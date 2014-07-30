// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.StreamUtil;
import jodd.jerry.Jerry;
import jodd.jerry.JerryFunction;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StuckTest {

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
	public void testStuck() throws IOException {
		File file = new File(testDataRoot, "stuck.html.gz");
		InputStream in = new GZIPInputStream(new FileInputStream(file));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamUtil.copy(in, out);
		in.close();

		Jerry.JerryParser jerryParser = new Jerry.JerryParser();
//		LagartoDOMBuilder lagartoDOMBuilder = (LagartoDOMBuilder) jerryParser.getDOMBuilder();
//		lagartoDOMBuilder.setParsingErrorLogLevelName("ERROR");
		Jerry doc = jerryParser.parse(out.toString("UTF-8"));

		// parse
		try {
			doc.$("a").each(new JerryFunction() {
				public boolean onNode(Jerry $this, int index) {
					assertEquals("Go to Database Directory", $this.html().trim());
					return false;
				}
			});
		} catch (StackOverflowError stackOverflowError) {
			fail("stack overflow!");
		}
	}
}
