// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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
 * todo: Add sorting via comparators
 */
public class FindFile {

	// ---------------------------------------------------------------- flags

	protected boolean recursive;
	protected boolean includeDirs = true;
	protected boolean includeFiles = true;
	protected boolean walking = true;


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

	public boolean isWalking() {
		return walking;
	}

	/**
	 * Sets the walking mode. When walking mode is on (by default),
	 * folders are walked immediately. Although natural, for large
	 * set of files, this is not memory-optimal approach, since many
	 * files are held in memory, when going deeper.
	 * <p>
	 * When walking mode is turned off, folders are processed once
	 * all files are processed, one after the other. The order is
	 * not natural, but memory consumption is optimal.
	 */
	public void setWalking(boolean walking) {
		this.walking = walking;
	}

	// ---------------------------------------------------------------- search path

	/**
	 * Specifies single search path.
	 */
	public FindFile searchPath(File searchPath) {
		addPath(searchPath);
		return this;
	}

	/**
	 * Specifies a set of search paths.
	 */
	public FindFile searchPath(File... searchPath) {
		for (File file : searchPath) {
			addPath(file);
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
				addPath(new File(path));
			}
			return this;
		}
		addPath(new File(searchPath));
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
		addPath(file);
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
		addPath(file);
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

	// ---------------------------------------------------------------- files iterator

	/**
	 * Files iterator simply walks over files array.
	 * Ignores null items. Consumed files are immediately
	 * removed from the array.
	 */
	protected class FilesIterator {
		protected final File folder;
		protected final String[] files;

		public FilesIterator(File folder) {
			this.folder = folder;
			this.files = folder.list();
		}

		public FilesIterator(String[] files) {
			this.folder = null;
			this.files = files;
		}

		protected int index;

		/**
		 * Returns next file or <code>null</code>
		 * when no next file is available.
		 */
		public File next() {
			while (index < files.length) {
				String fileName = files[index];

				if (fileName == null) {
					index++;
					continue;
				}
				files[index] = null;
				index++;

				File file;
				if (folder == null) {
					file = new File(fileName);
				} else {
					file = new File(folder, fileName);
				}

				if (file.isFile()) {
					if (acceptFile(file) == false) {
						continue;
					}
				}

				return file;
			}
			return null;
		}
	}

	/**
	 * Called on each file entry (file or directory) and returns <code>true</code>
	 * if file passes search criteria.
	 */
	protected boolean acceptFile(File file) {
		return true;
	}

	// ---------------------------------------------------------------- next file

	protected LinkedList<File> pathList;
	protected LinkedList<File> pathListOriginal;
	protected LinkedList<File> todoFolders;
	protected LinkedList<FilesIterator> todoFiles;

	protected File lastFile;

	/**
	 * Returns last founded file.
	 * Returns <code>null</code> at the very beginning.
	 */
	public File lastFile() {
		return lastFile;
	}

	/**
	 * Adds existing search path to the file list.
	 * Non existing files are ignored.
	 * If path is a folder, it will be scanned for all files.
	 */
	protected void addPath(File path) {
		if (path.exists() == false) {
			return;
		}
		if (pathList == null) {
			pathList = new LinkedList<File>();
		}

		pathList.add(path);
	}

	/**
	 * Reset the search.
	 */
	public void reset() {
		pathList = pathListOriginal;
		pathListOriginal = null;
		todoFiles = null;
		lastFile = null;
	}

	/**
	 * Finds the next file. Returns founded file that matches search configuration
	 * or <code>null</code> if no more files can be found.
	 */
	public File nextFile() {

		if (todoFiles == null) {
			init();
		}

		while (true) {

			// iterate files

			if (todoFiles.isEmpty() == false) {
				FilesIterator filesIterator = todoFiles.getLast();
				File nextFile = filesIterator.next();

				if (nextFile == null) {
					todoFiles.removeLast();
					continue;
				}

				if (nextFile.isDirectory()) {
					if (walking == false) {
						todoFolders.add(nextFile);
						continue;
					}
					// walking
					if (recursive == true) {
						todoFiles.add(new FilesIterator(nextFile));
					}
					if (includeDirs == true) {
						if (acceptFile(nextFile)) {
							lastFile = nextFile;
							return nextFile;
						}
					}
					continue;
				}

				lastFile = nextFile;
				return nextFile;
			}

			// process folders

			File folder;
			boolean initialDir = false;

			if (todoFolders.isEmpty()) {
				if (pathList.isEmpty()) {
					// the end
					return null;
				}

				folder = pathList.removeFirst();
				initialDir = true;
			} else {
				folder = todoFolders.removeFirst();
			}

			if (recursive == true) {
				todoFiles.add(new FilesIterator(folder));
			}

			if ((!initialDir) && (includeDirs == true)) {
				if (acceptFile(folder)) {
					lastFile = folder;
					return folder;
				}
			}
		}
	}

	/**
	 * Performs scanning.
	 */
	public void scan() {
		while (nextFile() != null) {
		}
	}


	/**
	 * Initializes file walking.
	 * Separates input files and folders.
	 */
	@SuppressWarnings("unchecked")
	protected void init() {
		todoFiles = new LinkedList<FilesIterator>();
		todoFolders = new LinkedList<File>();

		if (pathListOriginal == null) {
			pathListOriginal = (LinkedList<File>) pathList.clone();
		}
		String[] files = new String[pathList.size()];

		int index = 0;
		Iterator<File> iterator = pathList.iterator();
		while (iterator.hasNext()) {
			File file = iterator.next();

			if (file.isFile()) {
				files[index++] = file.getAbsolutePath();
				iterator.remove();
			}
		}

		if (index != 0) {
			FilesIterator filesIterator = new FilesIterator(files);
			todoFiles.add(filesIterator);
		}
	}

	/**
	 * Returns file walking iterator.
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
}
