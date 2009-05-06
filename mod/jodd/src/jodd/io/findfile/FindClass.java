// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

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

/**
 * Simple utility that scans <code>URL</code>s for classes.
 * Its purpose is to help scanning class paths for some classes.
 * Jar files are also examined.
 */
public abstract class FindClass {

	private static final String CLASS_FILE_EXT = ".class";
	private static final String JAR_FILE_EXT = ".jar";

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

	protected String[] includedPackages;    // array of included package name patterns

	public String[] getIncludedPackages() {
		return includedPackages;
	}

	/**
	 * Sets included set of packages that will be considered during configuration,   
	 */
	public void setIncludedPackages(String[] includedPackages) {
		this.includedPackages = includedPackages;
	}


	protected String[] excludedPackages;    // array of excluded package name patterns

	public String[] getExcludedPackages() {
		return excludedPackages;
	}

	/**
	 * Sets excluded packages that narrows included set of packages.
	 */
	public void setExcludedPackages(String[] excludedPackages) {
		this.excludedPackages = excludedPackages;
	}

	// ---------------------------------------------------------------- implementation

	/**
	 * If set to <code>true</code>, input stream will be opened for each file. 
	 */
	protected boolean createInputStream;

	/**
	 * If set to <code>true</code> all exceptions per {@link #scanUrls(java.net.URL[]) single url scanning}
	 * will be ignored.
	 */
	protected boolean ignoreExceptions;


	/**
	 * Scans several URLs. If (#ignoreExceptions} is set, exceptions
	 * per one URL will be ignored and loops continues. 
	 */
	protected void scanUrls(URL... urls) throws Exception {
		for (URL path : urls) {
			try {
				scanUrl(path);
			} catch (Exception ex) {
				if (ignoreExceptions == false) {
					throw ex;
				}
			}
		}
	}
	
	/**
	 * Scans single URL for classes and jar files.
	 * Callback {@link #onClassName(String, java.io.InputStream)} is called on
	 * each class name.
	 */
	protected void scanUrl(URL url) throws Exception {
		File file = FileUtil.toFile(url);
		if (file == null) {
			throw new IOException("URL is not a valid file: '" + url + "'.");
		}
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

	/**
	 * Scans classes inside single JAR archive. Archive is scanned as a zip file.
	 * @see #onClassName(String, java.io.InputStream)
	 */
	protected void scanJarFile(File file) throws Exception {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				String zipEntryName = zipEntry.getName();
				if (StringUtil.endsWithIgnoreCase(zipEntryName, CLASS_FILE_EXT)) {
					scanClassName(zipEntryName, createInputStream == true ? zipFile.getInputStream(zipEntry) : null);
				}
			}
		} finally {
			ZipUtil.close(zipFile);
		}
	}

	/**
	 * Scans single classpath directory.
	 * @see #onClassName(String, java.io.InputStream)  
	 */
	protected void scanClassPath(File root) throws Exception {
		//noinspection NonConstantStringShouldBeStringBuffer
		String rootPath = root.getAbsolutePath();
		if (rootPath.endsWith(File.separator) == false) {
			rootPath += File.separatorChar;
		}

		FindFile ff = new FindFile().includeDirs(false).recursive(true).searchPath(rootPath);
		File file;
		while ((file = ff.nextFile()) != null) {
			String filePath = file.getAbsolutePath();
			if (StringUtil.endsWithIgnoreCase(filePath, CLASS_FILE_EXT)) {
				if (StringUtil.startsWithIgnoreCase(filePath, rootPath) == true) {
					InputStream is = null;
					try {
						is = createInputStream == true ? new FileInputStream(file) : null;
						scanClassName(filePath.substring(rootPath.length()), is);
					} finally {
						StreamUtil.close(is);
					}
				}
			}
		}
	}


	/**
	 * Scans class name and calls {@link #onClassName(String, java.io.InputStream)} callback.
	 * Strips '.class' from the end and converts all slashes to dots.
	 */
	protected void scanClassName(String name, InputStream inputStream) throws Exception {
		name = name.substring(0, name.length() - 6);
		name = StringUtil.replaceChar(name, '/', '.');
		name = StringUtil.replaceChar(name, '\\', '.');

		if (includedPackages != null) {
			if (Wildcard.matchOne(name, includedPackages) == -1) {
				return;
			}
			if (excludedPackages != null) {
				if (Wildcard.matchOne(name, excludedPackages) != -1) {
					return;
				}
			}
		}
		try {
			onClassName(name, inputStream);
		} catch (Throwable th) {
			throw new Exception("Unable to scan class: '" + name + "'.", th);
		}
	}


	// ---------------------------------------------------------------- callback

	/**
	 * Called during scanning when class has been found. Provided class name is java-alike
	 * class name that may be immediately used for dynamic loading.
	 * <code>InputStream</code> is available only when {@link #createInputStream} is set
	 * to <code>true</code> and may be used for reading class content for any reason.
	 */
	protected abstract void onClassName(String className, InputStream inputStream) throws Exception;

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
	protected boolean isTypeSignatureInUse(InputStream inputStream, byte[] bytes) throws IOException {
		byte[] data = StreamUtil.readBytes(inputStream);
		int index = ArraysUtil.indexOf(data, bytes);
		return index != -1;
	}
}
