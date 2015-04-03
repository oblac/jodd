package jodd.execution;

import java.io.File;
import java.io.IOException;

import jodd.io.StreamGobbler;
import jodd.util.ArraysUtil;
import jodd.util.StringUtil;

/**
 * @author Vilmos Papp
 */
public class CommandLine {
	public static final String ERROR_TYPE = "error";

	public static final int OK = 0;

	public static final String OUTPUT_TYPE = "output";

	private String[] command;

	private File workDirectory;

	public CommandLine(String[] command, File workDirectory) {
		this.command = command;
		this.workDirectory = workDirectory;
	}

	public int execute() throws IOException, InterruptedException {
		return execute(OUTPUT_TYPE);
	}

	public int execute(String outputType) throws IOException, InterruptedException {
		return execute(outputType, ERROR_TYPE);
	}

	public int execute(String outputType, String errorType) throws IOException, InterruptedException {

		int exitCode = OK;

		Process process = Runtime.getRuntime().exec(command, null, workDirectory);

		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorType, System.out);

		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), outputType, System.out);

		exitCode = process.waitFor();

		outputGobbler.start();

		errorGobbler.start();

		return exitCode;
	}
}
