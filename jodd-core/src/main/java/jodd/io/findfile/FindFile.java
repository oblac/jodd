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

import jodd.inex.InExRules;
import jodd.io.FileNameUtil;
import jodd.io.FileUtil;
import jodd.util.MultiComparator;
import jodd.util.StringUtil;
import jodd.util.function.Consumers;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Generic iterative file finder. Searches all files on specified search path.
 * By default, it starts in whitelist mode, where everything is excluded.
 * To search, you need to explicitly set include patterns. If no pattern is
 * set, then the search starts in blacklist mode, where everything is included (search all).
 *
 * @see WildcardFindFile
 * @see RegExpFindFile
 * @see InExRules
 */
public class FindFile implements Iterable<File> {

	public static WildcardFindFile createWildcardFF() {
		return new WildcardFindFile();
	}
	public static RegExpFindFile createRegExpFF() {
		return new RegExpFindFile();
	}
	public static FindFile create() {
		return new FindFile();
	}

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

	/**
	 * Activates recursive search.
	 */
	public FindFile recursive(final boolean recursive) {
		this.recursive = recursive;
		return this;
	}

	/**
	 * Include directories in search.
	 */
	public FindFile includeDirs(final boolean includeDirs) {
		this.includeDirs = includeDirs;
		return this;
	}

	/**
	 * Include files in search.
	 */
	public FindFile includeFiles(final boolean includeFiles) {
		this.includeFiles = includeFiles;
		return this;
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
	public FindFile walking(final boolean walking) {
		this.walking = walking;
		return this;
	}

	/**
	 * Set {@link Match matching type}.
	 */
	public FindFile matchType(final Match match) {
		this.matchType = match;
		return this;
	}

	public FindFile matchOnlyFileName() {
		this.matchType = Match.NAME;
		return this;
	}
	public FindFile matchFullPath() {
		this.matchType = Match.FULL_PATH;
		return this;
	}
	public FindFile matchRelativePath() {
		this.matchType = Match.RELATIVE_PATH;
		return this;
	}

	// ---------------------------------------------------------------- consumer

	private Consumers<File> consumers;

	/**
	 * Registers file consumer
	 */
	public FindFile onFile(final Consumer<File> fileConsumer) {
		if (consumers == null) {
			consumers = Consumers.of(fileConsumer);
		}
		else {
			consumers.add(fileConsumer);
		}
		return this;
	}

	// ---------------------------------------------------------------- search path

	/**
	 * Specifies single search path.
	 */
	public FindFile searchPath(final File searchPath) {
		addPath(searchPath);
		return this;
	}

	/**
	 * Specifies a set of search paths.
	 */
	public FindFile searchPath(final File... searchPath) {
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
	public FindFile searchPath(final String searchPath) {
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
	public FindFile searchPaths(final String... searchPaths) {
		for (String searchPath : searchPaths) {
			searchPath(searchPath);
		}
		return this;
	}

	/**
	 * Specifies the search path. Throws an exception if URI is invalid.
	 */
	public FindFile searchPath(final URI searchPath) {
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
	public FindFile searchPaths(final URI... searchPath) {
		for (URI uri : searchPath) {
			searchPath(uri);
		}
		return this;
	}

	/**
	 * Specifies the search path. Throws an exception if URL is invalid.
	 */
	public FindFile searchPath(final URL searchPath) {
		File file = FileUtil.toContainerFile(searchPath);
		if (file == null) {
			throw new FindFileException("URL error: " + searchPath);
		}
		addPath(file);
		return this;
	}

	/**
	 * Specifies the search path.
	 */
	public FindFile searchPaths(final URL... searchPath) {
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

		public FilesIterator(final File folder) {
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

		public FilesIterator(final String[] fileNames) {
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

	protected final InExRules<String, String, ?> rules = createRulesEngine();

	/**
	 * Creates rule engine.
	 */
	protected InExRules<String, String, ?> createRulesEngine() {
		return new InExRules<>();
	}

	/**
	 * Defines include pattern.
	 */
	public FindFile include(final String pattern) {
		rules.include(pattern);
		return this;
	}

	/**
	 * Defines include patterns.
	 */
	public FindFile include(final String... patterns) {
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
	public FindFile exclude(final String pattern) {
		rules.exclude(pattern);
		return this;
	}

	/**
	 * Defines exclude patterns.
	 */
	public FindFile exclude(final String... patterns) {
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
	 * @see InExRules
	 */
	protected boolean acceptFile(final File file) {
		String matchingFilePath = getMatchingFilePath(file);

		if (rules.match(matchingFilePath)) {
			if (consumers != null) {
				consumers.accept(file);
			}
			return true;
		}
		return false;
	}

	/**
	 * Resolves file path depending on {@link Match matching type}
	 * Returned path is formatted in unix style.
	 */
	protected String getMatchingFilePath(final File file) {

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
	protected void addPath(final File path) {
		if (!path.exists()) {
			return;
		}
		if (pathList == null) {
			pathList = new LinkedList<>();
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
		rules.detectMode();

		todoFiles = new LinkedList<>();
		todoFolders = new LinkedList<>();

		if (pathList == null) {
			pathList = new LinkedList<>();
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
	@Override
	public Iterator<File> iterator() {

		return new Iterator<File>() {
			private File nextFile;

			@Override
			public boolean hasNext() {
				nextFile = nextFile();
				return nextFile != null;
			}

			@Override
			public File next() {
				if (nextFile == null) {
					throw new NoSuchElementException();
				}
				return nextFile;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	// ---------------------------------------------------------------- sort

	protected List<Comparator<File>> sortComparators;

	protected void addComparator(final Comparator<File> comparator) {
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
	public FindFile sortWith(final Comparator<File> fileComparator) {
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

}
