// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.JoddCore;

/**
 * {@link FileUtil File utilities} parameters.
 */
public class FileUtilParams implements Cloneable {

	protected boolean preserveDate = true;				// should destination file have the same timestamp as source
	protected boolean overwrite = true;					// overwrite existing destination
	protected boolean createDirs = true;				// create missing subdirectories of destination
	protected boolean recursive = true;					// use recursive directory copying and deleting
	protected boolean continueOnError = true;			// don't stop on error and continue job as much as possible
	protected String encoding = JoddCore.encoding;		// default encoding for reading/writing strings


	public boolean isPreserveDate() {
		return preserveDate;
	}
	public FileUtilParams setPreserveDate(boolean preserveDate) {
		this.preserveDate = preserveDate;
		return this;
	}

	public boolean isOverwrite() {
		return overwrite;
	}
	public FileUtilParams setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
		return this;
	}

	public boolean isCreateDirs() {
		return createDirs;
	}
	public FileUtilParams setCreateDirs(boolean createDirs) {
		this.createDirs = createDirs;
		return this;
	}

	public boolean isRecursive() {
		return recursive;
	}
	public FileUtilParams setRecursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}

	public boolean isContinueOnError() {
		return continueOnError;
	}
	public FileUtilParams setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
		return this;
	}


	public String getEncoding() {
		return encoding;
	}
	public FileUtilParams setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	// ------------------------------------------------------------ clone

	@Override
	public FileUtilParams clone() throws CloneNotSupportedException {
		return (FileUtilParams) super.clone();
	}

}