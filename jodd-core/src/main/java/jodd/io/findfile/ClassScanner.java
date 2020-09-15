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

package jodd.io.findfile;

import jodd.inex.InExRules;
import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.io.IOUtil;
import jodd.io.ZipUtil;
import jodd.util.ArraysUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.ClassPathUtil;
import jodd.util.Consumers;
import jodd.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static jodd.inex.InExRuleMatcher.WILDCARD_PATH_RULE_MATCHER;
import static jodd.inex.InExRuleMatcher.WILDCARD_RULE_MATCHER;

/**
 * Convenient class path scanner.
 */
public class ClassScanner {

	private static final String CLASS_FILE_EXT = ".class";
	private static final String JAR_FILE_EXT = ".jar";

	/**
	 * Create new class scanner.
	 */
	public static ClassScanner create() {
		return new ClassScanner();
	}

	public ClassScanner() {
		this.rulesJars = new InExRules<>(WILDCARD_PATH_RULE_MATCHER);
		this.rulesEntries = new InExRules<>(WILDCARD_RULE_MATCHER);

		excludeJars(SYSTEM_JARS);
	}

	// ---------------------------------------------------------------- excluded jars

	/**
	 * Array of system jars that are excluded from the search.
	 * By default, these paths are common for linux, windows and mac.
	 */
	protected static final String[] SYSTEM_JARS = new String[] {
		"**/jre/lib/*.jar",
		"**/jre/lib/ext/*.jar",
		"**/Java/Extensions/*.jar",
		"**/Classes/*.jar"
	};

	protected static final String[] COMMONLY_EXCLUDED_JARS = new String[] {
		"**/tomcat*",
		"**/jetty*",
		"**/javafx*",
		"**/junit*",
		"**/javax*",
		"**/org.eclipse.*",
		"**/ant*",
		"**/idea_rt.jar",
	};

	protected final InExRules<String, String, String> rulesJars;

	/**
	 * Specify excluded jars.
	 */
	public ClassScanner excludeJars(final String... excludedJars) {
		for (final String excludedJar : excludedJars) {
			rulesJars.exclude(excludedJar);
		}
		return this;
	}

	/**
	 * Exclude some commonly unused jars.
	 */
	public ClassScanner excludeCommonJars() {
		return excludeJars(COMMONLY_EXCLUDED_JARS);
	}

	/**
	 * Specify included jars.
	 */
	public ClassScanner includeJars(final String... includedJars) {
		for (final String includedJar : includedJars) {
			rulesJars.include(includedJar);
		}
		return this;
	}

	/**
	 * Sets white/black list mode for jars.
	 */
	public ClassScanner includeAllJars(final boolean blacklist) {
		if (blacklist) {
			rulesJars.blacklist();
		} else {
			rulesJars.whitelist();
		}
		return this;
	}

	/**
	 * Sets white/black list mode for jars.
	 */
	public ClassScanner excludeAllJars(final boolean whitelist) {
		if (whitelist) {
			rulesJars.whitelist();
		} else {
			rulesJars.blacklist();
		}
		return this;
	}

	// ---------------------------------------------------------------- included entries

	protected static final String[] COMMONLY_EXCLUDED_ENTRIES = new String[] {
		"java.*",
		"ch.qos.logback.*",
		"sun.*",
		"com.sun.*",
		"org.eclipse.*",
	};

	protected final InExRules<String, String, String> rulesEntries;
	protected boolean detectEntriesMode = false;

	/**
	 * Sets included set of names that will be considered during configuration.
	 * @see InExRules
	 */
	public ClassScanner includeEntries(final String... includedEntries) {
		for (final String includedEntry : includedEntries) {
			rulesEntries.include(includedEntry);
		}
		return this;
	}

	/**
	 * Sets white/black list mode for entries.
	 */
	public ClassScanner includeAllEntries(final boolean blacklist) {
		if (blacklist) {
			rulesEntries.blacklist();
		} else {
			rulesEntries.whitelist();
		}
		return this;
	}
	/**
	 * Sets white/black list mode for entries.
	 */
	public ClassScanner excludeAllEntries(final boolean whitelist) {
		if (whitelist) {
			rulesEntries.whitelist();
		} else {
			rulesEntries.blacklist();
		}
		return this;
	}

