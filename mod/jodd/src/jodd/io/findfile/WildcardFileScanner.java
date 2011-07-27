// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;


import jodd.io.FileNameUtil;
import jodd.util.Wildcard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Wildcard file scanner.
 */
public class WildcardFileScanner extends FileScanner {

	protected final String wildcard;
	protected boolean usePathWildcards;
	protected List<File> files;

	public WildcardFileScanner(String wildcard) {
		files = new ArrayList<File>();
		this.wildcard = wildcard;
	}

	public WildcardFileScanner(String wildcard, boolean usePathWildcards) {
		this.wildcard = wildcard;
		this.usePathWildcards = usePathWildcards;
	}

	public boolean isUsePathWildcards() {
		return usePathWildcards;
	}

	/**
	 * Set path wildcard matching algorithm.
	 */
	public void setUsePathWildcards(boolean usePathWildcards) {
		this.usePathWildcards = usePathWildcards;
	}

	@Override
	protected void onFile(File file) {
		String path = file.getAbsolutePath();
		path = FileNameUtil.separatorsToUnix(path);

		boolean match = usePathWildcards ?
				Wildcard.matchPath(path, wildcard) :
				Wildcard.match(path, wildcard) ;


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
