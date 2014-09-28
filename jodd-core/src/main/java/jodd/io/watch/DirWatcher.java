// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.watch;

import jodd.mutable.MutableLong;
import jodd.util.StringPool;
import jodd.util.Wildcard;

import java.io.File;
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
	protected HashMap<File, MutableLong> map = new HashMap<File, MutableLong>();
	protected int filesCount;
	protected List<DirWatcherListener> listeners = new ArrayList<DirWatcherListener>();
	protected String[] patterns;

	public DirWatcher(String dir) {
		this(dir, null);
	}

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

	// ---------------------------------------------------------------- accept

	protected boolean ignoreDotFiles = true;

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

	/**
	 * Enables or disables if dot files should be watched.
	 */
	public DirWatcher ignoreDotFiles(boolean ignoreDotFiles) {
		this.ignoreDotFiles = ignoreDotFiles;
		return this;
	}

	// ---------------------------------------------------------------- watch file

	protected File watchFile;
	protected long watchFileLastAccessTime;

	/**
	 * Enables usage of default watch file.
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
			throw new DirWatcherException("Invalid watch file: " + name);
		}

		watchFileLastAccessTime = watchFile.lastModified();

		return this;
	}


	// ---------------------------------------------------------------- timer

	protected Timer timer;

	/**
	 * Starts the dir watcher.
	 */
	public void start(long pollingInterval) {
		if (timer == null) {
			init();
			timer = new Timer(true);
			timer.schedule(new WatchTask(), 0, pollingInterval);
		}
	}
	/**
	 * Stops the dir watcher.
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
				deletedFiles = new HashSet<File>(map.keySet());
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

				if (currentTime == null) {
					// new file
					map.put(file, new MutableLong(file.lastModified()));
					onChange(file, Event.CREATED);
				}
				else if (currentTime.longValue() != file.lastModified()) {
					// modified file
					currentTime.setValue(file.lastModified());
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