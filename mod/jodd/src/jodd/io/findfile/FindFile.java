// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.io.FileNameUtil;
import jodd.util.MultipleComparator;
import jodd.util.StringUtil;
import jodd.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Generic iterative file finder. Searches all files on specified search path.
 *
 * @see WildcardFindFile
 * @see RegExpFindFile
 * @see FilterFindFile
 */
public class FindFile {

	/**
	 * Match type.
	 * @see FindFile#getMatchingFilePath(java.io.File)
	 * @see FindFile#acceptFile(java.io.File)
	 */
	public enum Match {
		/**
		 * Full, absolute path.
		 */
		FULL_PATH,
		/**
		 * Relative path from current root.
		 */
		RELATIVE_PATH,
		/**
		 * Just file name.
		 */
		NAME
	}

	// ---------------------------------------------------------------- flags

	protected boolean recursive;
	protected boolean includeDirs = true;
	protected boolean includeFiles = true;
	protected boolean walking = true;
	protected Match matchType = Match.FULL_PATH;


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
	 * Sets the walking recursive mode. When walking mode is on (by default),
	 * folders are walked immediately. Although natural, for large
	 * set of files, this is not memory-optimal approach, since many
	 * files are held in memory, when going deeper.
	 * <p>
	 * When walking mode is turned off, folders are processed once
	 * all files are processed, one after the other. The order is
	 * not natural, but memory consumption is optimal.
	 * @see #setRecursive(boolean)
	 */
	public FindFile setWalking(boolean walking) {
		this.walking = walking;
		return this;
	}

	public Match getMatchType() {
		return matchType;
	}

