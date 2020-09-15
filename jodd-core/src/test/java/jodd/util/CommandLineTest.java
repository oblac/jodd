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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link CommandLine}.
 */
class CommandLineTest {

	@Nested
	@DisplayName("tests for CommandLine#run on windows hosts")
	@EnabledOnOs(value = {OS.WINDOWS})
	class Run_on_windows {

		@Test
		void testRun() throws Exception {

			final ProcessResult processResult = new CommandLine("cmd.exe")
					.arg("/c")
					.args("dir")
					.workingDirectory(".")
					.err(System.err)
					.errPrefix("<error_prefix>")
					.out(System.out)
					.outPrefix("<output_prefix>")
					.newEnv(false)
					.env("jodd", "n1")
					.run();

			// asserts
			assertNotNull(processResult);
			assertEquals(CommandLine.OK, processResult.getExitCode());
			assertTrue(processResult.getOutput() != null);
			assertFalse(processResult.getOutput().isEmpty());
			assertTrue(processResult.getOutput().contains("<output_prefix>"));
			assertFalse(processResult.getOutput().contains("<error_prefix>"));
		}

		@Test
		void testRun_with_expected_process_error() throws Exception {

			final ProcessResult processResult = new CommandLine("cmd.exe")
					.arg("/c")
					.args("this_command_is_unknown")
					.workingDirectory(".")
					.err(System.err)
					.errPrefix("<error_prefix>")
					.out(System.out)
					.outPrefix("<output_prefix>")
					.newEnv(false)
					.env("jodd", "n1")
					.run();

			// asserts
			assertNotNull(processResult);
			assertNotEquals(CommandLine.OK, processResult.getExitCode());
			assertTrue(processResult.getOutput() != null);
			assertFalse(processResult.getOutput().isEmpty());
			assertFalse(processResult.getOutput().contains("<output_prefix>"));
			assertTrue(processResult.getOutput().contains("<error_prefix>"));
		}
	}


	@Nested
	@DisplayName("tests for CommandLine#run on linux hosts")
	@EnabledOnOs(value = {OS.AIX, OS.LINUX, OS.MAC, OS.SOLARIS})
	class Run_on_linux {

		@Test
		void testRun() throws Exception {

			final ProcessResult processResult = new CommandLine("bash")
					.arg("-c")
					.args("ls")
					.workingDirectory(".")
					.err(System.err)
					.errPrefix("<error_prefix>")
					.out(System.out)
					.outPrefix("<output_prefix>")
					.newEnv(false)
					.env("jodd", "n1")
					.run();

			// asserts
			assertNotNull(processResult);
			assertEquals(CommandLine.OK, processResult.getExitCode());
			assertTrue(processResult.getOutput() != null);
			assertFalse(processResult.getOutput().isEmpty());
			assertTrue(processResult.getOutput().contains("<output_prefix>"));
			assertFalse(processResult.getOutput().contains("<error_prefix>"));
		}

		@Test
		void testRun_with_expected_process_error() throws Exception {

			final ProcessResult processResult = new CommandLine("bash")
					.arg("-c")
					.args("this_command_is_unknown")
					.workingDirectory(".")
					.err(System.err)
					.errPrefix("<error_prefix>")
					.out(System.out)
					.outPrefix("<output_prefix>")
					.newEnv(false)
					.env("jodd", "n1")
					.run();

			// asserts
			assertNotNull(processResult);
			assertNotEquals(CommandLine.OK, processResult.getExitCode());
			assertTrue(processResult.getOutput() != null);
			assertFalse(processResult.getOutput().isEmpty());
			assertFalse(processResult.getOutput().contains("<output_prefix>"));
			assertTrue(processResult.getOutput().contains("<error_prefix>"));
		}
	}

}
