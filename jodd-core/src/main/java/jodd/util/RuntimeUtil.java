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

import jodd.Jodd;
import jodd.io.StreamGobbler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Runtime utilities.
 */
public class RuntimeUtil {

	public static final String ERROR_PREFIX = "err> ";
	public static final String OUTPUT_PREFIX = "out> ";

	// ---------------------------------------------------------------- memory

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory) in bytes.
	 */
	public static long availableMemory() {
		return Runtime.getRuntime().freeMemory() + (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory());
	}

	/**
	 * Returns the amount of available memory in percents.
	 */
	public static float availableMemoryPercent() {
		return availableMemory() * 100.0f / Runtime.getRuntime().maxMemory();
	}

	/**
	 * Compacts memory as much as possible by allocating huge memory block
	 * and then forcing garbage collection.
	 */
	public static void compactMemory() {
		try {
			final byte[][] unused = new byte[128][];
			for(int i = unused.length; i-- != 0;) {
				unused[i] = new byte[2000000000];
			}
		} catch(OutOfMemoryError ignore) {
		}
		System.gc();
	}

	// ---------------------------------------------------------------- location

	/**
	 * Returns location of the class. If class is not in a jar, it's classpath
	 * is returned; otherwise the jar location.
	 */
	public static String classLocation(Class clazz) {
		return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
	}

	/**
	 * Returns Jodd {@link #classLocation(Class) location}.
	 * @see #classLocation
	 */
	public static String joddLocation() {
		return classLocation(Jodd.class);
	}

	// ---------------------------------------------------------------- process

	public static class ProcessResult {
		private final int exitCode;
		private final String output;

		protected ProcessResult(int existCode, String output) {
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
	public static ProcessResult run(Process process) throws IOException, InterruptedException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), baos, OUTPUT_PREFIX);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), baos, ERROR_PREFIX);

		outputGobbler.start();
		errorGobbler.start();

		int result = process.waitFor();

		outputGobbler.waitFor();
		errorGobbler.waitFor();

		return new ProcessResult(result, baos.toString());
	}


}
