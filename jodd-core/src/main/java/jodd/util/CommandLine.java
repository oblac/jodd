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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jodd.util.ProcessRunner.ERROR_PREFIX;
import static jodd.util.ProcessRunner.OUTPUT_PREFIX;

/**
 * Simple user-friendly wrapper over {@code ProcessBuilder}. Has the following:
 * <ul>
 *     <li>fluent interface</li>
 *     <li>no exception is throw</li>
 *     <li>output is collected to string</li>
 *     <li>easy environment manipulation</li>
 * </ul>
 */
public class CommandLine {

	public static final int OK = 0;

	protected final List<String> cmdLine = new ArrayList<>();
	protected Map<String, String> env = null;
	protected boolean cleanEnvironment = false;
	protected File workingDirectory;
	protected String outPrefix = OUTPUT_PREFIX;
	protected String errPrefix = ERROR_PREFIX;
	protected OutputStream out = System.out;
	protected OutputStream err = System.err;

	// ---------------------------------------------------------------- ctor

	protected CommandLine(final String command) {
		cmdLine.add(command);
	}

	/**
	 * Creates command line with given command.
	 */
	public static CommandLine cmd(final String command) {
		return new CommandLine(command);
	}

	// ---------------------------------------------------------------- arguments

	/**
	 * Defines working directory.
	 */
	public CommandLine workingDirectory(final File workDirectory) {
		this.workingDirectory = workDirectory;

		return this;
	}

	/**
	 * Defines working directory.
	 */
	public CommandLine workingDirectory(final String workDirectory) {
		this.workingDirectory = new File(workDirectory);

		return this;
	}

	/**
	 * Adds single argument.
	 */
	public CommandLine arg(final String argument) {
		cmdLine.add(argument);

		return this;
	}

	/**
	 * Adds several arguments.
	 */
	public CommandLine args(final String... arguments) {
		if (arguments != null && arguments.length > 0) {
			Collections.addAll(cmdLine, arguments);
		}

		return this;
	}

	/**
	 * Defines output prefix.
	 */
	public CommandLine outPrefix(final String prefix) {
		this.outPrefix = prefix;
		return this;
	}

	/**
	 * Defines error prefix.
	 */
	public CommandLine errPrefix(final String prefix) {
		this.errPrefix = prefix;
		return this;
	}

	public CommandLine out(final OutputStream out) {
		this.out = out;
		return this;
	}

	public CommandLine err(final OutputStream err) {
		this.err = err;
		return this;
	}

	/**
	 * Sets environment variable.
	 */
	public CommandLine env(final String key, final String value) {
		if (env == null) {
			env = new HashMap<>();
		}
		env.put(key, value);
		return this;
	}

	/**
	 * When set to {@code true}, environment will not be copied from the
	 * parent process and will be completly empty.
	 */
	public CommandLine newEnv(final boolean clean) {
		cleanEnvironment = clean;
		return this;
	}

	// ---------------------------------------------------------------- execute

	/**
	 * Runs command and returns process result.
	 */
	public ProcessRunner.ProcessResult run() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		out = err = baos;

		try {
			baos.write(StringUtil.join(cmdLine, ' ').getBytes());
			baos.write(StringPool.BYTES_NEW_LINE);
		}
		catch (IOException ignore) {
		}

		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.command(cmdLine);

		if (cleanEnvironment) {
			processBuilder.environment().clear();
		}
		if (env != null) {
			processBuilder.environment().putAll(env);
		}

		processBuilder.directory(workingDirectory);

		Process process = null;
		try {
			process = processBuilder.start();
		}
		catch (IOException ioex) {
			return writeException(baos, ioex);
		}

		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), out, outPrefix);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), err, errPrefix);

		outputGobbler.start();
		errorGobbler.start();

		int result;

		try {
			result = process.waitFor();
		}
		catch (InterruptedException iex) {
			return writeException(baos, iex);
		}

		outputGobbler.waitFor();
		errorGobbler.waitFor();

		return new ProcessRunner.ProcessResult(result, baos.toString());
	}

	private ProcessRunner.ProcessResult writeException(final ByteArrayOutputStream baos, final Exception ex) {
		try {
			baos.write(errPrefix.getBytes());
		}
		catch (IOException ignore) {
		}

		ex.printStackTrace(new PrintStream(baos));

		return new ProcessRunner.ProcessResult(-1, baos.toString());
	}
}
