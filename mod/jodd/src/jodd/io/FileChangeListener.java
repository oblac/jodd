// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import java.io.File;

/**
 * Interface for listening to disk file changes.
 * @see jodd.io.FileMonitor
 */
public interface FileChangeListener {

	/**
	 * Invoked when one of the monitored files is created, deleted or modified.
	 */
	void onFileChange(File file);

}