	/**
	 * Sets excluded names that narrows included set of packages.
	 * @see InExRules
	 */
	public ClassScanner excludeEntries(final String... excludedEntries) {
		for (final String excludedEntry : excludedEntries) {
			rulesEntries.exclude(excludedEntry);
		}
		return this;
	}

	/**
	 * Excludes some commonly skipped packages.
	 */
	public ClassScanner excludeCommonEntries() {
		return excludeEntries(COMMONLY_EXCLUDED_ENTRIES);
	}

	public ClassScanner detectEntriesMode(final boolean detectMode) {
		this.detectEntriesMode = detectMode;
		return this;
	}

	// ---------------------------------------------------------------- implementation

	/**
	 * If set to <code>true</code> all files will be scanned and not only classes.
	 */
	protected boolean includeResources;
	/**
	 * If set to <code>true</code> exceptions for entry scans are ignored.
	 */
	protected boolean ignoreException;

	public ClassScanner includeResources(final boolean includeResources) {
		this.includeResources = includeResources;
		return this;
	}

	/**
	 * Sets if exceptions during scanning process should be ignored or not.
	 */
	public ClassScanner ignoreException(final boolean ignoreException) {
		this.ignoreException = ignoreException;
		return this;
	}

	// ---------------------------------------------------------------- scan


	/**
	 * Returns <code>true</code> if some JAR file has to be accepted.
	 */
	protected boolean acceptJar(final File jarFile) {
		String path = jarFile.getAbsolutePath();
		path = FileNameUtil.separatorsToUnix(path);

		return rulesJars.match(path);
	}

	// ---------------------------------------------------------------- internal

