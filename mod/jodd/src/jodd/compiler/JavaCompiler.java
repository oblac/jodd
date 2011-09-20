// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.compiler;

import jodd.io.FileNameUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringBand;
import jodd.util.StringPool;
import jodd.util.ThreadUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Java compilation tool. Supports following compilers:
 * <li>jikesw, jikes (disabled by default)
 * <li>internal javac (from tools.jar)
 * <li>external javac
 */
public class JavaCompiler {

	protected static final String COMPILERS[] = {
			"%j/bin/jikesw", "jikesw", "jikes", "javaw com.sun.tools.javac.Main -classpath %C", "javac -classpath %c"
	};

	protected String classpath;
	protected String compilerSpec;
	protected String argDebug;
	protected String argEncoding;
	protected String argSource;
	protected String argTarget;
	protected boolean haveToolsJar;
	protected boolean useJikes;

	/**
	 * Creates new java compiler.
	 */
	public JavaCompiler() {
		compilerSpec = "-nowarn -sourcepath %s -d %d %f";
		argDebug = argEncoding = argSource = argTarget = StringPool.EMPTY;
		useJikes = false;

		File[] classPath = ClassLoaderUtil.getDefaultClasspath();
		StringBand sb = new StringBand(classPath.length + 1);
		for (File entry : classPath) {
			sb.append(entry.toString() + File.pathSeparatorChar);
		}
		File toolsJar = ClassLoaderUtil.findToolsJar();
		if (toolsJar != null) {
			haveToolsJar = true;
			sb.append(toolsJar);
		}
		classpath = sb.toString();
	}

	/**
	 * Specifies new java compiler specification.
	 * By default it is set to <code>-nowarn -sourcepath %s -d %d %f</code>.
	 * <p>
	 * The whole command line can be set in here, but it also can be set
	 * via properties of this class.
	 */
	public void setCompilerSpec(String compiler) {
		compilerSpec = compiler;
	}

	/**
	 * Enables or disables jikes compiler.
	 */
	public void setUseJikes(boolean useJikes) {
		this.useJikes = useJikes;
	}

	/**
	 * Add more classpath after default one.
	 */
	public void appendClasspath(String... classpathItems) {
		for (String classpathItem : classpathItems) {
			try {
				File file = new File(classpathItem);
				String canonicalPath = file.getCanonicalPath();
				if (!classpath.contains(canonicalPath)) {
					classpath = classpath + File.pathSeparatorChar + canonicalPath;
				}
			} catch (IOException ignore) {
			}
		}
	}

	/**
	 * Sets debug argument.
	 */
	public void setDebug(boolean debug) {
		argDebug = debug ? "-g" : "";
	}

	/**
	 * Sets encoding. Not available on jikes.
	 */
	public void setEncoding(String encoding) {
		argEncoding = "-encoding " + encoding;
	}

	public void setSourceVersion(String source) {
		argSource = "-source " + source;
	}

	public void setTargetVersion(String target) {
		argTarget = "-target " + target;
	}

	// ---------------------------------------------------------------- compile

	public void compile(String source) throws IOException {
		compile(new File(source), null, null, null);
	}

	public void compile(File sourceFile, String className, File sourceDirectoryFile, File outputDirectoryFile) throws IOException {

		// ensure source directory file
		if (sourceDirectoryFile == null) {
			sourceDirectoryFile = sourceFile.getParentFile();
		}

		String source = sourceFile.getCanonicalPath();
		String sourceDirectory = sourceDirectoryFile.getCanonicalPath();

		if (source.startsWith(sourceDirectory) == false) {
			throw new CompilationException("Source file is not under source directory.");
		}

		// ensure class name
		if (className == null) {
			className = source.substring(sourceDirectory.length() + 1);
			className = className.replace('/', '.');
			className = className.replace('\\', '.');
			className = FileNameUtil.removeExtension(className);
		}

		// ensure output dir
		if (outputDirectoryFile == null) {
			outputDirectoryFile = sourceFile.getParentFile();
			for (int index = 0; (index = className.indexOf('.', index)) != -1; index++) {
				outputDirectoryFile = outputDirectoryFile.getParentFile();
			}
		}
		String outputDirectory = outputDirectoryFile.getCanonicalPath();

		// add output dir to the classpath
		if (!classpath.contains(outputDirectory)) {
			classpath = classpath + File.pathSeparatorChar + outputDirectory;
		}

		// compile
		StringBuffer errors = new StringBuffer();
		boolean status = invokeCompileProcess(source, sourceDirectory, outputDirectory, classpath, errors);
		if (!status) {
			if (errors.length() > 0) {
				throw new CompilationException(errors.toString());
			} else {
				throw new CompilationException("Java compilation failure.");
			}
		}
	}

