// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.StringUtil;
import jodd.util.Wildcard;
import jodd.util.ArraysUtil;
import jodd.util.Provider;
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
 * Jar files are also examined.
 * @see jodd.io.findfile.ClasspathScanner
 */
public abstract class FindClass {

	private static final String CLASS_FILE_EXT = ".class";
	private static final String JAR_FILE_EXT = ".jar";
	private final InputStreamProvider inputStreamProvider;

	protected FindClass() {
		inputStreamProvider = new InputStreamProvider();
	}

	// ---------------------------------------------------------------- excluded jars

	/**
	 * Array of jar file name patterns that are excluded from the search.
	 * By default java runtime libraries are excluded.
	 */
	protected String[] excludedJars = new String[] {
			"*/jre/lib/*.jar",
			"*/jre/lib/ext/*.jar",
			"*/tools.jar",
			"*/j2ee.jar"
	};


	public String[] getExcludedJars() {
		return excludedJars;
	}

	public void setExcludedJars(String[] excludedJars) {
		this.excludedJars = excludedJars;
	}

	/**
	 * Array of jar file name patterns that are included in the search.
	 * This rule is applied after the excluded rule.
	 */
	protected String[] includedJars;

	public String[] getIncludedJars() {
		return includedJars;
	}

	public void setIncludedJars(String[] includedJars) {
		this.includedJars = includedJars;
	}

	// ---------------------------------------------------------------- included packages

	protected String[] includedEntries;    // array of included name patterns

	public String[] getIncludedEntries() {
		return includedEntries;
	}

	/**
	 * Sets included set of names that will be considered during configuration,
	 */
	public void setIncludedEntries(String[] includedEntries) {
		this.includedEntries = includedEntries;
	}


	protected String[] excludedEntries;    // array of excluded name patterns

	public String[] getExcludedEntries() {
		return excludedEntries;
	}

	/**
	 * Sets excluded names that narrows included set of packages.
	 */
	public void setExcludedEntries(String[] excludedEntries) {
		this.excludedEntries = excludedEntries;
	}

	// ---------------------------------------------------------------- implementation

	/**
	 * If set to <code>true</code> all files will be scanned and not only classes.
	 */
	protected boolean includeResources;


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
	 * Callback {@link #onEntryName(String, InputStreamProvider)} is called on
	 * each class name.
	 */
	protected void scanUrl(URL url) {
		File file = FileUtil.toFile(url);
		if (file == null) {
			throw new FindFileException("URL is not a valid file: '" + url + "'.");
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
	 * Scans single path.
	 */
	protected void scanPath(File file) {
		String pathString = file.toString();
		if (StringUtil.endsWithIgnoreCase(pathString, JAR_FILE_EXT) == true) {
			if (excludedJars != null) {
				if (Wildcard.matchOne(pathString, excludedJars) != -1) {
					return;
				}
			}
			if (includedJars != null) {
				if (Wildcard.matchOne(pathString, includedJars) == -1) {
					return;
				}
			}
			scanJarFile(file);
		} else if (file.isDirectory() == true) {
			scanClassPath(file);
		}
	}

	// ---------------------------------------------------------------- internal

	/**
	 * Scans classes inside single JAR archive. Archive is scanned as a zip file.
	 * @see #onEntryName(String, InputStreamProvider)
	 */
	protected void scanJarFile(File file) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				String zipEntryName = zipEntry.getName();
				if (StringUtil.endsWithIgnoreCase(zipEntryName, CLASS_FILE_EXT)) {
					inputStreamProvider.init(zipFile, zipEntry);
					try {
						scanClassName(zipEntryName, true);
					} finally {
						inputStreamProvider.closeInputStreamIfOpen();
					}
				} else if (includeResources == true) {
					inputStreamProvider.init(zipFile, zipEntry);
					try {
						scanClassName(zipEntryName, false);
					} finally {
						inputStreamProvider.closeInputStreamIfOpen();
					}
				}
			}
		} catch (IOException ioex) {
			throw new FindFileException("Unable to work with zip file '" + file.getName() + "'.", ioex);
		} finally {
			ZipUtil.close(zipFile);
		}
	}

	/**
	 * Scans single classpath directory.
	 * @see #onEntryName(String, InputStreamProvider)
	 */
	protected void scanClassPath(File root) {
		String rootPath = root.getAbsolutePath();
		if (rootPath.endsWith(File.separator) == false) {
			rootPath += File.separatorChar;
		}

		FindFile ff = new FindFile().includeDirs(false).recursive(true).searchPath(rootPath);
		File file;
		while ((file = ff.nextFile()) != null) {
			String filePath = file.getAbsolutePath();
			if (StringUtil.endsWithIgnoreCase(filePath, CLASS_FILE_EXT)) {
				scanClassFile(filePath, rootPath, file, true);
			} else if (includeResources == true) {
				scanClassFile(filePath, rootPath, file, false);
			}
		}
	}

	private void scanClassFile(String filePath, String rootPath, File file, boolean isClass) {
		if (StringUtil.startsWithIgnoreCase(filePath, rootPath) == true) {
			inputStreamProvider.init(file);
			try {
				scanClassName(filePath.substring(rootPath.length()), isClass);
			} finally {
				inputStreamProvider.closeInputStreamIfOpen();
			}
		}
	}


	/**
	 * Scans class name and calls {@link #onEntryName(String, InputStreamProvider)} callback.
	 * Strips '.class' from the end and converts all slashes to dots.
	 */
	protected void scanClassName(String name, boolean isClass) {
		String className = name;
		if (isClass) {
			className = name.substring(0, name.length() - 6);		// 6 == ".class".length()
			className = StringUtil.replaceChar(className, '/', '.');
			className = StringUtil.replaceChar(className, '\\', '.');
		} else {
			className = '\\' + className;
		}

		if (excludedEntries != null) {
			if (Wildcard.matchOne(className, excludedEntries) != -1) {
				return;
			}
		}
		if (includedEntries != null) {
			if (Wildcard.matchOne(className, includedEntries) == -1) {
				return;
			}
		}
		try {
			onEntryName(className, inputStreamProvider);
		} catch (Exception ex) {
			throw new FindFileException("Unable to scan class: '" + name + "'.", ex);
		}
	}


	// ---------------------------------------------------------------- callback

	/**
	 * Called during classpath scanning when class or resource is found.
	 * <li>Class name is java-alike class name (pk1.pk2.class) that may be immediately used
	 * for dynamic loading.
	 * <li>Resouce name starts with '\' and represents either jar path (\pk1/pk2/res) or relative file path (\pk1\pk2\res).
     * <code>InputStream</code> is provided by InputStreamProvider and opened lazy.
	 * Once opened, input stream doesn't have to be closed - this is done by this class anyway.
	 */
	protected abstract void onEntryName(String entryName, InputStreamProvider inputStreamProvider) throws Exception;

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
	protected static class InputStreamProvider implements Provider<InputStream> {

		private File file;
		private ZipFile zipFile;
		private ZipEntry zipEntry;

		void init(ZipFile zipFile, ZipEntry zipEntry) {
			this.zipFile = zipFile;
			this.zipEntry = zipEntry;
			this.file = null;
			inputStream = null;
		}
		void init(File file) {
			this.file = file;
			this.zipEntry = null;
			this.zipFile = null;
			inputStream = null;
		}

		private InputStream inputStream;

		/**
		 * Opens zip entry or plain file and returns its input stream.
		 */
		public InputStream get() {
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
				throw new FindFileException("Unable to open file: '" + file.getName() + "'.", fnfex);
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
	}
}
