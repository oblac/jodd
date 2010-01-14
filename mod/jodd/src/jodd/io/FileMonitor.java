// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.
package jodd.io;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashSet;

/**
 * Monitoring disk files changes. Change event is broadcasted to all registered listeners.
 *
 * @see jodd.io.FileChangeListener
 */
public class FileMonitor {

	protected Timer timer;
	protected final HashMap<File, Long> files;
	protected final Collection<FileChangeListener> listeners;

	/**
	 * Creates a file monitor instance with specified polling interval in ms.
	 */
	public FileMonitor(long pollingInterval) {
		files = new HashMap<File, Long>();
		listeners = new HashSet<FileChangeListener>();
		timer = new Timer(true);
		timer.schedule(new FileMonitorNotifier(), 0, pollingInterval);
	}

	/**
	 * Stops the file monitor polling.
	 */
	public void stop() {
		timer.cancel();
	}


	/**
	 * Adds file to listen for. File may be any file or folder,
	 * including a non-existing file in the case where the
	 * creating of the file is to be trapped.
	 */
	public void monitorFile(File file) {
		if (files.containsKey(file) == false) {
			long modifiedTime = file.exists() ? file.lastModified() : -1;
			files.put(file, new Long(modifiedTime));
		}
	}

	/**
	 * Removes specified file for listening.
	 */
	public void releaseFile(File file) {
		files.remove(file);
	}


	/**
	 * Adds listener to this file monitor.
	 */
	public void registerListener(FileChangeListener fileChangeListener) {
		for (FileChangeListener listener : listeners) {
			if (listener == fileChangeListener) {
				return;
			}
		}
		listeners.add(fileChangeListener);
	}


	/**
	 * Removes listener from this file monitor.
	 */
	public void removeListener(FileChangeListener fileChangeListener) {
		Iterator<FileChangeListener> i = listeners.iterator();
		while(i.hasNext()) {
			FileChangeListener listener = i.next();
			if (listener == fileChangeListener) {
				i.remove();
				break;
			}
		}
	}

	/**
	 * Removes all file listeners/
	 */
	public void removeAllListeners() {
		listeners.clear();
	}


	/**
	 * Actual file monitor timer task.
	 */
	protected class FileMonitorNotifier extends TimerTask {

		@Override
		public void run() {
			for (File file : files.keySet()) {
				long lastModifiedTime = files.get(file).longValue();
				long newModifiedTime = file.exists() ? file.lastModified() : -1;

				// check if file has been changed
				if (newModifiedTime != lastModifiedTime) {

					// store new modified time
					files.put(file, new Long(newModifiedTime));

					// notify listeners
					for (FileChangeListener listener : listeners) {
						listener.onFileChange(file);
					}
				}
			}
		}
	}


}