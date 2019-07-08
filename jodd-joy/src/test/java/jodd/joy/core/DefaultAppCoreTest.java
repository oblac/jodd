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

package jodd.joy.core;

class DefaultAppCoreTest {

//	@Test
//	void testAppPropsNameAndPattern() {
//		AppCore appCore = new AppCore();
//
//		appCore.initCore();
//
//		assertEquals("app.props", appCore.appPropsName);
//		assertEquals("/app*.prop*", appCore.appPropsNamePattern);
//
//		assertEquals("core", AppCore.PETITE_CORE);
//
//		appCore.initLogger();
//		appCore.initProps();
//		appCore.initScanner();
//		appCore.startPetite();
//		PetiteContainer pc = appCore.petite;
//
//		JoyScanner as = pc.getBean(AppCore.PETITE_SCAN);
//
//		assertSame(appCore.appScanner, as);
//
//		assertTrue(as.ignoreExceptions);
//		assertEquals(3, as.includedEntries.length);
//		assertEquals("jodd.*", as.includedEntries[0]);
//		assertEquals("foo.*", as.includedEntries[1]);
//		assertEquals("bar.*", as.includedEntries[2]);
//
//		assertEquals(1, as.includedJars.length);
//		assertEquals("xxx", as.includedJars[0]);
//	}
//
//	public static class AppCore extends DefaultAppCore {
//		public AppCore() {
//			appDir = "";
//		}
//
//		@Override
//		protected void initProps() {
//			appProps = new Props();
//
//			appProps.setValue("scan.ignoreExceptions", "true");
//			appProps.setValue("scan.includedEntries", "jodd.*,foo.*,bar.*");
//			appProps.setValue("scan.includedJars", "xxx");
//		}
//	}
}
