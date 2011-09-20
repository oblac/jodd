// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.util.StringUtil;
import jodd.io.FileUtil;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;

/**
 * Generic iterative file finder. Searches all files on specified search path.
 *
 * @see WildcardFindFile
 * @see RegExpFindFile
 * @see FilterFindFile
 *
 * todo: Add sorting and comparators
 */
public class FindFile {

	// ---------------------------------------------------------------- flags

	protected boolean recursive;

	public boolean isRecursive() {
		return recursive;
	}

	/**
	 * Activates recursive search.
	 */
	public FindFile setRecursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}


	protected boolean includeDirs = true;

	public boolean isIncludeDirs() {
		return includeDirs;
	}

	/**
	 * Include directories in search.
	 */
	public FindFile setIncludeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
		return this;
	}


	protected boolean includeFiles = true;

	public boolean isIncludeFiles() {
		return includeFiles;
	}

	/**
	 * Include files in search.
	 */
	public FindFile setIncludeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
		return this;
	}


	// ---------------------------------------------------------------- search path

	protected LinkedList<File> fileList;

	/**
	 * Adds existing search path to the file list.
	 * If path is a folder, it will be scanned for all files.
	 */
	protected void addSearchPath(File searchPath) {
		if (searchPath.exists() == false) {
			return;
		}
		if (fileList == null) {
			fileList = new LinkedList<File>();
		}
		if (searchPath.isDirectory() == false) {
			fileList.add(searchPath);
			return;
		}
		listFiles(searchPath);
	}

	/**
	 * Specifies single search path.
	 */
	public FindFile searchPath(File searchPath) {
		addSearchPath(searchPath);
		return this;
	}

	/**
	 * Specifies a set of search paths.
	 */
	public FindFile searchPath(File... searchPath) {
		for (File file : searchPath) {
			addSearchPath(file);
		}
		return this;
	}

	/**
	 * Specifies the search path. If provided path contains
	 * {@link File#pathSeparator} than string will be tokenized
	 * and each part will be added separately as a search path. 
	 */
	public FindFile searchPath(String searchPath) {
		if (searchPath.indexOf(File.pathSeparatorChar) != -1) {
			String[] paths = StringUtil.split(searchPath, File.pathSeparator);
			for (String path : paths) {
				addSearchPath(new File(path));
			}
			return this;
		}
		addSearchPath(new File(searchPath));
		return this;
	}

	/**
	 * Specifies search paths.
	 * @see #searchPath(String) 
	 */
	public FindFile searchPath(String... searchPaths) {
		for (String searchPath : searchPaths) {
			searchPath(searchPath);
		}
		return this;
	}

	/**
	 * Specifies the search path. Throws an exception if URI is invalid.
	 */
	public FindFile searchPath(URI searchPath) {
		File file = FileUtil.toFile(searchPath);
		if (file == null) {
			throw new FindFileException("Invalid search path URI: " + searchPath);
		}
		addSearchPath(file);
		return this;
	}

	/**
	 * Specifies the search path.
	 */
	public FindFile searchPath(URI... searchPath) {
		for (URI uri : searchPath) {
			searchPath(uri);
		}
		return this;
	}

	/**
	 * Specifies the search path. Throws an exception if URL is invalid.
	 */
	public FindFile searchPath(URL searchPath) {
		File file = FileUtil.toFile(searchPath);
		if (file == null) {
			throw new FindFileException("Invalid search path URL: " + searchPath);
		}
		addSearchPath(file);
		return this;
	}

	/**
	 * Specifies the search path.
	 */
	public FindFile searchPath(URL... searchPath) {
		for (URL url : searchPath) {
			searchPath(url);
		}
		return this;
	}

	// ---------------------------------------------------------------- types

	protected boolean listSubfilesAfterFolder = true;

	public boolean isListSubfilesAfterFolder() {
		return listSubfilesAfterFolder;
	}

	/**
	 * If set to <code>true</code> then all subfiles of a folder will be listed
	 * directly after the folder, while folder will be listed first. Otherwise,
	 * sub files will be listed after the all files of current folder.
	 */
	public FindFile setListSubfilesAfterFolder(boolean listSubfilesAfterFolder) {
		this.listSubfilesAfterFolder = listSubfilesAfterFolder;
		return this;
	}

	// ---------------------------------------------------------------- next file

	/**
	 * Finds the next file. Returns founded file that matches search configuration
	 * or <code>null</code> if no more files can be found.
	 */
	public File nextFile() {
		if (fileList == null) {
			return null;
		}

		while (true) {
			if (fileList.isEmpty()) {
				fileList = null;
				return null;
			}
			File currentFile = fileList.removeFirst();
			if (currentFile.isDirectory()) {
				if (recursive == true) {
					listFiles(currentFile);
				}
				if (includeDirs == true) {
					if (acceptFile(currentFile) == true) {
						return currentFile;
					}
				}
				continue;
			}
			return currentFile;
		}
	}

	/**
	 * Returns file walker iterator.
	 */
	public Iterator<File> iterator() {

		return new Iterator<File>() {
			private File nextFile;
			public boolean hasNext() {
				nextFile = nextFile();
				return nextFile != null;
			}

			public File next() {
				if (nextFile == null) {
					throw new NoSuchElementException();
				}
				return nextFile;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}



	// ---------------------------------------------------------------- list files

	/**
	 * List all files and folders in specified directory.
	 * <b>All</b> folders are added to the list (because of possible recursion).
	 * Opposite, files are filtered first and added if matched.
	 */
	protected void listFiles(File directory) {
		File[] list = directory.listFiles();
		LinkedList<File> subFolders;
		LinkedList<File> subFiles;
		if (listSubfilesAfterFolder == false) {
			subFolders = fileList;
			subFiles = fileList;
		} else {
			subFolders = new LinkedList<File>();
			subFiles = new LinkedList<File>();
		}
		for (File currentFile : list) {
			if (currentFile.isFile() == true) {
				if ((includeFiles == true) && (acceptFile(currentFile) == true)) {
					subFiles.addLast(currentFile);
				}
			} else if (currentFile.isDirectory() == true) {
				subFolders.addLast(currentFile);
			}
		}
		if (listSubfilesAfterFolder == true) {
			if (subFiles.isEmpty() == false) {
				fileList.addAll(0, subFiles);
			}
			if (subFolders.isEmpty() == false) {
				fileList.addAll(0, subFolders);
			}
		}
	}

	// ---------------------------------------------------------------- callback

	/**
	 * Called on each file entry (file or directory) and returns <code>true</code>
	 * if file passes search criteria.
	 */
	protected boolean acceptFile(File currentFile) {
		return true;
	}
}