	/**
	 * Set {@link Match matching type}.
	 */
	public FindFile setMatchType(Match match) {
		this.matchType = match;
		return this;
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
		protected final String[] fileNames;
		protected final File[] files;

		public FilesIterator(File folder) {
			this.folder = folder;
			if (sortComparators != null) {
				this.files = folder.listFiles();

				if ((sortComparators != null) && (this.files != null)) {
					Arrays.sort(this.files, new MultipleComparator<File>(sortComparators));
				}

				this.fileNames = null;
			} else {
				this.files = null;
				this.fileNames = folder.list();
			}
		}

		public FilesIterator(String[] fileNames) {
			this.folder = null;
			if (sortComparators != null) {
				int fileNamesLength = fileNames.length;
				this.files = new File[fileNamesLength];

				for (int i = 0; i < fileNamesLength; i++) {
					String fileName = fileNames[i];
					if (fileName != null) {
						this.files[i] = new File(fileName);
					}

				}
				this.fileNames = null;
			} else {
				this.files = null;
				this.fileNames = fileNames;
			}
		}

		protected int index;

		/**
		 * Returns next file or <code>null</code>
		 * when no next file is available.
		 */
		public File next() {
			if (files != null) {
				return nextFile();
			} else {
				return nextFileName();
			}
		}

		protected File nextFileName() {
			while (index < fileNames.length) {
				String fileName = fileNames[index];

				if (fileName == null) {
					index++;
					continue;
				}
				fileNames[index] = null;
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

		protected File nextFile() {
			while (index < files.length) {
				File file = files[index];

				if (file == null) {
					index++;
					continue;
				}
				files[index] = null;
				index++;

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

	/**
	 * Resolves file path depending on {@link Match matching type}
	 * Returned path is formatted in unix style.
	 */
	protected String getMatchingFilePath(File file) {

		String path = null;

		switch (matchType) {
			case FULL_PATH:
				path = file.getAbsolutePath();
				break;
			case RELATIVE_PATH:
				path = file.getAbsolutePath();
				path = path.substring(rootPath.length());
				break;
			case NAME:
				path = file.getName();
		}

		path = FileNameUtil.separatorsToUnix(path);

		return path;
	}

	// ---------------------------------------------------------------- next file

	protected LinkedList<File> pathList;
	protected LinkedList<File> pathListOriginal;
	protected LinkedList<File> todoFolders;
	protected LinkedList<FilesIterator> todoFiles;

	protected File lastFile;
	protected File rootFile;
	protected String rootPath;

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
	 * Reset the search so it can be run again with very
	 * same parameters (and sorting options).
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

				rootFile = folder;
				rootPath = rootFile.getAbsolutePath();

				initialDir = true;
			} else {
				folder = todoFolders.removeFirst();
			}

			if ((initialDir) || (recursive == true)) {
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

		if (pathList == null) {
			pathList = new LinkedList<File>();
			return;
		}

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

	// ---------------------------------------------------------------- sort

	protected List<Comparator<File>> sortComparators;

	protected void addComparator(Comparator<File> comparator) {
		if (sortComparators == null) {
			sortComparators = new ArrayList<Comparator<File>>(4);
		}
		sortComparators.add(comparator);
	}

	/**
	 * Removes ALL sorting options.
	 */
	public FindFile sortNone() {
		sortComparators = null;
		return this;
	}

	/**
	 * Adds generic sorting.
	 */
	public FindFile sortWith(Comparator<File> fileComparator) {
		addComparator(fileComparator);
		return this;
	}

	/**
	 * Puts folders before files.
	 */
	public FindFile sortFoldersFirst() {
		addComparator(new FolderFirstComparator(true));
		return this;
	}

	/**
	 * Puts files before folders.
	 */
	public FindFile sortFoldersLast() {
		addComparator(new FolderFirstComparator(false));
		return this;
	}

	/**
	 * Sorts files by file name.
	 */
	public FindFile sortByName() {
		addComparator(new FileNameComparator(true));
		return this;
	}

	/**
	 * Sorts files by file names descending.
	 */
	public FindFile sortByNameDesc() {
		addComparator(new FileNameComparator(false));
		return this;
	}

	/**
	 * Sorts files by file extension.
	 */
	public FindFile sortByExtension() {
		addComparator(new FileExtensionComparator(true));
		return this;
	}

	/**
	 * Sorts files by file extension descending.
	 */
	public FindFile sortByExtensionDesc() {
		addComparator(new FileExtensionComparator(false));
		return this;
	}

	/**
	 * Sorts files by last modified time.
	 */
	public FindFile sortByTime() {
		addComparator(new FileLastModifiedTimeComparator(true));
		return this;
	}

	/**
	 * Sorts files by last modified time descending.
	 */
	public FindFile sortByTimeDesc() {
		addComparator(new FileLastModifiedTimeComparator(false));
		return this;
	}

	// ---------------------------------------------------------------- comparators

	public static class FolderFirstComparator implements Comparator<File> {

		protected final int order;

		public FolderFirstComparator(boolean foldersFirst) {
			if (foldersFirst) {
				order = 1;
			} else {
				order = -1;
			}
		}

		public int compare(File file1, File file2) {
			if (file1.isFile() && file2.isDirectory()) {
				return order;
			}
			if (file1.isDirectory() && file2.isFile()) {
				return -order;
			}
			return 0;
		}
	}

	public static class FileNameComparator implements Comparator<File> {

		protected final int order;

		public FileNameComparator(boolean ascending) {
			if (ascending) {
				order = 1;
			} else {
				order = -1;
			}
		}

		public int compare(File file1, File file2) {
			long diff = file1.getName().compareToIgnoreCase(file2.getName());
			if (diff == 0) {
				return 0;
			}
			if (diff > 0) {
				return order;
			}
			return -order;
		}
	}

	public static class FileExtensionComparator implements Comparator<File> {

		protected final int order;

		public FileExtensionComparator(boolean ascending) {
			if (ascending) {
				order = 1;
			} else {
				order = -1;
			}
		}

		public int compare(File file1, File file2) {
			String ext1 = FileNameUtil.getExtension(file1.getName());
			String ext2 = FileNameUtil.getExtension(file2.getName());
			long diff = ext1.compareToIgnoreCase(ext2);
			if (diff == 0) {
				return 0;
			}
			if (diff > 0) {
				return order;
			}
			return -order;
		}
	}

	public static class FileLastModifiedTimeComparator implements Comparator<File> {

		protected final int order;

		public FileLastModifiedTimeComparator(boolean ascending) {
			if (ascending) {
				order = 1;
			} else {
				order = -1;
			}
		}

		public int compare(File file1, File file2) {
			long diff = file1.lastModified() - file2.lastModified();
			if (diff == 0) {
				return 0;
			}
			if (diff > 0) {
				return order;
			}
			return -order;
		}
	}
}
