// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.filter;

import java.io.File;

abstract class FileFilterBase implements FileFilterEx {

	/**
	 * Tests whether or not the specified abstract pathname should be
	 * included in a pathname list.
	 *
	 * @param  file  The abstract pathname to be tested
	 * @return  <code>true</code> if and only if <code>pathname</code>
	 *          should be included
	 */
	public boolean accept(File file) {
		return accept(file.getParentFile(), file.getName());
	}

	/**
	 * Tests if a specified file should be included in a file list.
	 *
	 * @param   dir    the directory in which the file was found.
	 * @param   name   the name of the file.
	 * @return  <code>true</code> if and only if the name should be
	 * included in the file list; <code>false</code> otherwise.
	 */
	 public boolean accept(File dir, String name) {
		return accept(new File(dir, name));
	}

}
