// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.watch;

import java.io.File;

/**
 * Listener for {@link jodd.io.watch.DirWatcher}.
 */
public interface DirWatcherListener {

	/**
	 * Invoked when one of the monitored files is created, deleted or modified.
	 */
	void onChange(File file, DirWatcher.Event event);

}