	/**
	 * Scans classes inside single JAR archive. Archive is scanned as a zip file.
	 * @see #onEntry(ClassPathEntry)
	 */
	protected void scanJarFile(final File file) {
		final ZipFile zipFile;
		try {
			zipFile = new ZipFile(file);
		} catch (final IOException ioex) {
			if (!ignoreException) {
				throw new FindFileException("Invalid zip: " + file.getName(), ioex);
			}
			return;
		}
		final Enumeration entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			final String zipEntryName = zipEntry.getName();
			try {
				if (StringUtil.endsWithIgnoreCase(zipEntryName, CLASS_FILE_EXT)) {
					final String entryName = prepareEntryName(zipEntryName, true);
					final ClassPathEntry classPathEntry = new ClassPathEntry(entryName, zipFile, zipEntry);
					try {
						scanEntry(classPathEntry);
					} finally {
						classPathEntry.closeInputStream();
					}
				} else if (includeResources) {
					final String entryName = prepareEntryName(zipEntryName, false);
					final ClassPathEntry classPathEntry = new ClassPathEntry(entryName, zipFile, zipEntry);
					try {
						scanEntry(classPathEntry);
					} finally {
						classPathEntry.closeInputStream();
					}
				}
			} catch (final RuntimeException rex) {
				if (!ignoreException) {
					ZipUtil.close(zipFile);
					throw rex;
				}
			}
		}
		ZipUtil.close(zipFile);
	}

	/**
	 * Scans single classpath directory.
	 * @see #onEntry(ClassPathEntry)
	 */
	protected void scanClassPath(final File root) {
		String rootPath = root.getAbsolutePath();
		if (!rootPath.endsWith(File.separator)) {
			rootPath += File.separatorChar;
		}

		final FindFile ff = FindFile.create().includeDirs(false).recursive(true).searchPath(rootPath);
		File file;
		while ((file = ff.nextFile()) != null) {
			final String filePath = file.getAbsolutePath();
			try {
				if (StringUtil.endsWithIgnoreCase(filePath, CLASS_FILE_EXT)) {
					scanClassFile(filePath, rootPath, file, true);
				} else if (includeResources) {
					scanClassFile(filePath, rootPath, file, false);
				}
			} catch (final RuntimeException rex) {
				if (!ignoreException) {
					throw rex;
				}
			}
		}
	}

	protected void scanClassFile(final String filePath, final String rootPath, final File file, final boolean isClass) {
		if (StringUtil.startsWithIgnoreCase(filePath, rootPath)) {
			final String entryName = prepareEntryName(filePath.substring(rootPath.length()), isClass);
			final ClassPathEntry classPathEntry = new ClassPathEntry(entryName, file);
			try {
				scanEntry(classPathEntry);
			} finally {
				classPathEntry.closeInputStream();
			}
		}
	}

	/**
	 * Prepares resource and class names. For classes, it strips '.class' from the end and converts
	 * all (back)slashes to dots. For resources, it replaces all backslashes to slashes.
	 */
	protected String prepareEntryName(final String name, final boolean isClass) {
		String entryName = name;
		if (isClass) {
			entryName = name.substring(0, name.length() - 6);		// 6 == ".class".length()
			entryName = StringUtil.replaceChar(entryName, '/', '.');
			entryName = StringUtil.replaceChar(entryName, '\\', '.');
		} else {
			entryName = '/' + StringUtil.replaceChar(entryName, '\\', '/');
		}
		return entryName;
	}

	/**
	 * Returns <code>true</code> if some entry name has to be accepted.
	 * @see #prepareEntryName(String, boolean)
	 * @see #scanEntry(ClassPathEntry)
	 */
	protected boolean acceptEntry(final String entryName) {
		return rulesEntries.match(entryName);
	}

	/**
	 * If entry name is {@link #acceptEntry(String) accepted} invokes {@link #onEntry(ClassPathEntry)} a callback}.
	 */
	protected void scanEntry(final ClassPathEntry classPathEntry) {
		if (!acceptEntry(classPathEntry.name())) {
			return;
		}
		try {
			onEntry(classPathEntry);
		} catch (final Exception ex) {
			throw new FindFileException("Scan entry error: " + classPathEntry, ex);
		}
	}


	// ---------------------------------------------------------------- callback

	private final Consumers<ClassPathEntry> entryDataConsumers = Consumers.empty();

	/**
	 * Registers a {@link ClassPathEntry class path entry} consumer.
	 * It will be called on each loaded entry.
	 */
	public ClassScanner registerEntryConsumer(final Consumer<ClassPathEntry> entryDataConsumer) {
		entryDataConsumers.add(entryDataConsumer);
		return this;
	}

	/**
	 * Called during classpath scanning when class or resource is found.
	 * <ul>
	 * <li>Class name is java-alike class name (pk1.pk2.class) that may be immediately used
	 * for dynamic loading.</li>
	 * <li>Resource name starts with '\' and represents either jar path (\pk1/pk2/res) or relative file path (\pk1\pk2\res).</li>
	 * </ul>
	 *
	 * <code>InputStream</code> is provided by InputStreamProvider and opened lazy.
	 * Once opened, input stream doesn't have to be closed - this is done by this class anyway.
	 */
	protected void onEntry(final ClassPathEntry classPathEntry) {
		entryDataConsumers.accept(classPathEntry);
	}

	// ---------------------------------------------------------------- utilities

	/**
	 * Returns type signature bytes used for searching in class file.
	 */
	public static byte[] bytecodeSignatureOfType(final Class type) {
		final String name = 'L' + type.getName().replace('.', '/') + ';';
		return name.getBytes();
	}

	// ---------------------------------------------------------------- provider

	/**
	 * Provides input stream on demand. Input stream is not open until get().
	 */
	public class ClassPathEntry {

		private final File file;
		private final ZipFile zipFile;
		private final ZipEntry zipEntry;
		private final String name;

		ClassPathEntry(final String name, final ZipFile zipFile, final ZipEntry zipEntry) {
			this.name = name;
			this.zipFile = zipFile;
			this.zipEntry = zipEntry;
			this.file = null;
			this.inputStream = null;
		}
		ClassPathEntry(final String name, final File file) {
			this.name = name;
			this.file = file;
			this.zipEntry = null;
			this.zipFile = null;
			this.inputStream = null;
		}

		private InputStream inputStream;
		private byte[] inputStreamBytes;

		/**
		 * Returns entry name.
		 */
		public String name() {
			return name;
		}

		/**
		 * Returns <code>true</code> if archive.
		 */
		public boolean isArchive() {
			return zipFile != null;
		}

		/**
		 * Returns archive name or <code>null</code> if entry is not inside archived file.
		 */
		public String archiveName() {
			if (zipFile != null) {
				return zipFile.getName();
			}
			return null;
		}

		/**
		 * Returns <code>true</code> if class contains {@link #bytecodeSignatureOfType(Class) type signature}.
		 * It searches the class content for bytecode signature. This is the fastest way of finding if come
		 * class uses some type. Please note that if signature exists it still doesn't means that class uses
		 * it in expected way, therefore, class should be loaded to complete the scan.
		 */
		public boolean isTypeSignatureInUse(final byte[] bytes) {
			try {
				final byte[] data = readBytes();
				final int index = ArraysUtil.indexOf(data, bytes);
				return index != -1;
			} catch (final IOException ioex) {
				throw new FindFileException("Read error", ioex);
			}
		}

		/**
		 * Reads stream bytes. Since stream can be read only once, the byte content
		 * is cached.
		 */
		public byte[] readBytes() throws IOException {
			openInputStream();

			if (inputStreamBytes == null) {
				inputStreamBytes = IOUtil.readBytes(inputStream);
			}
			return inputStreamBytes;
		}

		/**
		 * Opens zip entry or plain file and returns its input stream.
		 */
		public InputStream openInputStream() {
			if (inputStream != null) {
				return inputStream;
			}
			if (zipFile != null && zipEntry != null) {
				try {
					inputStream = zipFile.getInputStream(zipEntry);
					return inputStream;
				} catch (final IOException ioex) {
					throw new FindFileException("Input stream error: '" + zipFile.getName()
						+ "', entry: '" + zipEntry.getName() + "'." , ioex);
				}
			}
			if (file != null) {
				try {
					inputStream = new FileInputStream(file);
					return inputStream;
				} catch (final FileNotFoundException fnfex) {
					throw new FindFileException("Unable to open: " + file.getAbsolutePath(), fnfex);
				}
			}
			throw new FindFileException("Unable to open stream: " + name());
		}

		/**
		 * Closes input stream if opened.
		 */
		public void closeInputStream() {
			if (inputStream == null) {
				return;
			}
			IOUtil.close(inputStream);
			inputStream = null;
			inputStreamBytes = null;
		}

		/**
		 * Loads class by its name. If {@link #ignoreException} is set,
		 * no exception is thrown, but <code>null</code> is returned.
		 */
		public Class loadClass() throws ClassNotFoundException {
			try {
				return ClassLoaderUtil.loadClass(name);
			} catch (final ClassNotFoundException | Error cnfex) {
				if (ignoreException) {
					return null;
				}
				throw cnfex;
			}
		}

		@Override
		public String toString() {
			return "ClassPathEntry{" + name + '\'' +'}';
		}
	}

	// ---------------------------------------------------------------- public scanning

	private final Set<File> filesToScan = new LinkedHashSet<>();

	/**
	 * Scans URLs. If (#ignoreExceptions} is set, exceptions
	 * per one URL will be ignored and loops continues.
	 */
	public ClassScanner scan(final URL... urls) {
		for (final URL url : urls) {
			final File file = FileUtil.toContainerFile(url);
			if (file == null) {
				if (!ignoreException) {
					throw new FindFileException("URL is not a valid file: " + url);
				}
			}
			else {
				filesToScan.add(file);
			}
		}
		return this;
	}

	/**
	 * Scans {@link ClassPathUtil#getDefaultClasspath() default class path}.
	 */
	public ClassScanner scanDefaultClasspath() {
		return scan(ClassPathUtil.getDefaultClasspath());
	}

	/**
	 * Scans provided paths.
	 */
	public ClassScanner scan(final File... paths) {
		filesToScan.addAll(Arrays.asList(paths));
		return this;
	}

	/**
	 * Scans provided paths.
	 */
	public ClassScanner scan(final String... paths) {
		for (final String path : paths) {
			filesToScan.add(new File(path));
		}
		return this;
	}

	/**
	 * Starts with the scanner.
	 */
	public void start() {
		if (detectEntriesMode) {
			rulesEntries.detectMode();
		}

		filesToScan.forEach(file -> {
			final String path = file.getAbsolutePath();
			if (StringUtil.endsWithIgnoreCase(path, JAR_FILE_EXT)) {
				if (!acceptJar(file)) {
					return;
				}
				scanJarFile(file);
			} else if (file.isDirectory()) {
				scanClassPath(file);
			}
		});
	}

}
