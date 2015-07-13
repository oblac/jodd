package jodd.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.StreamGobbler;

/**
 *
 * Simple tool to easily execute native applications.
 *
 * @author Vilmos Papp
 */
public class CommandLine {

	public static final String ERROR_TYPE = "error";
	public static final String OUTPUT_TYPE = "output";

	public static final String CMD = "cmd";
	public static final String OPEN = "open";
	public static final String SH = "sh";
	public static final String SHC = "sh -c";

	public static final int OK = 0;

	protected final List<String> commands = new ArrayList<>();
	protected File workingDirectory;

	// ---------------------------------------------------------------- ctor

	protected CommandLine(String command) {
		resolveExecutor(command);

		commands.add(command);
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
	public CommandLine setWorkingDirectory(File workDirectory) {
		this.workingDirectory = workDirectory;

		return this;
	}

	/**
	 * Defines working directory.
	 */
	public CommandLine setWorkingDirectory(String workDirectory) {
		this.workingDirectory = new File(workDirectory);

		return this;
	}

	/**
	 * Adds single argument.
	 */
	public CommandLine addArgument(String argument) {
		commands.add(argument);

		return this;
	}

	/**
	 * Adds several arguments.
	 */
	public CommandLine addArguments(String... arguments) {
		if (arguments != null && arguments.length > 0) {
			for (String argument : arguments) {
				commands.add(argument);
			}
		}

		return this;
	}


	// ---------------------------------------------------------------- executor

	/**
	 * Resolves system-dependent executor.
	 */
	protected void resolveExecutor(String command) {
		File commandFile = new File(command);

		if (SystemUtil.isHostMac()) {
			if (isSH(command)) {
				commands.add(SH);
			}
			else if (commandFile.canExecute() && !FileNameUtil.hasExtension(commandFile.getAbsolutePath())) {
			}
			else if (FileUtil.isExistingFile(commandFile)) { // for native application and files with associated applications, open command should be used
				commands.add(OPEN);
			}
			else {
				commands.add(SHC);
			}
		}
		else if (SystemUtil.isHostAix() || SystemUtil.isHostLinux() || SystemUtil.isHostSolaris() || SystemUtil.isHostUnix()) {
			commands.add(SH);
			commands.add("-c");
		}
		else if (SystemUtil.isHostWindows()) {
			commands.add(CMD);

			commands.add("/c");
		}
	}

	protected boolean isSH(String command) {
		return FileNameUtil.getExtension(command).equals(SH);
	}

	// ---------------------------------------------------------------- execute

	public int execute() throws IOException, InterruptedException {
		return execute(OUTPUT_TYPE);
	}

	public int execute(String outputType) throws IOException, InterruptedException {
		return execute(outputType, ERROR_TYPE);
	}

	public int execute(String outputType, String errorType) throws IOException, InterruptedException {
		return execute(outputType, errorType, System.out, System.out);
	}

	public int execute(String outputType, String errorType, OutputStream out, OutputStream error) throws IOException, InterruptedException {
		String[] commandsArray = commands.toArray(new String[commands.size()]);

		StringBand commandLine = new StringBand(commandsArray.length * 2);

		for (String command : commandsArray) {
			commandLine.append(command);
			commandLine.append(StringPool.SPACE);
		}

		Process process = Runtime.getRuntime().exec(commandLine.toString().trim(), null, workingDirectory);

		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorType, error);

		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), outputType, out);

		int exitCode = process.waitFor();

		outputGobbler.start();

		errorGobbler.start();

		return exitCode;
	}
}
