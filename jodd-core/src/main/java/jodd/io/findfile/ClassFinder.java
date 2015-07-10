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

import jodd.io.FileNameUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.InExRules;
import jodd.util.StringUtil;
import jodd.util.Wildcard;
import jodd.util.ArraysUtil;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import jodd.io.ZipUtil;

import java.net.URL;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;

import static jodd.util.InExRuleMatcher.WILDCARD_PATH_RULE_MATCHER;
import static jodd.util.InExRuleMatcher.WILDCARD_RULE_MATCHER;

/**
 * Simple utility that scans <code>URL</code>s for classes.
 * Its purpose is to help scanning class paths for some classes.
 * Content of Jar files is also examined.
 * <p>
 * Scanning starts in included all mode (blacklist mode) for both jars and lists.
 * User can set explicit excludes. Of course, mode can be changed.
 * <p>
 * All paths are matched using {@link Wildcard#matchPath(String, String) path-style}
 * wildcard matcher. All entries are matched using {@link Wildcard#match(String, String) common-style}
 * wildcard matcher.
 *
 * @see ClassScanner
 */
public abstract class ClassFinder {

	private static final String CLASS_FILE_EXT = ".class";
	private static final String JAR_FILE_EXT = ".jar";

	// ---------------------------------------------------------------- excluded jars

	/**
	 * Array of system jars that are excluded from the search.
	 * By default, these paths are common for linux, windows and mac.
	 */
	protected static String[] systemJars = new String[] {
			"**/jre/lib/*.jar",
			"**/jre/lib/ext/*.jar",
			"**/Java/Extensions/*.jar",
			"**/Classes/*.jar"
	};

	protected final InExRules<String, String> rulesJars = createJarRules();

	/**
	 * Creates JAR rules. By default, excludes all system jars.
	 */
	protected InExRules<String, String> createJarRules() {
		InExRules<String, String> rulesJars = new InExRules<>(WILDCARD_PATH_RULE_MATCHER);

		for (String systemJar : systemJars) {
			rulesJars.exclude(systemJar);
		}

		return rulesJars;
	}

	/**
	 * Returns system jars.
	 */
	public static String[] getSystemJars() {
		return systemJars;
	}

	/**
	 * Specify excluded jars.
	 */
	public void setExcludedJars(String... excludedJars) {
		for (String excludedJar : excludedJars) {
			rulesJars.exclude(excludedJar);
		}
	}

	/**
	 * Specify included jars.
	 */
	public void setIncludedJars(String... includedJars) {
		for (String includedJar : includedJars) {
			rulesJars.include(includedJar);
		}
	}

	/**
	 * Sets white/black list mode for jars.
	 */
	public void setIncludeAllJars(boolean blacklist) {
		if (blacklist) {
			rulesJars.blacklist();
		} else {
			rulesJars.whitelist();
		}
	}

	/**
	 * Sets white/black list mode for jars.
	 */
	public void setExcludeAllJars(boolean whitelist) {
		if (whitelist) {
			rulesJars.whitelist();
		} else {
			rulesJars.blacklist();
		}
	}

	// ---------------------------------------------------------------- included entries

	protected final InExRules<String, String> rulesEntries = createEntriesRules();

	protected InExRules<String, String> createEntriesRules() {
		return new InExRules<>(WILDCARD_RULE_MATCHER);
	}

	/**
	 * Sets included set of names that will be considered during configuration.
	 * @see jodd.util.InExRules
	 */
	public void setIncludedEntries(String... includedEntries) {
		for (String includedEntry : includedEntries) {
			rulesEntries.include(includedEntry);
		}
	}

	/**
	 * Sets white/black list mode for entries.
	 */
	public void setIncludeAllEntries(boolean blacklist) {
		if (blacklist) {
			rulesEntries.blacklist();
		} else {
			rulesEntries.whitelist();
		}
	}
	/**
	 * Sets white/black list mode for entries.
	 */
	public void setExcludeAllEntries(boolean whitelist) {
		if (whitelist) {
			rulesEntries.whitelist();
		} else {
			rulesEntries.blacklist();
		}
	}

