// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import jodd.io.FileNameUtil;
import jodd.util.FastSort;
import jodd.util.MultiComparator;
import jodd.util.NaturalOrderComparator;
import jodd.util.StringUtil;
import jodd.io.FileUtil;
import jodd.util.collection.JoddArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Generic iterative file finder. Searches all files on specified search path.
 *
 * @see WildcardFindFile
 * @see RegExpFindFile
 */
@SuppressWarnings("unchecked")
public class FindFile<T extends FindFile> {

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
	public T setRecursive(boolean recursive) {
		this.recursive = recursive;
		return (T) this;
	}

	public boolean isIncludeDirs() {
		return includeDirs;
	}

	/**
	 * Include directories in search.
	 */
	public T setIncludeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
		return (T) this;
	}

	public boolean isIncludeFiles() {
		return includeFiles;
	}

	/**
	 * Include files in search.
	 */
	public T setIncludeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
		return (T) this;
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
	public T setWalking(boolean walking) {
		this.walking = walking;
		return (T) this;
	}

	public Match getMatchType() {
		return matchType;
	}

	/**
	 * Set {@link Match matching type}.
	 */
	public T setMatchType(Match match) {
		this.matchType = match;
		return (T) this;
	}

	// ---------------------------------------------------------------- search path

	/**
	 * Specifies single search path.
	 */
	public T searchPath(File searchPath) {
		addPath(searchPath);
		return (T) this;
	}

	/**
	 * Specifies a set of search paths.
	 */
	public T searchPath(File... searchPath) {
		for (File file : searchPath) {
			addPath(file);
		}
		return (T) this;
	}

	/**
	 * Specifies the search path. If provided path contains
	 * {@link File#pathSeparator} than string will be tokenized
	 * and each part will be added separately as a search path. 
	 */
	public T searchPath(String searchPath) {
		if (searchPath.indexOf(File.pathSeparatorChar) != -1) {
			String[] paths = StringUtil.split(searchPath, File.pathSeparator);
			for (String path : paths) {
				addPath(new File(path));
			}
		} else {
			addPath(new File(searchPath));
		}
		return (T) this;
	}

	/**
	 * Specifies search paths.
	 * @see #searchPath(String) 
	 */
	public T searchPath(String... searchPaths) {
		for (String searchPath : searchPaths) {
			searchPath(searchPath);
		}
		return (T) this;
	}

	/**
	 * Specifies the search path. Throws an exception if URI is invalid.
	 */
	public T searchPath(URI searchPath) {
		File file;
		try {
			file = new File(searchPath);
		} catch (Exception ex) {
			throw new FindFileException("Invalid URI: " + searchPath, ex);
		}

		addPath(file);

		return (T) this;
	}

	/**
	 * Specifies the search path.
	 */
	public T searchPath(URI... searchPath) {
		for (URI uri : searchPath) {
			searchPath(uri);
		}
		return (T) this;
	}

	/**
	 * Specifies the search path. Throws an exception if URL is invalid.
	 */
	public T searchPath(URL searchPath) {
		File file = FileUtil.toFile(searchPath);
		if (file == null) {
			throw new FindFileException("Invalid URL: " + searchPath);
		}
		addPath(file);
		return (T) this;
	}

	/**
	 * Specifies the search path.
	 */
	public T searchPath(URL... searchPath) {
		for (URL url : searchPath) {
			searchPath(url);
		}
		return (T) this;
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

				if (this.files != null) {
					FastSort.sort(this.files, new MultiComparator<File>(sortComparators));
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
			} else if (fileNames != null) {
				return nextFileName();
			} else {
				return null;
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
					if (includeFiles == false) {
						continue;
					}
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
					if (includeFiles == false) {
						continue;
					}
					if (acceptFile(file) == false) {
						continue;
					}
				}

				return file;
			}
			return null;
		}
	}

	// ---------------------------------------------------------------- matching

	protected ArrayList<String> includes;
	protected ArrayList<String> excludes;

	/**
	 * Defines include pattern.
	 */
	public T include(String pattern) {
		if (includes == null) {
			includes = new ArrayList<String>();
		}
		includes.add(pattern);
		return (T) this;
	}

	/**
	 * Defines include patterns.
	 */
	public T include(String... patterns) {
		if (includes == null) {
			includes = new ArrayList<String>();
		}
		Collections.addAll(includes, patterns);
		return (T) this;
	}

	/**
	 * Defines exclude pattern.
	 */
	public T exclude(String pattern) {
		if (excludes == null) {
			excludes = new ArrayList<String>();
		}
		excludes.add(pattern);
		return (T) this;
	}

	/**
	 * Defines exclude patterns.
	 */
	public T exclude(String... patterns) {
		if (excludes == null) {
			excludes = new ArrayList<String>();
		}
		Collections.addAll(excludes, patterns);
		return (T) this;
	}

	/**
	 * Determine if file is accepted, based on include and exclude
	 * rules. Called on each file entry (file or directory) and
	 * returns <code>true</code> if file passes search criteria.
	 * If exclude rules exist, file is matched against them.
	 * If file matches one of the exclude rules, it will not be accepted.
	 * Then the file matches includes rules, if they exist.
	 * If file matches one of the includes rules, it will be accepted,
	 * otherwise it will be rejected.
	 * File is matched using {@link #getMatchingFilePath(java.io.File) matching file path}.
	 */
	protected boolean acceptFile(File file) {
		String matchingFilePath = getMatchingFilePath(file);

		if (excludes != null) {
			for (String pattern : excludes) {
				if (match(matchingFilePath, pattern) == true) {
					return false;
				}
			}
		}

		if (includes != null) {
			for (String pattern : includes) {
				if (match(matchingFilePath, pattern) == true) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Defines matching logic for given pattern. Default implementation
	 * simple checks if {@link #getMatchingFilePath(java.io.File) matching file name}
	 * is equal to some pattern. Various implementation may define different matching logic.
	 * @param path matched file path
	 * @param pattern pattern to match against
	 *
	 * @see #getMatchingFilePath(java.io.File)
	 * @see #acceptFile(java.io.File)
	 */
	protected boolean match(String path, String pattern) {
		return path.equals(pattern);
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

	protected JoddArrayList<File> pathList;
	protected JoddArrayList<File> pathListOriginal;
	protected JoddArrayList<File> todoFolders;
	protected JoddArrayList<FilesIterator> todoFiles;

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
			pathList = new JoddArrayList<File>();
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
		includes = null;
		excludes = null;
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
		todoFiles = new JoddArrayList<FilesIterator>();
		todoFolders = new JoddArrayList<File>();

		if (pathList == null) {
			pathList = new JoddArrayList<File>();
			return;
		}

		if (pathListOriginal == null) {
			pathListOriginal = (JoddArrayList<File>) pathList.clone();
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
	public T sortNone() {
		sortComparators = null;
		return (T) this;
	}

	/**
	 * Adds generic sorting.
	 */
	public T sortWith(Comparator<File> fileComparator) {
		addComparator(fileComparator);
		return (T) this;
	}

	/**
	 * Puts folders before files.
	 */
	public T sortFoldersFirst() {
		addComparator(new FolderFirstComparator(true));
		return (T) this;
	}

	/**
	 * Puts files before folders.
	 */
	public T sortFoldersLast() {
		addComparator(new FolderFirstComparator(false));
		return (T) this;
	}

	/**
	 * Sorts files by file name, using <b>natural</b> sort.
	 */
	public T sortByName() {
		addComparator(new FileNameComparator(true));
		return (T) this;
	}

	/**
	 * Sorts files by file names descending, using <b>natural</b> sort.
	 */
	public T sortByNameDesc() {
		addComparator(new FileNameComparator(false));
		return (T) this;
	}

	/**
	 * Sorts files by file extension.
	 */
	public T sortByExtension() {
		addComparator(new FileExtensionComparator(true));
		return (T) this;
	}

	/**
	 * Sorts files by file extension descending.
	 */
	public T sortByExtensionDesc() {
		addComparator(new FileExtensionComparator(false));
		return (T) this;
	}

	/**
	 * Sorts files by last modified time.
	 */
	public T sortByTime() {
		addComparator(new FileLastModifiedTimeComparator(true));
		return (T) this;
	}

	/**
	 * Sorts files by last modified time descending.
	 */
	public T sortByTimeDesc() {
		addComparator(new FileLastModifiedTimeComparator(false));
		return (T) this;
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
		protected NaturalOrderComparator<String> naturalOrderComparator = new NaturalOrderComparator<String>(true);

		public FileNameComparator(boolean ascending) {
			if (ascending) {
				order = 1;
			} else {
				order = -1;
			}
		}

		public int compare(File file1, File file2) {
			int result = naturalOrderComparator.compare(file1.getName(), file2.getName());
			if (result == 0) {
				return result;
			}
			if (result > 0) {
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
