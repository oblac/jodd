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

package jodd.io.watch;

import jodd.io.FileUtil;
import jodd.mutable.MutableLong;
import jodd.util.StringPool;
import jodd.util.Wildcard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DirWatcher {

	/**
	 * Events that describes file change.
	 */
	public enum Event {
		CREATED,
		DELETED,
		MODIFIED
	}

	protected final File dir;
	protected HashMap<File, MutableLong> map = new HashMap<>();
	protected int filesCount;
	protected List<DirWatcherListener> listeners = new ArrayList<>();
	protected String[] patterns;

	/**
	 * Creates new watcher on specified directory.
	 * You can set file patterns {@link #monitor(String...) later}.
	 */
	public DirWatcher(String dir) {
		this(dir, null);
	}

	/**
	 * Creates new watched on specified directory with given set of
	 * wildcard patterns for file names.
	 */
	public DirWatcher(String dirName, String... patterns) {
		this.dir = new File(dirName);

		if (!dir.exists() || !dir.isDirectory()) {
			throw new DirWatcherException("Invalid watch dir: " + dirName);
		}

		this.patterns = patterns;
	}

	/**
	 * Initializes dir watcher by reading all files
	 * from watched folder.
	 */
	protected void init() {
		File[] filesArray = dir.listFiles();

		filesCount = 0;

		if (filesArray != null) {
			filesCount = filesArray.length;

			for (File file : filesArray) {
				if (!acceptFile(file)) {
					continue;
				}

				map.put(file, new MutableLong(file.lastModified()));
			}
		}
	}

	// ---------------------------------------------------------------- flags

	protected boolean ignoreDotFiles = true;
	protected boolean startBlank = false;

	/**
	 * Enables or disables if dot files should be watched.
	 */
	public DirWatcher ignoreDotFiles(boolean ignoreDotFiles) {
		this.ignoreDotFiles = ignoreDotFiles;
		return this;
	}

	/**
	 * Defines if watcher should start blank and consider all present
	 * files as {@link jodd.io.watch.DirWatcher.Event#CREATED created}.
	 * By default all existing files will consider as existing ones.
	 */
	public DirWatcher startBlank(boolean startBlank) {
		this.startBlank = startBlank;
		return this;
	}

	/**
	 * Defines patterns to scan.
	 */
	public DirWatcher monitor(String... patterns) {
		this.patterns = patterns;
		return this;
	}

	// ---------------------------------------------------------------- accept

	/**
	 * Accepts if a file is going to be watched.
	 */
	protected boolean acceptFile(File file) {
		if (!file.isFile()) {
			return false;			// ignore non-files
		}

		String fileName = file.getName();

		if (ignoreDotFiles) {
			if (fileName.startsWith(StringPool.DOT)) {
				return false;        // ignore hidden files
			}
		}

		if (patterns == null) {
			return true;
		}

		return Wildcard.matchOne(fileName, patterns) != -1;
	}

	// ---------------------------------------------------------------- watch file

	protected File watchFile;
	protected long watchFileLastAccessTime;

	/**
	 * Enables usage of default watch file (".watch.ready").
	 */
	public DirWatcher useWatchFile() {
		return useWatchFile(".watch.ready");
	}

	/**
	 * Enables usage of provided watch file.
	 */
	public DirWatcher useWatchFile(String name) {
		watchFile = new File(dir, name);

		if (!watchFile.isFile() || !watchFile.exists()) {
			try {
				FileUtil.touch(watchFile);
			} catch (IOException ioex) {
				throw new DirWatcherException("Invalid watch file: " + name, ioex);
			}
		}

		watchFileLastAccessTime = watchFile.lastModified();

		return this;
	}


	// ---------------------------------------------------------------- timer

	protected Timer timer;

	/**
	 * Starts the watcher.
	 */
	public void start(long pollingInterval) {
		if (timer == null) {
			if (!startBlank) {
				init();
			}
			timer = new Timer(true);
			timer.schedule(new WatchTask(), 0, pollingInterval);
		}
	}
	/**
	 * Stops the watcher.
	 */
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	// ---------------------------------------------------------------- timer

	public class WatchTask extends TimerTask {

		protected boolean running;

		public final void run() {
			if (running) {
				// if one task takes too long, don't fire another one
				return;
			}
			running = true;

			if (watchFile != null) {
				// wait for watch file changes
				long last = watchFile.lastModified();

				if (last <= watchFileLastAccessTime) {
					running = false;
					return;
				}
				watchFileLastAccessTime = last;
			}

			// scan!

			File[] filesArray = dir.listFiles();

			if (filesArray == null) {
				running = false;
				return;
			}

			HashSet<File> deletedFiles = null;

			// check if there might be a delete file
			if (filesArray.length < filesCount) {
				deletedFiles = new HashSet<>(map.keySet());
			}

			filesCount = filesArray.length;

			// scan the files and check for modification/addition
			for (File file : filesArray) {
				if (!acceptFile(file)) {
					continue;
				}

				MutableLong currentTime = map.get(file);

				if (deletedFiles != null) {
					deletedFiles.remove(file);
				}

				long lastModified = file.lastModified();

				if (currentTime == null) {
					// new file
					map.put(file, new MutableLong(lastModified));
					onChange(file, Event.CREATED);
				}
				else if (currentTime.longValue() != lastModified) {
					// modified file
					currentTime.setValue(lastModified);
					onChange(file, Event.MODIFIED);
				}
			}

			// check for deleted files
			if (deletedFiles != null) {
				for (File deletedFile : deletedFiles) {
					map.remove(deletedFile);
					onChange(deletedFile, Event.DELETED);
				}
			}

			// stop running
			running = false;
		}
	}

	/**
	 * Triggers listeners on file change.
	 */
	protected void onChange(File file, Event event) {
		for (DirWatcherListener listener : listeners) {
			listener.onChange(file, event);
		}
	}

	// ---------------------------------------------------------------- listeners

	/**
	 * Registers {@link jodd.io.watch.DirWatcherListener listener}.
	 */
	public void register(DirWatcherListener dirWatcherListener) {
		if (!listeners.contains(dirWatcherListener)) {
			listeners.add(dirWatcherListener);
		}
	}

	/**
	 * Removes registered {@link jodd.io.watch.DirWatcherListener listener}.
	 */
	public void remove(DirWatcherListener dirWatcherListener) {
		listeners.remove(dirWatcherListener);
	}

}