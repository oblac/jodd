// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * {@link FindFile} that uses file filters.
 */
public class FilterFindFile extends FindFile {


	protected List<FileFilter> ffilters;

	/**
	 * Adds array of file filters.
	 */
	public FindFile addFilters(FileFilter[] ff) {
		if (ffilters == null) {
			ffilters = new ArrayList<FileFilter>(ff.length);
		}
		ffilters.addAll(Arrays.asList(ff));
		return this;
	}

	/**
	 * Adds a file filter.
	 */
	public FindFile addFilter(FileFilter ff) {
		if (ffilters == null) {
			ffilters = new ArrayList<FileFilter>();
		}
		ffilters.add(ff);
		return this;
	}

	/**
	 * Called on each file entry (file or directory) and returns <code>true</code>
	 * if file passes search criteria.
	 */
	@Override
	protected boolean acceptFile(File currentFile) {
		if (ffilters != null) {
			for (FileFilter ff : ffilters) {
				if (ff.accept(currentFile) == false) {
					return false;
				}
			}
		}
		return true;
	}

}
