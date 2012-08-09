// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.filter;

import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Join of two interfaces: <code>FileFilter</code> and <code>FilenameFilter</code>.
 * See joint implementation in {@link FileFilterBase}.
 */
public interface FileFilterEx extends FileFilter, FilenameFilter {
}
