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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.StreamGobbler;
import jodd.io.StreamUtil;

import static jodd.util.RuntimeUtil.ERROR_PREFIX;
import static jodd.util.RuntimeUtil.OUTPUT_PREFIX;

/**
 * Simple tool to easily execute native applications.
 *
 * @author Vilmos Papp
 * @author Igor Spasić
 */
public class CommandLine {

	public static final int OK = 0;

	protected final String command;
	protected final List<String> args = new ArrayList<>();
	protected Map<String, String> env = null;
	protected File workingDirectory;
	protected String outPrefix = OUTPUT_PREFIX;
	protected String errPrefix = ERROR_PREFIX;
	protected OutputStream out = System.out;
	protected OutputStream err = System.err;
	protected boolean newShell = false;

	// ---------------------------------------------------------------- ctor

	protected CommandLine(String command) {
		this.command = command;
	}

	/**
	 * Creates command line with given command.
	 */
	public static CommandLine cmd(String command) {
		return new CommandLine(command);
	}

	// ---------------------------------------------------------------- arguments

	/**
	 * Defines working directory.
	 */
	public CommandLine workingDirectory(File workDirectory) {
		this.workingDirectory = workDirectory;

		return this;
	}

	/**
	 * Defines working directory.
	 */
	public CommandLine workingDirectory(String workDirectory) {
		this.workingDirectory = new File(workDirectory);

		return this;
	}

	/**
	 * Adds single argument.
	 */
	public CommandLine arg(String argument) {
		args.add(argument);

		return this;
	}

	/**
	 * Adds several arguments.
	 */
	public CommandLine args(String... arguments) {
		if (arguments != null && arguments.length > 0) {
			Collections.addAll(args, arguments);
		}

		return this;
	}

	/**
	 * Defines output prefix.
	 */
	public CommandLine outPrefix(String prefix) {
		this.outPrefix = prefix;
		return this;
	}

	/**
	 * Defines error prefix.
	 */
	public CommandLine errPrefix(String prefix) {
		this.errPrefix = prefix;
		return this;
	}

	public CommandLine out(OutputStream out) {
		this.out = out;
		return this;
	}

	public CommandLine err(OutputStream err) {
		this.err = err;
		return this;
	}

	/**
	 * Sets environment variable.
	 */
	public CommandLine env(String key, String value) {
		if (env == null) {
			env = new HashMap<>();
		}
		env.put(key, value);
		return this;
	}

	public CommandLine newShell(boolean newShell) {
		this.newShell = newShell;
		return this;
	}

	// ---------------------------------------------------------------- executor

	/**
	 * Resolves system-dependent executor.
	 */
	protected List<String> resolveShellExecutor(String command) {
		List<String> newCommand = new ArrayList<>();

		File commandFile = new File(command);

		if (SystemUtil.isHostMac()) {
			if (isSH(command)) {
				newCommand.add("sh");
			}
			else if (commandFile.canExecute() && !FileNameUtil.hasExtension(commandFile.getAbsolutePath())) {
			}
			else if (FileUtil.isExistingFile(commandFile)) { // for native application and files with associated applications, open command should be used
				newCommand.add("open");
			}
			else {
				newCommand.add("sh");
				newCommand.add("-c");
			}
		}
		else if (SystemUtil.isHostAix() || SystemUtil.isHostLinux() || SystemUtil.isHostSolaris() || SystemUtil.isHostUnix()) {
			String shebang = getShebang(commandFile);

			newCommand.add(shebang);

			if (shebang.equals("sh")) {
				newCommand.add("-c");
			}
		}
		else if (SystemUtil.isHostWindows()) {
			newCommand.add("cmd");
			newCommand.add("/c");
		}

		newCommand.add(command);

		return newCommand;
	}

	protected String getShebang(File commandFile) {
		String shebang;

		if (commandFile.exists() && !commandFile.isDirectory() && commandFile.length() > 0) {

			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(commandFile));
				shebang = reader.readLine();
				shebang = shebang.substring(shebang.indexOf('/'));
			}
			catch (Exception e) {
				shebang = "sh";
			}
			finally {
				StreamUtil.close(reader);
			}
		}

		return shebang;
	}

	protected boolean isSH(String command) {
		return FileNameUtil.getExtension(command).equals("sh");
	}

	// ---------------------------------------------------------------- execute

	protected List<String> prepareCommands() {
		List<String> commands = new ArrayList<>(args.size() + 1);

		if (newShell) {
			commands.addAll(resolveShellExecutor(command));
		}
		else {
			commands.add(command);
		}
		commands.addAll(args);

		return commands;
	}

	/**
	 * Executes command and returns process result.
	 */
	public RuntimeUtil.ProcessResult execute() throws IOException, InterruptedException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		out = err = baos;

		List<String> commands = prepareCommands();

		baos.write(StringUtil.join(commands, ' ').getBytes());
		baos.write(StringPool.BYTES_NEW_LINE);

		int exitCode = execute(commands);

		return new RuntimeUtil.ProcessResult(exitCode, baos.toString());
	}

	private int execute(List<String> commands) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.command(commands);

		if (env != null) {
			processBuilder.environment().putAll(env);
		}

		processBuilder.directory(workingDirectory);

		Process process = processBuilder.start();

		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), out, outPrefix);
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), err, errPrefix);

		outputGobbler.start();
		errorGobbler.start();

		int result = process.waitFor();

		outputGobbler.waitFor();
		errorGobbler.waitFor();

		return result;
	}
}
