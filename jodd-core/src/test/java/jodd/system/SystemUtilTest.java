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

package jodd.system;

import jodd.bean.BeanUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * tests for class {@link SystemUtil}
 */
class SystemUtilTest {

	@Test
	void testGet_with_unknown_key() throws Exception {
		final String key = "jodd_makes_fun";
		final String def = "yeah - really!";

		final String actual = SystemUtil.get(key, def);

		// asserts
		assertNotNull(actual);
		assertEquals(def, actual);
	}

	@Test
	void testJrePackages() throws Exception {
		final String[] actual = SystemUtil.info().getJrePackages();

		// asserts
		assertNotNull(actual);
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class SystemPropertiesCalls {
	
		@ParameterizedTest
		@MethodSource("testdata_testAgainstSystemProperty")
		void testAgainstSystemProperty(final String methodname, final String expected) {
			SystemInfo systemInfo = SystemUtil.info();

			final String actual = BeanUtil.declared.getProperty(systemInfo, methodname);

			// asserts
			assertNotNull(actual);
			assertEquals(expected, actual);
		}

		private Collection<Arguments> testdata_testAgainstSystemProperty() {

			final List<Arguments> params = new ArrayList<>();

			params.add(Arguments.of("workingDir", System.getProperty("user.dir")));
			params.add(Arguments.of("userName", System.getProperty("user.name")));
			params.add(Arguments.of("homeDir", System.getProperty("user.home")));
			params.add(Arguments.of("javaHomeDir", System.getProperty("java.home")));
			params.add(Arguments.of("tempDir", System.getProperty("java.io.tmpdir")));
			params.add(Arguments.of("osName", System.getProperty("os.name")));
			params.add(Arguments.of("osVersion", System.getProperty("os.version")));
			params.add(Arguments.of("javaVersion", System.getProperty("java.version")));
			params.add(Arguments.of("javaSpecificationVersion", System.getProperty("java.specification.version")));
			params.add(Arguments.of("javaVendor", System.getProperty("java.vendor")));
			//params.add(Arguments.of("systemClasspath", System.getProperty("java.class.path")));
			params.add(Arguments.of("pathSeparator", System.getProperty("path.separator")));
			params.add(Arguments.of("fileEncoding", System.getProperty("file.encoding")));

			return params;
		}

	}
}