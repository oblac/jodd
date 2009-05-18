// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.io;

import jodd.util.StringPool;

/**
 * {@link FileUtil File utilities} parameters.
 */
public class FileUtilParams implements Cloneable {

	protected boolean preserveDate = true;			// should destination file have the same timestamp as source
	protected boolean overwrite = true;				// overwrite existing destination
	protected boolean createDirs = true;			// create missing subdirectories of destination
	protected boolean recursive = true;				// use recursive directory copying and deleting
	protected boolean continueOnError = true;		// don't stop on error and continue job as much as possible
	protected String encoding = StringPool.UTF_8;	// default encoding for reading/writing strings


	public boolean isPreserveDate() {
		return preserveDate;
	}
	public void setPreserveDate(boolean preserveDate) {
		this.preserveDate = preserveDate;
	}
	public FileUtilParams preserveDate(boolean preserveDate) {
		this.preserveDate = preserveDate;
		return this;
	}

	public boolean isOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	public FileUtilParams overwrite(boolean overwrite) {
		this.overwrite = overwrite;
		return this;
	}

	public boolean isCreateDirs() {
		return createDirs;
	}
	public void setCreateDirs(boolean createDirs) {
		this.createDirs = createDirs;
	}
	public FileUtilParams createDirs(boolean createDirs) {
		this.createDirs = createDirs;
		return this;
	}

	public boolean isRecursive() {
		return recursive;
	}
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	public FileUtilParams recursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}

	public boolean isContinueOnError() {
		return continueOnError;
	}
	public void setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
	}
	public FileUtilParams continueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
		return this;
	}


	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public FileUtilParams encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	// ------------------------------------------------------------ clone

	@Override
	public FileUtilParams clone() throws CloneNotSupportedException {
		return (FileUtilParams) super.clone();
	}

}
