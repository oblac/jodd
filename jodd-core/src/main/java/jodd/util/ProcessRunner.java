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

import jodd.io.StreamGobbler;

import java.io.ByteArrayOutputStream;

/**
 * Runtime utilities.
 */
public class ProcessRunner {

	public static final String ERROR_PREFIX = "err> ";
	public static final String OUTPUT_PREFIX = "out> ";

	public static class ProcessResult {

		private final int exitCode;
		private final String output;

		protected ProcessResult(final int existCode, final String output) {
			this.exitCode = existCode;
			this.output = output;
		}

		/**
		 * Returns process exit code.
		 */
		public int getExitCode() {
			return exitCode;
		}

		/**
		 * Returns process output.
		 */
		public String getOutput() {
			return output;
		}
	}

	/**
	 * Executes a process and returns the process output and exit code.
	 */
	public static ProcessResult run(final Process process) throws InterruptedException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		final StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), baos, OUTPUT_PREFIX);
		final StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), baos, ERROR_PREFIX);

		outputGobbler.start();
		errorGobbler.start();

		final int result = process.waitFor();

		outputGobbler.waitFor();
		errorGobbler.waitFor();

		return new ProcessResult(result, baos.toString());
	}


}
