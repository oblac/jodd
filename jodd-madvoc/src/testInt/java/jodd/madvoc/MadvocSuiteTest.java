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

package jodd.madvoc;

import jodd.exception.UncheckedException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;

public class MadvocSuiteTest {

	/**
	 * Starts Tomcat after the suite.
	 */
	@BeforeAll
	public static void beforeClass() {
		isSuite = true;
		startTomcat();
	}

	/**
	 * Stop Tomcat after the suite.
	 */
	@AfterAll
	public static void afterSuite() {
		isSuite = false;
		stopTomcat();
	}


	public static boolean isSuite;

	// ---------------------------------------------------------------- tomcat

	protected static TomcatTestServer server;

	/**
	 * Starts Tomcat.
	 */
	protected static void startTomcat(String webXmlFileName) {
		if (server != null) {
			return;
		}
		server = new TomcatTestServer(webXmlFileName);
		try {
			server.start();
			System.out.println("Tomcat test server started");
		} catch (Exception e) {
			throw new UncheckedException(e);
		}
	}

	/**
	 * Stops Tomcat if not in the suite.
	 */
	public static void stopTomcat() {
		if (server == null) {
			return;
		}
		if (isSuite) {	// don't stop tomcat if it we are still running in the suite!
			return;
		}
		try {
			server.stop();
		} catch (Exception ignore) {
		} finally {
			System.out.println("Tomcat test server stopped");
			server = null;
		}
	}

	public static void startTomcat() {
		startTomcat("web-test-int.xml");
	}

	// ---------------------------------------------------------------- go

	@Nested
	class HelloActionTest extends HelloActionTestBase {}
	@Nested
	class SimpleTest extends SimpleTestBase {}
	@Nested
	class RawActionTest extends RawActionTestBase {}
	@Nested
	class UrlActionTest extends UrlActionTestBase {}
	@Nested
	class OneTwoActionTest extends OneTwoActionTestBase {}
	@Nested
	class IntcptActionTest extends IntcptActionTestBase {}
	@Nested
	class RestActionTest extends RestActionTestBase {}
	@Nested
	class FilterTest extends FilterTestBase {}
	@Nested
	class SessionScopeTest extends SessionScopeTestBase {}
	@Nested
	class AlphaTest extends AlphaTestBase {}
	@Nested
	class ArgsTest extends ArgsTestBase {}
	@Nested
	class TypesTest extends TypesTestBase {}
	@Nested
	class ExcTest extends ExcTestBase {}
	@Nested
	class UserActionTest extends UserActionTestBase {}
	@Nested
	class AsyncTest extends AsyncTestBase {}
	@Nested
	class MoveTest extends MoveTestBase {}
	@Nested
	class BookActionTest extends BookActionTestBase {}
	@Nested
	class ResultsTest extends ResultsTestBase {}
	@Nested
	class TagActionTest extends TagActionTestBase {}
	@Nested
	class MissingActionTest extends MissingActionTestBase {}
}