	// ---------------------------------------------------------------- helpers

	/**
	 * Invokes compiler.
	 */
	private boolean invokeCompileProcess(String source, String sourceDir, String resultDir, String classpath, StringBuffer err) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process p;
		String envs[] = {"CLASSPATH=" + classpath};

		int compilerIndex = 0;
		if (useJikes == false) {
			compilerIndex = 3;
		}
		while(true) {
			try {
				String compSpec = COMPILERS[compilerIndex];
				compSpec += ' ' + argDebug;
				compSpec += ' ' + argSource;
				compSpec += ' ' + argTarget;
				if (useJikes == false) {
					compSpec += ' ' + argEncoding;
				}
				compSpec += ' ' + compilerSpec;
				String command = parseCompileArgs(compSpec, source, sourceDir, resultDir, classpath);
				p = runtime.exec(command, envs);
				break;
			} catch (IOException ignore) {
				if (++compilerIndex >= COMPILERS.length) {
					throw new CompilationException("Compiler not found.");
				}
			}
		}

		boolean processDone = false;
		InputStream stdout = p.getInputStream();
		InputStream stderr = p.getErrorStream();

		while ((stdout.available() == 0) && (stderr.available() == 0) && !processDone) {
			ThreadUtil.sleep(100);
			try {
				p.exitValue();
				processDone = true;
			} catch (IllegalThreadStateException ignore) {
			}
		}

		if (!isStreamEmpty(stdout, err) || !isStreamEmpty(stderr, err)) {
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				throw new CompilationException(e);
			}
			return false;
		} else {
			return true;
		}
	}

	private static boolean isStreamEmpty(InputStream in, StringBuffer err) throws IOException {
		BufferedReader errorIn = new BufferedReader(new InputStreamReader(in));
		if (in.available() == 0) {
			return true;
		}
		String line;
		if ((line = errorIn.readLine()) == null) {
			return true;
		}
		err.append(line).append(StringPool.NEWLINE);
		while ((line = errorIn.readLine()) != null) {
			err.append(line).append(StringPool.NEWLINE);
		}
		return false;
	}

	/**
	 * Parse compiler command line arguments.
	 */
	private String parseCompileArgs(String compilerSpec, String source, String srcdir, String outdir, String classpath) throws CompilationException {

		String quote = StringPool.QUOTE;
		if (File.separator.equals(StringPool.SLASH)) {
			quote = StringPool.EMPTY;
		}

		StringBuilder sb = new StringBuilder();

		char chars[] = compilerSpec.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '%') {
				c = chars[++i];

				// %d is destination dir
				if ((c == 'd') && (outdir != null)) {
					sb.append(quote).append(outdir).append(quote);
				} else
				// %C is classpath for tools.jar
				if (c == 'C') {
					if (!haveToolsJar) {
						throw new CompilationException("Can't find tools.jar");
					}
					sb.append(quote).append(classpath).append(quote);
				} else
				// %c is classpath
				if (c == 'c') {
					sb.append(quote).append(classpath).append(quote);
				} else
				// %f is source file
				if (c == 'f') {
					sb.append(quote).append(source).append(quote);
				} else
				// %s is source folder
				if (c == 's') {
					if (srcdir == null) {
						srcdir = outdir;
					}
					sb.append(quote).append(srcdir).append(quote);
				}
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

}