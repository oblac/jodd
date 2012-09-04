// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.StreamUtil;
import jodd.io.StringOutputStream;
import jodd.lagarto.dom.jerry.Jerry;
import jodd.lagarto.dom.jerry.JerryFunction;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class StuckTest extends TestCase {

	protected String testDataRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (testDataRoot != null) {
			return;
		}
		URL data = LagartoParserTest.class.getResource("data");
		testDataRoot = data.getFile();
	}

	public void testStuck() throws IOException {
		File file = new File(testDataRoot, "stuck.html.gz");
		InputStream in = new GZIPInputStream(new FileInputStream(file));
		StringOutputStream out = new StringOutputStream("UTF-8");
		StreamUtil.copy(in, out);

		Jerry doc = Jerry.jerry(out.toString());

		// parse
		try {
			doc.$("a").each(new JerryFunction() {
				public boolean onNode(Jerry $this, int index) {
					System.out.println("-----");
					System.out.println($this.html());
					return false;
				}
			});
		} catch (StackOverflowError stackOverflowError) {
			fail("stack overflow!");
		}
	}
}
