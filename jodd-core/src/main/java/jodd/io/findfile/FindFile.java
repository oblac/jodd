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

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.util.InExRules;
import jodd.util.MultiComparator;
import jodd.util.NaturalOrderComparator;
import jodd.util.StringUtil;
import jodd.util.collection.JoddArrayList;

/**
 * Generic iterative file finder. Searches all files on specified search path.
 * By default, it starts in whitelist mode, where everything is excluded.
 * To search, you need to explicitly set include patterns. If no pattern is
 * set, then the search starts in blacklist mode, where everything is included (search all).
 *
 * @see WildcardFindFile
 * @see RegExpFindFile
 * @see jodd.util.InExRules
 */
public class FindFile implements Iterable<File> {

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

	public boolean recursive() {
		return recursive;
	}

	/**
	 * Activates recursive search.
	 */
	public FindFile recursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}

	public boolean includeDirs() {
		return includeDirs;
	}

	/**
	 * Include directories in search.
	 */
	public FindFile includeDirs(boolean includeDirs) {
		this.includeDirs = includeDirs;
		return this;
	}

	public boolean includeFiles() {
		return includeFiles;
	}

	/**
	 * Include files in search.
	 */
	public FindFile includeFiles(boolean includeFiles) {
		this.includeFiles = includeFiles;
		return this;
	}

	public boolean walking() {
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
	 * @see #recursive(boolean)
	 */
	public FindFile walking(boolean walking) {
		this.walking = walking;
		return this;
	}

	public Match matchType() {
		return matchType;
	}

	/**
	 * Set {@link Match matching type}.
	 */
	public FindFile matchType(Match match) {
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
		} else {
			addPath(new File(searchPath));
		}
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
		File file;
		try {
			file = new File(searchPath);
		} catch (Exception ex) {
			throw new FindFileException("URI error: " + searchPath, ex);
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
			throw new FindFileException("URL error: " + searchPath);
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

				if (this.files != null) {
					Arrays.sort(this.files, new MultiComparator<>(sortComparators));
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
					if (!includeFiles) {
						continue;
					}
					if (!acceptFile(file)) {
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
					if (!includeFiles) {
						continue;
					}
					if (!acceptFile(file)) {
						continue;
					}
				}

				return file;
			}
			return null;
		}
	}

	// ---------------------------------------------------------------- matching

	protected final InExRules<String, String> rules = createRulesEngine();

	/**
	 * Creates rule engine.
	 */
	protected InExRules<String, String> createRulesEngine() {
		return new InExRules<>();
	}

	/**
	 * Defines include pattern.
	 */
	public FindFile include(String pattern) {
		rules.include(pattern);
		return this;
	}

	/**
	 * Defines include patterns.
	 */
	public FindFile include(String... patterns) {
		for (String pattern : patterns) {
			rules.include(pattern);
		}
		return this;
	}

	/**
	 * Enables whitelist mode.
	 */
	public FindFile excludeAll() {
		rules.whitelist();
		return this;
	}

	/**
	 * Enables blacklist mode.
	 */
	public FindFile includeAll() {
		rules.blacklist();
		return this;
	}

	/**
	 * Defines exclude pattern.
	 */
	public FindFile exclude(String pattern) {
		rules.exclude(pattern);
		return this;
	}

	/**
	 * Defines exclude patterns.
	 */
	public FindFile exclude(String... patterns) {
		for (String pattern : patterns) {
			rules.exclude(pattern);
		}
		return this;
	}

	/**
	 * Determine if file is accepted, based on include and exclude
	 * rules. Called on each file entry (file or directory) and
	 * returns <code>true</code> if file passes search criteria.
	 * File is matched using {@link #getMatchingFilePath(java.io.File) matching file path}.
	 * @see jodd.util.InExRules
	 */
	protected boolean acceptFile(File file) {
		String matchingFilePath = getMatchingFilePath(file);

		return rules.match(matchingFilePath);
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
		if (!path.exists()) {
			return;
		}
		if (pathList == null) {
			pathList = new JoddArrayList<>();
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
		rules.reset();
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

			if (!todoFiles.isEmpty()) {
				FilesIterator filesIterator = todoFiles.getLast();
				File nextFile = filesIterator.next();

				if (nextFile == null) {
					todoFiles.removeLast();
					continue;
				}

				if (nextFile.isDirectory()) {
					if (!walking) {
						todoFolders.add(nextFile);
						continue;
					}
					// walking
					if (recursive) {
						todoFiles.add(new FilesIterator(nextFile));
					}
					if (includeDirs) {
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

			if ((initialDir) || (recursive)) {
				todoFiles.add(new FilesIterator(folder));
			}

			if ((!initialDir) && (includeDirs)) {
				if (acceptFile(folder)) {
					lastFile = folder;
					return folder;
				}
			}
		}
	}

	/**
	 * Finds all files and returns list of founded files.
	 */
	public List<File> findAll() {
		List<File> allFiles = new ArrayList<>();
		File file;
		while ((file = nextFile()) != null) {
			allFiles.add(file);
		}
		return allFiles;
	}

	/**
	 * Initializes file walking.
	 * Separates input files and folders.
	 */
	protected void init() {
		rules.smartMode();

		todoFiles = new JoddArrayList<>();
		todoFolders = new JoddArrayList<>();

		if (pathList == null) {
			pathList = new JoddArrayList<>();
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
			sortComparators = new ArrayList<>(4);
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
	 * Sorts files by file name, using <b>natural</b> sort.
	 */
	public FindFile sortByName() {
		addComparator(new FileNameComparator(true));
		return this;
	}

	/**
	 * Sorts files by file names descending, using <b>natural</b> sort.
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

	public static class FolderFirstComparator implements Comparator<File>, Serializable {
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

	public static class FileNameComparator implements Comparator<File>, Serializable {
		protected final int order;
		protected NaturalOrderComparator<String> naturalOrderComparator = new NaturalOrderComparator<>(true, true);

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

	public static class FileExtensionComparator implements Comparator<File>, Serializable {
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

	public static class FileLastModifiedTimeComparator implements Comparator<File>, Serializable {
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
