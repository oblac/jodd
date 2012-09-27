package jodd.lagarto;

import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.io.StringOutputStream;
import jodd.jerry.Jerry;
import jodd.jerry.JerryFunction;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SpringApiTest extends TestCase {

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

	public void testPortletUtils() throws IOException {
		File file = new File(testDataRoot, "PortletUtils.html");
		String content = FileUtil.readString(file);

		Jerry.JerryParser jerryParser = new Jerry.JerryParser();
		//jerryParser.getDOMBuilder().setCalculatePosition(true);

		Jerry doc = jerryParser.parse(content);

		// parse
		doc.$("a").each(new JerryFunction() {
			@Override
			public boolean onNode(Jerry $this, int index) {
				assertEquals("<a name=\"navbar_top\"><!-- --></a>", $this.get()[0].getHtml());
				return false;
			}
		});
	}

	public void testAbstractFormController() throws IOException {
		File file = new File(testDataRoot, "AbstractFormController.html");
		String content = FileUtil.readString(file);

		Jerry.JerryParser jerryParser = new Jerry.JerryParser();
		//jerryParser.getDOMBuilder().setCalculatePosition(true);

		Jerry doc = jerryParser.parse(content);

		// parse
		doc.$("a").each(new JerryFunction() {
			@Override
			public boolean onNode(Jerry $this, int index) {
				assertEquals("<a name=\"navbar_top\"><!-- --></a>", $this.get()[0].getHtml());
				return false;
			}
		});
	}

}