	/**
	 * Sets excluded names that narrows included set of packages.
	 * @see jodd.util.InExRules
	 */
	public void setExcludedEntries(String... excludedEntries) {
		for (String excludedEntry : excludedEntries) {
			rulesEntries.exclude(excludedEntry);
		}
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


	public boolean isIncludeResources() {
		return includeResources;
	}

	public void setIncludeResources(boolean includeResources) {
		this.includeResources = includeResources;
	}

	public boolean isIgnoreException() {
		return ignoreException;
	}

	/**
	 * Sets if exceptions during scanning process should be ignored or not.
	 */
	public void setIgnoreException(boolean ignoreException) {
		this.ignoreException = ignoreException;
	}

	// ---------------------------------------------------------------- scan

	/**
	 * Scans several URLs. If (#ignoreExceptions} is set, exceptions
	 * per one URL will be ignored and loops continues. 
	 */
	protected void scanUrls(URL... urls) {
		for (URL path : urls) {
			scanUrl(path);
		}
	}
	
	/**
	 * Scans single URL for classes and jar files.
	 * Callback {@link #onEntry(EntryData)} is called on
	 * each class name.
	 */
	protected void scanUrl(URL url) {
		File file = FileUtil.toFile(url);
		if (file == null) {
			if (ignoreException == false) {
				throw new FindFileException("URL is not a valid file: " + url);
			}
		}
		scanPath(file);
	}


	protected void scanPaths(File... paths) {
		for (File path : paths) {
			scanPath(path);
		}
	}

	protected void scanPaths(String... paths) {
		for (String path : paths) {
			scanPath(path);
		}
	}
	
	protected void scanPath(String path) {
		scanPath(new File(path));
	}

	/**
	 * Returns <code>true</code> if some JAR file has to be accepted.
	 */
	protected boolean acceptJar(File jarFile) {
		String path = jarFile.getAbsolutePath();
		path = FileNameUtil.separatorsToUnix(path);

		return rulesJars.match(path);
	}
	
	/**
	 * Scans single path.
	 */
	protected void scanPath(File file) {
		String path = file.getAbsolutePath();

		if (StringUtil.endsWithIgnoreCase(path, JAR_FILE_EXT) == true) {

			if (acceptJar(file) == false) {
				return;
			}
			scanJarFile(file);
		} else if (file.isDirectory() == true) {
			scanClassPath(file);
		}
	}

	// ---------------------------------------------------------------- internal

	/**
	 * Scans classes inside single JAR archive. Archive is scanned as a zip file.
	 * @see #onEntry(EntryData)
	 */
	protected void scanJarFile(File file) {
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(file);
		} catch (IOException ioex) {
			if (ignoreException == false) {
				throw new FindFileException("Invalid zip: " + file.getName(), ioex);
			}
			return;
		}
		Enumeration entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			String zipEntryName = zipEntry.getName();
			try {
				if (StringUtil.endsWithIgnoreCase(zipEntryName, CLASS_FILE_EXT)) {
					String entryName = prepareEntryName(zipEntryName, true);
					EntryData entryData = new EntryData(entryName, zipFile, zipEntry);
					try {
						scanEntry(entryData);
					} finally {
						entryData.closeInputStreamIfOpen();
					}
				} else if (includeResources == true) {
					String entryName = prepareEntryName(zipEntryName, false);
					EntryData entryData = new EntryData(entryName, zipFile, zipEntry);
					try {
						scanEntry(entryData);
					} finally {
						entryData.closeInputStreamIfOpen();
					}
				}
			} catch (RuntimeException rex) {
				if (ignoreException == false) {
					ZipUtil.close(zipFile);
					throw rex;
				}
			}
		}
		ZipUtil.close(zipFile);
	}

	/**
	 * Scans single classpath directory.
	 * @see #onEntry(EntryData)
	 */
	protected void scanClassPath(File root) {
		String rootPath = root.getAbsolutePath();
		if (rootPath.endsWith(File.separator) == false) {
			rootPath += File.separatorChar;
		}

		FindFile ff = new FindFile().setIncludeDirs(false).setRecursive(true).searchPath(rootPath);
		File file;
		while ((file = ff.nextFile()) != null) {
			String filePath = file.getAbsolutePath();
			try {
				if (StringUtil.endsWithIgnoreCase(filePath, CLASS_FILE_EXT)) {
					scanClassFile(filePath, rootPath, file, true);
				} else if (includeResources == true) {
					scanClassFile(filePath, rootPath, file, false);
				}
			} catch (RuntimeException rex) {
				if (ignoreException == false) {
					throw rex;
				}
			}
		}
	}

	protected void scanClassFile(String filePath, String rootPath, File file, boolean isClass) {
		if (StringUtil.startsWithIgnoreCase(filePath, rootPath) == true) {
			String entryName = prepareEntryName(filePath.substring(rootPath.length()), isClass);
			EntryData entryData = new EntryData(entryName, file);
			try {
				scanEntry(entryData);
			} finally {
				entryData.closeInputStreamIfOpen();
			}
		}
	}

	/**
	 * Prepares resource and class names. For classes, it strips '.class' from the end and converts
	 * all (back)slashes to dots. For resources, it replaces all backslashes to slashes.
	 */
	protected String prepareEntryName(String name, boolean isClass) {
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
	 * @see #scanEntry(EntryData) 
	 */
	protected boolean acceptEntry(String entryName) {
		return rulesEntries.match(entryName);
	}


	/**
	 * If entry name is {@link #acceptEntry(String) accepted} invokes {@link #onEntry(EntryData)} a callback}.
	 */
	protected void scanEntry(EntryData entryData) {
		if (acceptEntry(entryData.getName()) == false) {
			return;
		}
		try {
			onEntry(entryData);
		} catch (Exception ex) {
			throw new FindFileException("Scan entry error: " + entryData, ex);
		}
	}


	// ---------------------------------------------------------------- callback

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
	protected abstract void onEntry(EntryData entryData) throws Exception;

	// ---------------------------------------------------------------- utilities

	/**
	 * Returns type signature bytes used for searching in class file.
	 */
	protected byte[] getTypeSignatureBytes(Class type) {
		String name = 'L' + type.getName().replace('.', '/') + ';';
		return name.getBytes();
	}

	/**
	 * Returns <code>true</code> if class contains {@link #getTypeSignatureBytes(Class) type signature}.
	 * It searches the class content for bytecode signature. This is the fastest way of finding if come
	 * class uses some type. Please note that if signature exists it still doesn't means that class uses
	 * it in expected way, therefore, class should be loaded to complete the scan.
	 */
	protected boolean isTypeSignatureInUse(InputStream inputStream, byte[] bytes) {
		try {
			byte[] data = StreamUtil.readBytes(inputStream);
			int index = ArraysUtil.indexOf(data, bytes);
			return index != -1;
		} catch (IOException ioex) {
			throw new FindFileException("Read error", ioex);
		}
	}

	// ---------------------------------------------------------------- class loading

	/**
	 * Loads class by its name. If {@link #ignoreException} is set,
	 * no exception is thrown, but <code>null</code> is returned.
	 */
	protected Class loadClass(String className) throws ClassNotFoundException {
		try {
			return ClassLoaderUtil.loadClass(className);
		} catch (ClassNotFoundException cnfex) {
			if (ignoreException) {
				return null;
			}
			throw cnfex;
		} catch (Error error) {
			if (ignoreException) {
				return null;
			}
			throw error;
		}
	}


	// ---------------------------------------------------------------- provider

	/**
	 * Provides input stream on demand. Input stream is not open until get().
	 */
	protected static class EntryData {

		private final File file;
		private final ZipFile zipFile;
		private final ZipEntry zipEntry;
		private final String name;

		EntryData(String name, ZipFile zipFile, ZipEntry zipEntry) {
			this.name = name;
			this.zipFile = zipFile;
			this.zipEntry = zipEntry;
			this.file = null;
			inputStream = null;
		}
		EntryData(String name, File file) {
			this.name = name;
			this.file = file;
			this.zipEntry = null;
			this.zipFile = null;
			inputStream = null;
		}

		private InputStream inputStream;

		/**
		 * Returns entry name.
		 */
		public String getName() {
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
		public String getArchiveName() {
			if (zipFile != null) {
				return zipFile.getName(); 
			}
			return null;
		}

		/**
		 * Opens zip entry or plain file and returns its input stream.
		 */
		public InputStream openInputStream() {
			if (zipFile != null) {
				try {
					inputStream = zipFile.getInputStream(zipEntry);
					return inputStream;
				} catch (IOException ioex) {
					throw new FindFileException("Input stream error: '" + zipFile.getName()
							+ "', entry: '" + zipEntry.getName() + "'." , ioex);
				}
			}
			try {
				inputStream = new FileInputStream(file);
				return inputStream;
			} catch (FileNotFoundException fnfex) {
				throw new FindFileException("Unable to open: " + file.getAbsolutePath(), fnfex);
			}
		}

		/**
		 * Closes input stream if opened.
		 */
		void closeInputStreamIfOpen() {
			if (inputStream == null) {
				return;
			}
			StreamUtil.close(inputStream);
			inputStream = null;
		}

		@Override
		public String toString() {
			return "EntryData{" + name + '\'' +'}';
		}
	}
}
