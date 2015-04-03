package jodd.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jodd.io.FileNameUtil;
import jodd.util.SystemUtil;

/**
 * @author Vilmos Papp
 */
public class CommandLineBuilder {

	public static final String CMD = "cmd";

	public static final String OPEN = "open";

	public static final String SH = "sh";

	private List<String> commandLine;

	private String executor;

	private File workDirectory;

	public CommandLineBuilder() {
	}

	public CommandLineBuilder(String command) {
		addCommand(command);
	}

	public CommandLineBuilder(String command, String[] arguments) {
		addCommand(command);

		addArguments(arguments);
	}

	public CommandLineBuilder(String command, String[] arguments, File workDirectory) {
		addCommand(command);
	}

	public CommandLineBuilder addArguments(String[] arguments) {
		if (arguments != null && arguments.length > 0) {
			for (String argument : arguments) {
				addArgument(argument);
			}
		}

		return this;
	}

	public CommandLineBuilder addArgument(String argument) {
		addToCommandLine(argument);

		return this;
	}

	public CommandLineBuilder addCommand(String command) {
		getExecutor(command);

		addToCommandLine(command);

		return this;
	}

	public CommandLineBuilder setWorkDirectory(File workDirectory) {
		this.workDirectory = workDirectory;

		return this;
	}

	public CommandLine create() {
		commandLine.add(0, executor);

		CommandLine command = new CommandLine(commandLine.toArray(new String[0]), workDirectory);

		return command;
	}

	protected void addToCommandLine(String parameter) {
		if (commandLine == null) {
			commandLine = new ArrayList<String>();
		}

		commandLine.add(parameter);
	}

	protected void getExecutor(String command) {
		if (SystemUtil.isHostAix() || SystemUtil.isHostLinux() || SystemUtil.isHostSolaris() || SystemUtil.isHostUnix()) {
			executor = SH;

			if (!SystemUtil.isHostMac()) {
				addToCommandLine("-c");
			}
		}
		else if (SystemUtil.isHostWindows()) {
			executor = CMD;

			addToCommandLine("/c");
		}
		else if (SystemUtil.isHostMac()) {
			if (isSH(command)) {
				executor = SH;
			}
			else { // for native application and files with associated applicaions, open command should be used
				executor = OPEN;
			}
		}

		if (executor == null) {
			throw new NoSuchExecutorException("Executor: " + executor +" found for command: " + command);
		}
	}

	protected boolean isSH(String command) {
		return FileNameUtil.getExtension(command).equals(SH);
	}

}
