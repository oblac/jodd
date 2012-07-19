// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;


import jodd.io.FileNameUtil;
import jodd.util.Wildcard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Wildcard pattern file scanner.
 */
public class WildcardFileScanner extends FileScanner {

	protected final String pattern;
	protected List<File> files;

	public WildcardFileScanner(String pattern) {
		files = new ArrayList<File>();
		this.pattern = pattern;
	}

	@Override
	protected void onFile(File file) {
		String path = file.getAbsolutePath();
		path = FileNameUtil.separatorsToUnix(path);

		boolean match = Wildcard.matchPath(path, pattern);

		if (match) {
			files.add(file);
		}
	}

	/**
	 * Returns matched files.
	 */
	public List<File> getMatchedFiles() {
		return files;
	}

	/**
	 * Lists all matched files.
	 */
	public List<File> list(File root) {
		files.clear();
		scan(root);
		return files;
	}

	/**
	 * Lists all matched files.
	 */
	public List<File> list(String root) {
		files.clear();
		scan(root);
		return files;
	}
}
