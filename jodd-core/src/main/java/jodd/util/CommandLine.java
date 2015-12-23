package jodd.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.StreamGobbler;

/**
 * Simple tool to easily execute native applications.
 *
 * @author Vilmos Papp
 * @author Igor Spasić
 */
public class CommandLine {

	public static final String ERROR_PREFIX = "err> ";
	public static final String OUTPUT_PREFIX = "out> ";

	public static final int OK = 0;

	protected final String command;
	protected final List<String> args = new ArrayList<>();
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

	public CommandLine newShell(boolean newShell) {
		this.newShell = newShell;
		return this;
	}

	// ---------------------------------------------------------------- executor

	/**
	 * Resolves system-dependent executor.
	 */
	protected String resolveExecutor(String command) {
		String newCommand = StringPool.EMPTY;

		File commandFile = new File(command);

		if (SystemUtil.isHostMac()) {
			if (isSH(command)) {
				newCommand += "sh ";
			}
			else if (commandFile.canExecute() && !FileNameUtil.hasExtension(commandFile.getAbsolutePath())) {
			}
			else if (FileUtil.isExistingFile(commandFile)) { // for native application and files with associated applications, open command should be used
				newCommand += "open ";
			}
			else {
				newCommand += "sh -c ";
			}
		}
		else if (SystemUtil.isHostAix() || SystemUtil.isHostLinux() || SystemUtil.isHostSolaris() || SystemUtil.isHostUnix()) {
			newCommand += "sh -c ";
		}
		else if (SystemUtil.isHostWindows()) {
			newCommand += "cmd /c ";
		}

		return newCommand + command;
	}

	protected boolean isSH(String command) {
		return FileNameUtil.getExtension(command).equals("sh");
	}

	// ---------------------------------------------------------------- execute

	protected String prepareCommand() {
		String finalCommand = command;

		if (newShell) {
			finalCommand = resolveExecutor(command);
		}

		StringBand commandLine = new StringBand(args.size() * 2 + 2);

		commandLine.append(finalCommand);
		commandLine.append(StringPool.SPACE);

		for (String command : args) {
			commandLine.append(command);
			commandLine.append(StringPool.SPACE);
		}

		return commandLine.toString().trim();
	}

	public int execute() throws IOException, InterruptedException {
		String command = prepareCommand();

		return execute(command);
	}

	/**
	 * Executes command and returns the String representation of the input and
	 * the outout. Provided output and error streams are ignored.
	 */
	public String execToString() throws IOException, InterruptedException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		out = err = baos;

		String command = prepareCommand();

		baos.write(command.getBytes());
		baos.write(StringPool.BYTES_NEW_LINE);

		execute(command);

		return baos.toString();
	}

	private int execute(String command) throws IOException, InterruptedException {

		Process process = Runtime.getRuntime().exec(command, null, workingDirectory);

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
