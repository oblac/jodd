// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.io.File;

/**
 * File scanner is a 'callback' type of files scanner.
 * For each founded file, method {@link #onFile(java.io.File)}
 * is invoked.
 */
public abstract class FileScanner {

	// ---------------------------------------------------------------- config

	protected boolean recursive;
	protected boolean includeDirs = true;
	protected boolean includeFiles = true;

	public boolean isRecursive() {
		return recursive;
	}

	/**
	 * Activates recursive search.
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public boolean isIncludeDirs() {
		return includeDirs;
	}

	/**
	 * Include directories in search.
	 */
	public void setIncludeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
	}


	public boolean isIncludeFiles() {
		return includeFiles;
	}

	/**
	 * Include files in search.
	 */
	public void setIncludeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
	}

	// ---------------------------------------------------------------- scan

	/**
	 * Starts files scanning for given destination root.
	 */
	public void scan(File root) {
		File[] allFiles = root.listFiles();
		if (allFiles == null) {
			return;
		}
		for(File file: allFiles) {
			process(file);
		}
	}

	/**
	 * Starts files scanning for given destination root.
	 */
	public void scan(String string) {
		scan(new File(string));
	}

	// ---------------------------------------------------------------- process

	/**
	 * Processes single path.
	 */
	protected void process(File file) {
		boolean isFolder = file.isDirectory();
		if (isFolder) {
			if (includeDirs == true) {
				onFile(file);
			}
			if (recursive && acceptFolder(file)) {
				scan(file);
			}
			return;
		}
		if (includeFiles == true) {
			onFile(file);
		}
	}

	/**
	 * Returns <code>true</code> if some path has to be scanned.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	protected boolean acceptFolder(File file) {
		return true;
	}

	/**
	 * Invoked on founded file.
	 */
	protected abstract void onFile(File file);

}
