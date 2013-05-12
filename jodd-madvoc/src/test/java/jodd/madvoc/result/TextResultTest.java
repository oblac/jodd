//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.result;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.madvoc.MadvocWinstoneServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TextResultTest {

	static MadvocWinstoneServer winstoneServer;

	@BeforeClass
	public static void startServer() throws IOException {
		winstoneServer = new MadvocWinstoneServer();
		winstoneServer.start();
	}

	@AfterClass
	public static void stopServer() {
		winstoneServer.stop();
	}

	@Test
	public void testEncoding() {
		HttpResponse httpResponse = HttpRequest.get("localhost:8080/textResultEncoding").send();
		assertEquals(200, httpResponse.statusCode());
		assertEquals("this text contents chinese chars 中文", httpResponse.bodyText());
	}

	public String madvocEncoding() {
		return "text:this text contents chinese chars 中文";
	}


	// ---------------------------------------------------------------- configurator

	public void configure() {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}