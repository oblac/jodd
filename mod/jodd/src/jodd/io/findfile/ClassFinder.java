// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.io.FileNameUtil;
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

/**
 * Simple utility that scans <code>URL</code>s for classes.
 * Its purpose is to help scanning class paths for some classes.
 * Content of Jar files is also examined.
 * @see ClassScanner
 */
public abstract class ClassFinder {

	private static final String CLASS_FILE_EXT = ".class";
	private static final String JAR_FILE_EXT = ".jar";

	// ---------------------------------------------------------------- excluded jars

	/**
	 * Array of system jars that are excluded from the search.
	 * By default it consists of java runtime libraries.
	 */
	protected String[] systemJars = new String[] {
			"*/jre/lib/*.jar",
			"*/jre/lib/ext/*.jar",
			"*/tools.jar",
			"*/j2ee.jar"
	};
	/**
	 * Array of excluded jars.
	 */
	protected String[] excludedJars;
	/**
	 * Array of jar file name patterns that are included in the search.
	 * This rule is applied after the excluded rule.
	 */
	protected String[] includedJars;
	/**
	 * If set to <code>true</code> jars will be scanned using path wildcards.
	 */
	protected boolean usePathWildcards;


	public String[] getSystemJars() {
		return systemJars;
	}

	public void setSystemJars(String[] systemJars) {
		this.systemJars = systemJars;
	}

	public String[] getExcludedJars() {
		return excludedJars;
	}

	public void setExcludedJars(String... excludedJars) {
		this.excludedJars = excludedJars;
	}

	public String[] getIncludedJars() {
		return includedJars;
	}

	public void setIncludedJars(String... includedJars) {
		this.includedJars = includedJars;
	}

	public boolean isUsePathWildcards() {
		return usePathWildcards;
	}

	public void setUsePathWildcards(boolean usePathWildcards) {
		this.usePathWildcards = usePathWildcards;
	}

	// ---------------------------------------------------------------- included packages

	protected String[] includedEntries;    // array of included name patterns
	protected String[] excludedEntries;    // array of excluded name patterns

	public String[] getIncludedEntries() {
		return includedEntries;
	}

	/**
	 * Sets included set of names that will be considered during configuration,
	 */
	public void setIncludedEntries(String... includedEntries) {
		this.includedEntries = includedEntries;
	}

	public String[] getExcludedEntries() {
		return excludedEntries;
	}

	/**
	 * Sets excluded names that narrows included set of packages.
	 */
	public void setExcludedEntries(String... excludedEntries) {
		this.excludedEntries = excludedEntries;
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

		if (systemJars != null) {
			int ndx = usePathWildcards ?
					Wildcard.matchPathOne(path, systemJars) :
					Wildcard.matchOne(path, systemJars);
			if (ndx != -1) {
				return false;
			}
		}
		if (excludedJars != null) {
			int ndx = usePathWildcards ?
					Wildcard.matchPathOne(path, excludedJars) :
					Wildcard.matchOne(path, excludedJars);
			if (ndx != -1) {
				return false;
			}
		}
		if (includedJars != null) {
			int ndx = usePathWildcards ?
					Wildcard.matchPathOne(path, includedJars) :
					Wildcard.matchOne(path, includedJars);
			if (ndx == -1) {
				return false;
			}
		}
		return true;
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
				throw new FindFileException("Unable to work with zip file: " + file.getName(), ioex);
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
		if (excludedEntries != null) {
			if (Wildcard.matchOne(entryName, excludedEntries) != -1) {
				return false;
			}
		}
		if (includedEntries != null) {
			if (Wildcard.matchOne(entryName, includedEntries) == -1) {
				return false;
			}
		}
		return true;
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
			throw new FindFileException("Unable to scan entry: " + entryData, ex);
		}
	}


	// ---------------------------------------------------------------- callback

	/**
	 * Called during classpath scanning when class or resource is found.
	 * <li>Class name is java-alike class name (pk1.pk2.class) that may be immediately used
	 * for dynamic loading.
	 * <li>Resource name starts with '\' and represents either jar path (\pk1/pk2/res) or relative file path (\pk1\pk2\res).
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
			throw new FindFileException("Unable to read bytes from input stream.", ioex);
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
					throw new FindFileException("Unable to get input stream for zip file: '" + zipFile.getName()
							+ "', entry: '" + zipEntry.getName() + "'." , ioex);
				}
			}
			try {
				inputStream = new FileInputStream(file);
				return inputStream;
			} catch (FileNotFoundException fnfex) {
				throw new FindFileException("Unable to open file: " + file.getAbsolutePath(), fnfex);
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
