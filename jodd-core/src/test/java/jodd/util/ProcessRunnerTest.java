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

package jodd.util;

import jodd.util.ProcessRunner.ProcessResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests for class {@link ProcessRunner}.
 */
class ProcessRunnerTest {

	@Nested
	@DisplayName("tests for RuntimeUtil#run")
	class Run {

		private Process process = null;

		@Test
		void testRun_on_windows() throws Exception {

			assumeTrue(SystemUtil.info().isWindows(), "no windows host");

			process = new ProcessBuilder("cmd.exe", "/c", "dir").start();

			final ProcessResult processResult = ProcessRunner.run(process);

			// asserts
			doAsserts(processResult);
		}

		@Test
		void testRun_on_linux() throws Exception {

			assumeTrue(SystemUtil.info().isLinux(), "no linux host");

			process = new ProcessBuilder("bash", "-c", "ls").start();

			final ProcessResult processResult = ProcessRunner.run(process);

			// asserts
			doAsserts(processResult);
		}

		private void doAsserts(final ProcessResult processResult) {
			assertNotNull(processResult.getOutput());
			assertTrue(!processResult.getOutput().isEmpty());
			if (processResult.getExitCode() != 0) {
				System.err.println(processResult.getOutput()); // print error messages
			}
			assertEquals(0, processResult.getExitCode());
		}

		@AfterEach
		void afterEachTest() {

			if (process != null) {
				try {
					process.destroy();
				} catch (final Exception e) {
					// ignore
				}
			}

		}

	}
}
