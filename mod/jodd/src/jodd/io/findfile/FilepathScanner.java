// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.io.File;

/**
 * File path scanner.
 */
public abstract class FilepathScanner {

	// ---------------------------------------------------------------- config

	protected boolean recursive;

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * Activates recursive search.
	 */
	public FilepathScanner recursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}


	protected boolean includeDirs = true;

	public boolean isIncludeDirs() {
		return includeDirs;
	}

	public void setIncludeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
	}

	/**
	 * Include directories in search.
	 */
	public FilepathScanner includeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
		return this;
	}



	protected boolean includeFiles = true;

	public boolean isIncludeFiles() {
		return includeFiles;
	}

	public void setIncludeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
	}

	/**
	 * Include files in search.
	 */
	public FilepathScanner includeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
		return this;
	}

	/**
	 * Include files and folders.
	 */
	public FilepathScanner includeFilesAndFolders() {
		includeDirs = true;
		includeFiles = true;
		return this;
	}


	// ---------------------------------------------------------------- scan

	/**
	 * Starts resource scanning.
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
	 * Invoked on some resource.
	 */
	protected abstract void onFile(File file);

}
