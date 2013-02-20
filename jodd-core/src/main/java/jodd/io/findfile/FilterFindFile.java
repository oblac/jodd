// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io.findfile;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * {@link FindFile} that uses file filters.
 */
public class FilterFindFile extends FindFile<FilterFindFile> {

	protected List<FileFilter> fileFilterList;

	/**
	 * Adds array of file filters.
	 */
	public FilterFindFile addFilters(FileFilter... filters) {
		if (fileFilterList == null) {
			fileFilterList = new ArrayList<FileFilter>(filters.length);
		}
		fileFilterList.addAll(Arrays.asList(filters));
		return this;
	}

	/**
	 * Adds a file filter.
	 */
	public FilterFindFile addFilter(FileFilter filter) {
		if (fileFilterList == null) {
			fileFilterList = new ArrayList<FileFilter>();
		}
		fileFilterList.add(filter);
		return this;
	}

	/**
	 * Called on each file entry (file or directory) and returns <code>true</code>
	 * if file passes search criteria.
	 */
	@Override
	protected boolean acceptFile(File file) {
		if (fileFilterList != null) {
			for (FileFilter ff : fileFilterList) {
				if (ff.accept(file) == false) {
					return false;
				}
			}
		}
		return true;
	}

}